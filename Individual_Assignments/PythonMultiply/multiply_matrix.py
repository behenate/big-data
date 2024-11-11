def matrix_multiply(matrix1, matrix2, result):
    rows_matrix1 = len(matrix1)
    cols_matrix1 = len(matrix1[0])
    rows_matrix2 = len(matrix2)
    cols_matrix2 = len(matrix2[0])

    # Initialize result matrix with zeros

    for i in range(rows_matrix1):
        for j in range(cols_matrix2):
            for k in range(cols_matrix1):
                result[i][j] += matrix1[i][k] * matrix2[k][j]

    return result

if __name__ == "__main__":
    # Example Usage:
    matrix1 = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
    matrix2 = [[1, 0, 0], [0, 1, 0], [0, 0, 1]]

    print(matrix_multiply(matrix1, matrix2))