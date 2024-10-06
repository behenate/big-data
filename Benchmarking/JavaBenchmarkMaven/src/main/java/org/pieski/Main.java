package org.pieski;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 9, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class Main {
  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(".")
        .forks(1)
        .build();

    new Runner(opt).run();
  }
  @Param({ "1", "5", "10", "50" })
  public int size;
  private double[][] matrix1;
  private double[][] matrix2;
  private double[][] result;


  @Setup(Level.Trial)
  public void setupMatrix() {
    matrix1 = generateMatrix(size, false);
    matrix2 = generateMatrix(size, false);
    result = generateMatrix(size, true);
  }

  @Benchmark
  public double[][] multiplyMatrices(){
    return multiplyMatrix(matrix1, matrix2, result);
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

  public static double[][] multiplyMatrix(double[][] matrix1, double[][] matrix2, double[][] result) {
    int rowM1 = matrix1.length;
    int colM1 = matrix1[0].length;
    int colM2 = matrix2[0].length;

    for (int i = 0; i < rowM1; i++) {
      for (int j = 0; j < colM2; j++) {
        for (int k = 0; k < colM1; k++) {
          result[i][j] += matrix1[i][k] * matrix2[k][j];
        }
      }
    }

    return result;
  }
}