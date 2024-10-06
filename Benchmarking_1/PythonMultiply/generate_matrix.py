import numpy as np
import multiply_matrix

def generate_matrices(size):
    # Generate matrices with random integers
    matrix1 = np.random.randint(-100, 100, size=(size, size))
    matrix2 = np.random.randint(-100, 100, size=(size, size))

    return matrix1, matrix2

if __name__ == "__main__":
    matrix1, matrix2 = generate_matrices(3, 3, 3, 3)
    print('Matrix 1: \n', matrix1)
    print('Matrix 2: \n', matrix2)
    result = multiply_matrix.matrix_multiply(matrix1, matrix2)

    print ('Result: \n', result)