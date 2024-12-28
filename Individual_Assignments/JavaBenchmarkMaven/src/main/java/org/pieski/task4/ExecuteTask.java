package org.pieski.task4;

import org.pieski.task2.MatrixMultiplier;

import java.util.concurrent.Callable;

public class ExecuteTask implements MatrixMultiplier, Callable<Void> {
  MatrixMultiplier internalMultiplier;
  private int columnStart;
  private int columnEnd;

  public ExecuteTask(int columnStart, int columnEnd) {
    MultiplicationTaskManager instance = MultiplicationTaskManager.getInstance();
    this.columnStart = columnStart;
    this.columnEnd = columnEnd;
    internalMultiplier = new MultithreadedLimitedBlockedColumnMultiplier(instance.matA, instance.matB, instance.result, 16, columnStart, columnEnd);
  }

  @Override
  public double[][] multiply() {
    double[][] result = internalMultiplier.multiply();
    return result;
  }

  @Override
  public Void call() throws Exception {
    long startTime = System.currentTimeMillis();
    double[][] result = internalMultiplier.multiply();
    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;
    System.out.println("Task finished, multiplying the range " + columnStart +"-"+ columnEnd + " took: " + duration + "ms");
    return null;
  }

  public int getColumnStart() {
    return columnStart;
  }

  public int getColumnEnd() {
    return columnEnd;
  }
}
