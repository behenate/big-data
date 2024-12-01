package org.pieski.task3;

import org.pieski.task2.MatrixMultiplier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MultithreadedBlockedColumnMultiplier implements MatrixMultiplier {
  private double[][] result;
  private double[][] matA;
  private double[][] matB;
  private final int numThreads;

  public MultithreadedBlockedColumnMultiplier(double[][] matA, double[][] matB, int numThreads) {
    this.result = MatrixMultiplier.generateEmpty(matA.length);
    this.matA = matA;
    this.matB = matB;
    this.numThreads = numThreads;
  }

  @Override
  public double[][] multiply() {
    int N = matA.length;
    int numThreads = this.numThreads == 0 ? Runtime.getRuntime().availableProcessors() : this.numThreads;
    int blockSize = N/numThreads + 1;
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);

    // Assign blocks of rows to different threads
    for (int row_block = 0; row_block < N; row_block += blockSize) {
      int finalRowBlock = row_block;
      executor.submit(() -> {
        for (int col_block = 0; col_block < N; col_block += blockSize) {
          for (int row = finalRowBlock; row < finalRowBlock + blockSize && row < N; row++) {
            for (int tile = 0; tile < N; tile += blockSize) {
              for (int tile_row = 0; tile_row < blockSize && tile_row + tile < N; tile_row++) {
                for (int idx = 0; idx < blockSize && col_block + idx < N; idx++) {
                  result[row][col_block + idx] +=
                      matA[row][tile + tile_row] *
                          matB[tile + tile_row][col_block + idx];
                }
              }
            }
          }
        }
      });
    }

    executor.shutdown();
    try {
      if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();  // preserve interruption status
    }
    return result;
  }
}

