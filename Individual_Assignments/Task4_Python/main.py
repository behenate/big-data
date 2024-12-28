from time import sleep

import hazelcast
import numpy as np
import base64
import pickle

def distribute_data(matrix_a, matrix_b, hz_map):
    """
    Serialize and distribute parts of matrix A and the whole matrix B to the Hazelcast map,
    using base64 encoding to handle binary data safely.
    """
    for i in range(matrix_a.shape[0]):
        # Serialize, encode, and store the data
        serialized_data = pickle.dumps(matrix_a[i, :])
        encoded_data = base64.b64encode(serialized_data).decode('utf-8')
        hz_map.put(f"row_{i}", encoded_data)

    # Do the same for the entire matrix_b
    serialized_b = pickle.dumps(matrix_b)
    encoded_b = base64.b64encode(serialized_b).decode('utf-8')
    hz_map.put("matrix_b", encoded_b)

def matrix_multiply_worker(map_name, row_key, hazelcast_instance):
    """
    Fetch a row and matrix B, decode, deserialize, perform multiplication,
    and store the result back to Hazelcast.
    """
    hz_map = hazelcast_instance.get_map(map_name).blocking()
    encoded_matrix_b = hz_map.get("matrix_b")
    encoded_row = hz_map.get(row_key)

    # Decode and deserialize data
    matrix_b = pickle.loads(base64.b64decode(encoded_matrix_b))
    row = pickle.loads(base64.b64decode(encoded_row))

    result_row = np.dot(row, matrix_b)
    # Serialize and encode the result
    result_serialized = pickle.dumps(result_row)
    result_encoded = base64.b64encode(result_serialized).decode('utf-8')
    hz_map.put(f"result_{row_key}", result_encoded)


def collect_results(num_rows, hz_map):
    """
    Collect results from Hazelcast map, decode and deserialize them, and construct the result matrix.
    """
    result_matrix = []
    for i in range(num_rows):
        encoded_result_row = hz_map.get(f"result_row_{i}")
        print(encoded_result_row)
        result_row = pickle.loads(base64.b64decode(encoded_result_row))
        result_matrix.append(result_row)
    return np.array(result_matrix)


def matrix_multiply_task(client, row_key, map_name):
    hz_map = client.get_map(map_name).blocking()

    encoded_matrix_b = hz_map.get("matrix_b")
    encoded_row = hz_map.get(row_key)

    matrix_b = pickle.loads(base64.b64decode(encoded_matrix_b))
    row = pickle.loads(base64.b64decode(encoded_row))

    result_row = np.dot(row, matrix_b)

    result_serialized = pickle.dumps(result_row)
    result_encoded = base64.b64encode(result_serialized).decode('utf-8')

    hz_map.put(f"result_{row_key}", result_encoded)

if __name__ == "__main__":
    # Initialize Hazelcast client
    client = hazelcast.HazelcastClient()

    queue = client.get_queue("my-distributed-queue").blocking()

    hz_map = client.get_map("matrix_map").blocking()

    size = 10
    # Matrices to multiply
    matrix_a = np.random.rand(size, size)
    matrix_b = np.random.rand(size, size)

    if queue.size() == 0:
        distribute_data(matrix_a, matrix_b, hz_map)
        for i in range(len(matrix_a)):
            print(f"Putting {i}")
            queue.put(i)


    # Invoke workers
    sleep(1)
    while queue.size() > 0:
        elem = queue.poll()
        print(f"Calculating {elem}")
        matrix_multiply_worker("matrix_map", f"row_{elem}", client)
    queue.clear()

    # Collect and print results
    result_matrix = collect_results(size, hz_map)
    print("Resultant Matrix:\n", result_matrix)

    # Shutdown client
    queue.clear()
    hz_map.clear()
    client.shutdown()