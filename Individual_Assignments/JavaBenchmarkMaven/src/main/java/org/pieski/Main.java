package org.pieski;
import com.jmatio.types.MLSparse;
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
public class Main {
  @Param({ "1", "5", "10", "50", "100", "200", "300", "500", "1000", "1500", "2000"})
  public int size;
  private double[][] matrix1;
  private double[][] matrix2;
  private double[][] dupa;

  private StandardMatrixMultiplier standardMatrixMultiplier;
  private BlockedMultiplier blockedMultiplier;
  private BlockedColumnMultiplier blockedColumnMultiplier;


  @Setup(Level.Trial)
  public void setupMatrix() {
    matrix1 = generateMatrix(size, false);
    matrix2 = generateMatrix(size, false);
    standardMatrixMultiplier = new StandardMatrixMultiplier(matrix1, matrix2);
    blockedColumnMultiplier = new BlockedColumnMultiplier(matrix1, matrix2);
    blockedMultiplier = new BlockedMultiplier(matrix1, matrix2);
  }

  @Benchmark
  public double[][] standardMultiply() {
    return standardMatrixMultiplier.multiply();
  }

  @Benchmark
  public double[][] blockedColumnMultiply() {
    return blockedColumnMultiplier.multiply();
  }

  @Benchmark
  public double[][] blockedMultiply() {
    return blockedMultiplier.multiply();
  }

  public void sparseMultiply() {
    MLSparse mat = MatFileLoader.loadMc2Depi();
    SparseMultiplier sparseMult = new SparseMultiplier(mat, mat);
    System.out.println("MULTIPLYING!");
    sparseMult.multiply();
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