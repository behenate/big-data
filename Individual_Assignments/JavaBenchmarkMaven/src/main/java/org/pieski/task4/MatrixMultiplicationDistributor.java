package org.pieski.task4;

import com.hazelcast.cluster.Member;
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

    // PREPARE THE MEMBERS FOR EXECUTION
    List<Future<Void>> preparationFutures = new ArrayList<>();

    for (Member member : instance.getCluster().getMembers()) {
      PrepareExecutionTask task = new PrepareExecutionTask(matA,matB);
      Future<Void> future = executor.submitToMember(task, member);
      preparationFutures.add(future);
    }

    for (Future<Void> future : preparationFutures) {
      try {
        future.get();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }

    // MULTIPLICATION

    List<Future<Void>> multiplicationFutures = new ArrayList<>();

    int numCols = matB[0].length;
    int numMembers = instance.getCluster().getMembers().size();
    int blockSize = numCols / numMembers;

    System.out.println("DISTRIBUTOR  STARTING DISTRIBUTION");
    int columnStart = 0;
    for (Member member : instance.getCluster().getMembers()) {
      int columnEnd = columnStart + blockSize;
      if (columnEnd > numCols) columnEnd = numCols;
      Callable<Void> task = new ExecuteTask(columnStart, columnEnd);
      Future<Void> future = executor.submit(task);
      multiplicationFutures.add(future);
      columnStart += blockSize;
    }
    long startTime = System.currentTimeMillis();
    System.out.println("DISTRIBUTOR ENDED DISTRIBUTION, WAITING FOR RESULTS");

    // Wait for the calculations to finish
    for (Future<Void> future : multiplicationFutures) {
      try {
         future.get();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }

    System.out.println("CALCULATIONS FINISHED");
    long endTime = System.currentTimeMillis();
    System.out.println("Finished in: " + (endTime - startTime) + "ms");

    // Gathering of the results
    System.out.println("GATHERING RESULTS");
    List<Future<FinishExecutionTaskResult>> resultFutures = new ArrayList<>();

    for (Member member : instance.getCluster().getMembers()) {
      Callable<FinishExecutionTaskResult> task = new FinishExecutionTask();
      Future<FinishExecutionTaskResult> future = executor.submit(task);
      resultFutures.add(future);
    }

    for (Future<FinishExecutionTaskResult> future: resultFutures) {
      try {
        FinishExecutionTaskResult taskResult = future.get();
        int[] starts = taskResult.calculatedStarts();
        int[] ends = taskResult.calculatedEnds();
        double[][] partialResult = taskResult.result();
        for (int i = 0; i < starts.length; i++) {
          for (int y = 0; y < result.length; y++) {
            if (ends[i] - starts[i] >= 0)
              System.arraycopy(partialResult[y], starts[i], result[y], starts[i], ends[i] - starts[i]);
          }
        }
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }

    instance.shutdown();
    return result;
  }
}