package org.pieski.task3;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.pieski.task2.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 0, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class Task3Main {
  // "1", "5", "10", "50", "100", "200", "300", "500", "1000", "1500", "2000"
  @Param({"2500"})
  public int size = 1000;
  @Param({"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"})
  public int numThreads = 0; // 0 For max amount of threads
  private double[][] matrix1;
  private double[][] matrix2;

  private MatrixMultiplier standardMatrixMultiplier;
  private MatrixMultiplier blockedColumnMultiplier;
  private MatrixMultiplier streamMultiplier;
  private MatrixMultiplier executorMultiplier;
  private MatrixMultiplier multithreadedBlockColumnMultiplier;

  @Setup(Level.Trial)
  public void setupMatrix() {
    matrix1 = generateMatrix(size, false);
    matrix2 = generateMatrix(size, false);
    standardMatrixMultiplier = new StandardMatrixMultiplier(matrix1, matrix2);
    blockedColumnMultiplier = new BlockedColumnMultiplier(matrix1, matrix2);
    streamMultiplier = new MultithreadedStreamMultiplier(matrix1, matrix2);
    executorMultiplier = new ExecutorMultiplier(matrix1, matrix2, numThreads);
    multithreadedBlockColumnMultiplier = new MultithreadedBlockedColumnMultiplier(matrix1, matrix2, numThreads);
  }

  //  @Benchmark
//  public double[][] standardMultiply() {
//    return standardMatrixMultiplier.multiply();
//  }
//
//  @Benchmark
//  public double[][] blockedColumnMultiply() {
//    return blockedColumnMultiplier.multiply();
//  }
//
  @Benchmark
  public double[][] multithreadedStreamMultiply() {
    return streamMultiplier.multiply();
  }

  @Benchmark
  public double[][] multithreadedExecutorMultiply() {
    return executorMultiplier.multiply();
  }

  @Benchmark
  public double[][] multithreadedBlockedColumnMultiply() {
    return multithreadedBlockColumnMultiplier.multiply();
  }

  public static double[][] generateMatrix(int size, boolean empty) {
    Random random = new Random();
    double[][] matrix = new double[size][size];

    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        if (empty) {
          matrix[i][j] = .0;
        } else {
          matrix[i][j] = random.nextDouble();
        }
      }
    }
    return matrix;
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(".")
        .forks(1)
        .build();

    new Runner(opt).run();
  }
}