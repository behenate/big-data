package org.pieski.task3;

import org.pieski.task2.MatrixMultiplier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ExecutorMultiplier implements MatrixMultiplier {
  private double[][] result;
  private double[][] matA;
  private double[][] matB;
  private final int numThreads;

  public ExecutorMultiplier(double[][] matA, double[][] matB, int numThreads) {
    this.result = MatrixMultiplier.generateEmpty(matA.length);
    this.matA = matA;
    this.matB = matB;
    this.numThreads = numThreads;
  }

  @Override
  public double[][] multiply() {
    int m = matA.length;
    int p = matB[0].length;
    int numThreads = Math.max(1, this.numThreads);
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    if (this.numThreads == 0) {
      executor = Executors.newCachedThreadPool();
    }

    List<Future<?>> tasks = new ArrayList<>();
    for (int i = 0; i < m; i++) {
      int finalI = i;
      tasks.add(executor.submit(() -> {
        for (int j = 0; j < p; j++) {
          for (int k = 0; k < matA[0].length; k++) {
            result[finalI][j] += matA[finalI][k] * matB[k][j];
          }
        }
      }));
    }

    for (Future<?> task : tasks) {
      try {
        task.get();
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }
    executor.shutdown();

    return result;
  }
}

