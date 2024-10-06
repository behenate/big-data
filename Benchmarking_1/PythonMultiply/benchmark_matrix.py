import numpy as np
import pytest
import os
from generate_matrix import generate_matrices
from multiply_matrix import matrix_multiply

@pytest.mark.parametrize('size', [1, 5, 10, 50, 100, 150, 200, 300])
def test_multiply_matrices(benchmark, size):
    # Generate random matrices
    matrix1, matrix2 = generate_matrices(size)
    # Initialize result matrix with zeros
    result = [[0 for _ in range(size)] for _ in range(size)]

    # Pass multiplication function to benchmark
    benchmark.pedantic(matrix_multiply, args= (matrix1, matrix2, result), iterations=5, rounds=2, warmup_rounds=1)