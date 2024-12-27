package org.pieski.task4;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import org.pieski.task2.MatrixMultiplier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

record RangedFuture<T>(Future<T> future, int rangeStart, int rangeEnd) {}

public class MatrixMultiplicationDistributor implements MatrixMultiplier {
  private double[][] result;
  private double[][] matA;
  private double[][] matB;

  private HazelcastInstance instance;
  private String executorName;

  public MatrixMultiplicationDistributor(double[][] matA, double[][] matB, HazelcastInstance instance, String executorName) {
    this.result = MatrixMultiplier.generateEmpty(matA.length);
    this.matA = matA;
    this.matB = matB;
    this.instance = instance;
    this.executorName = executorName;
  }
  public double[][] multiply() {
    IExecutorService executor = instance.getExecutorService(executorName);

    List<RangedFuture<double[][]>> futures = new ArrayList<>();

    int numCols = matB[0].length;
    int numMembers = instance.getCluster().getMembers().size();
    int blockSize = numCols / numMembers;

    System.out.println("DISTRIBUTOR  STARTING DISTRIBUTION");
    int columnStart = 0;
    for (Member member : instance.getCluster().getMembers()) {
      int columnEnd = columnStart + blockSize;
      if (columnEnd > numCols) columnEnd = numCols;
      Callable<double[][]> task = new MultiplierNode(matA, matB, columnStart, columnEnd);
      Future<double[][]> future = executor.submitToMember(task, member);
      RangedFuture<double[][]> rangedFuture = new RangedFuture<>(future, columnStart, columnEnd);
      futures.add(rangedFuture);
      columnStart += blockSize;
    }
    long startTime = System.currentTimeMillis();
    System.out.println("DISTRIBUTOR ENDED DISTRIBUTION, WAITING FOR RESULTS");

    // Collect results
    for (RangedFuture<double[][]> future : futures) {
      try {
        double[][] partResult = future.future().get();
        int futColumnStart = future.rangeStart();
        int futColumnEnd = future.rangeEnd();
        for (int i = 0; i < result.length; i++) {
          if (futColumnEnd - futColumnStart >= 0)
            System.arraycopy(partResult[i], futColumnStart, result[i], futColumnStart, futColumnEnd - futColumnStart);
        }
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }
    System.out.println("DISTRIBUTOR GOT THE RESULTS");
    long endTime = System.currentTimeMillis();
    System.out.println("Finished in: " + (endTime - startTime) + "ms");
    instance.shutdown();
    return result;
  }
}