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

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 0, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class Task4Main {
  // "1", "5", "10", "50", "100", "200", "300", "500", "1000", "1500", "2000"
  @Param({"2500"})
  public int size = 1000;
  @Param({"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"})
  public int numThreads = 0; // 0 For max amount of threads
  private double[][] matrix1;
  private double[][] matrix2;

  private MatrixMultiplier standardMatrixMultiplier;

//  @Setup(Level.Trial)
//  public void setupMatrix() {
//    matrix1 = generateMatrix(size, false);
//  }

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
    Config config = new Config();
    NetworkConfig network = config.getNetworkConfig();
    JoinConfig join = network.getJoin();
    join.getTcpIpConfig().setEnabled(false);

    MulticastConfig multicastConfig = join.getMulticastConfig();
    multicastConfig.setEnabled(true);
    multicastConfig.setMulticastGroup("224.2.2.3");
    multicastConfig.setMulticastPort(54327);


    config.getSerializationConfig().addSerializerConfig(
        new SerializerConfig()
            .setTypeClass(MultiplierNode.class)
            .setImplementation(new MultiplierNodeSerializer())
    );

    HazelcastInstance instance1 = Hazelcast.newHazelcastInstance(config);
    int cnt = 10;

    while (instance1.getCluster().getMembers().size() < 2 || cnt < 5) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      System.out.println("Only one instance, waiting for more to register!");
    }

    double[][] matrix1 = generateMatrix(64, false);
    double[][] matrix2 = generateMatrix(64, false);
    MatrixMultiplicationDistributor distributor = new MatrixMultiplicationDistributor(matrix1, matrix2, instance1, "executor");
    distributor.multiply();
  }
}