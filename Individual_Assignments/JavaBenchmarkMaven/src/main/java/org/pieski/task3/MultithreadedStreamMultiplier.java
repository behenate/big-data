package org.pieski.task3;

import org.pieski.task2.MatrixMultiplier;
import org.pieski.task2.MultiplicationMethods;

import java.util.stream.IntStream;

public class MultithreadedStreamMultiplier implements MatrixMultiplier {
  private double[][] result;
  private double[][] matA;
  private double[][] matB;

  public MultithreadedStreamMultiplier(double[][] matA, double[][] matB) {
    this.result = MatrixMultiplier.generateEmpty(matA.length);
    this.matA = matA;
    this.matB = matB;
  }

  @Override
  public double[][] multiply() {
    int m = matA.length;
    int p = matB[0].length;

    IntStream.range(0, m).parallel().forEach(i -> {
      for (int j = 0; j < p; j++) {
        for (int k = 0; k < matA[0].length; k++) {
          result[i][j] += matA[i][k] * matB[k][j];
        }
      }
    });

    return result;
  }
}
