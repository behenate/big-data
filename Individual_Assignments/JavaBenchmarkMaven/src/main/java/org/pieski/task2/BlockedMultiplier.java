package org.pieski.task2;

public class BlockedMultiplier implements MatrixMultiplier {
  private double[][] result;
  private double[][] matA;
  private double[][] matB;

  public BlockedMultiplier(double[][] matA, double[][] matB) {
    this.result = MatrixMultiplier.generateEmpty(matA.length);
    this.matA = matA;
    this.matB = matB;
  }

  @Override
  public double[][] multiply() {
    MultiplicationMethods.blockedMultiply(matA, matB, result);
    return result;
  }
}
