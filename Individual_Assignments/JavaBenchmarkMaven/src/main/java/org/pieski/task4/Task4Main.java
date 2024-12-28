package org.pieski.task4;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.pieski.task2.*;
import org.pieski.task3.MultithreadedBlockedColumnMultiplier;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 0, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class Task4Main {
  // "50", "100", "200", "300", "500", "1000", "1500", "2000", "2500", "3000", "4000", "4500"
  @Param({"5000"})
  public int size = 1000;
  public double[][] matrix1;
  public double[][] matrix2;
  Config config;

  HazelcastInstance instance1;
  MultithreadedBlockedColumnMultiplier MBCMultiplier;
  MatrixMultiplicationDistributor distributor;

  public Task4Main() {
    config = new XmlConfigBuilder(Task4Main.class.getResourceAsStream("/hazelcast.xml")).build();
    config.getSerializationConfig()
        .addSerializerConfig(
            new SerializerConfig()
                .setTypeClass(ExecuteTask.class)
                .setImplementation(new ExecuteTaskSerializer()
                )
        )
        .addSerializerConfig(
            new SerializerConfig()
                .setTypeClass(FinishExecutionTaskResult.class)
                .setImplementation(new FinishExecutionTaskResultSerializer()
                )
        )
        .addSerializerConfig(
            new SerializerConfig()
                .setTypeClass(PrepareExecutionTask.class)
                .setImplementation(new PrepareExecutionTaskSerializer()
                )
        );
  }

  @Setup(Level.Trial)
  public void setupMatrix() {
    instance1 = Hazelcast.newHazelcastInstance(config);
    matrix1 = generateMatrix(size, false);
    matrix2 = generateMatrix(size, false);
    MBCMultiplier = new MultithreadedBlockedColumnMultiplier(matrix1, matrix2, 16);
    distributor = new MatrixMultiplicationDistributor(matrix1, matrix2, instance1, "executor");

    while (instance1.getCluster().getMembers().size() < 2) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      System.out.println("Only one instance, waiting for more to register!");
    }

    distributor.prepare();
  }

  @Benchmark
  public double[][] distributedMBCMultiply() {
    return distributor.multiply();
  }

  @Benchmark
  public double[][] MBCMultiply() {
    return MBCMultiplier.multiply();
  }

  @TearDown
  public void tearDown() {
    instance1.shutdown();
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
        .include("Task4Main")
        .forks(0)
        .build();

    new Runner(opt).run();  }
}