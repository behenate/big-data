package org.pieski.task4;

import org.pieski.task2.MatrixMultiplier;

import java.util.concurrent.Callable;

public class MultiplierNode implements MatrixMultiplier, Callable<double[][]> {
  MatrixMultiplier internalMultiplier;

  private double[][] result;
  private double[][] matA;
  private double[][] matB;
  private int columnStart;
  private int columnEnd;

  public MultiplierNode(double[][] matA, double[][] matB, double[][] result, int columnStart, int columnEnd) {
    this.matA = matA;
    this.matB = matB;
    this.result = result;
    this.columnStart = columnStart;
    this.columnEnd = columnEnd;
    internalMultiplier = new MultithreadedLimitedBlockedColumnMultiplier(matA, matB, 16, columnStart, columnEnd);
  }

  public MultiplierNode(double[][] matA, double[][] matB, int columnStart, int columnEnd) {
    this.result = MatrixMultiplier.generateEmpty(matA.length);
    this.matA = matA;
    this.matB = matB;
    this.columnStart = columnStart;
    this.columnEnd = columnEnd;
    internalMultiplier = new MultithreadedLimitedBlockedColumnMultiplier(matA, matB,16, columnStart, columnEnd);
  }
  @Override
  public double[][] multiply() {
    double[][] result = internalMultiplier.multiply();
    return result;
  }

  @Override
  public double[][] call() throws Exception {
    double[][] result =  internalMultiplier.multiply();
    return result;
  }

  public double[][] getMatA() {
    return matA;
  }

  public double[][] getMatB() {
    return matB;
  }

  public double[][] getResult() {
    return result;
  }

  public int getColumnStart() {
    return columnStart;
  }

  public int getColumnEnd() {
    return columnEnd;
  }
}
