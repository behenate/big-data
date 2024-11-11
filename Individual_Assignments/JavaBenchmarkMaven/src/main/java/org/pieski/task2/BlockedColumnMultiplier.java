package org.pieski.task2;

public class BlockedColumnMultiplier implements MatrixMultiplier {
  private double[][] result;
  private double[][] matA;
  private double[][] matB;

  public BlockedColumnMultiplier(double[][] matA, double[][] matB) {
    this.result = MatrixMultiplier.generateEmpty(matA.length);
    this.matA = matA;
    this.matB = matB;
  }

  @Override
  public double[][] multiply() {
    MultiplicationMethods.blockedColumnMultiply(matA, matB, result);
    return result;
  }
}
