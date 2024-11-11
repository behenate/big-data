package org.pieski.task2;

public interface MatrixMultiplier {
  double[][] multiply();

  static double[][] generateEmpty(int size) {
    double[][] result = new double[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; i < size; i++) {
        result[i][j] = 0;
      }
    }
    return result;
  }
}
