package org.pieski.task2;

public class StandardMatrixMultiplier implements MatrixMultiplier {
  private double[][] result;
  private double[][] matA;
  private double[][] matB;

  public StandardMatrixMultiplier(double[][] matA, double[][] matB) {
    this.result = MatrixMultiplier.generateEmpty(matA.length);
    this.matA = matA;
    this.matB = matB;
  }

  @Override
  public double[][] multiply() {
    return MultiplicationMethods.standardMultiply(matA, matB, result);
  }
}
