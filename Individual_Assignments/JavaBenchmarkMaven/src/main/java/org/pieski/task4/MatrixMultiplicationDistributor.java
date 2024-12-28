package org.pieski.task4;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import org.pieski.task2.MatrixMultiplier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

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

  public void prepare() {
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

  }

  public double[][] multiply() {
    IExecutorService executor = instance.getExecutorService(executorName);
    Member[] members = instance.getCluster().getMembers().toArray(new Member[0]);
    ArrayList<Callable<Void>> tasksToExecute = new ArrayList<>();
    AtomicReference<Integer> executedTasks = new AtomicReference<>(0);

    // MULTIPLICATION
    int numCols = matB[0].length;
    int numMembers = instance.getCluster().getMembers().size();
    int blockSize = numCols / (numMembers*10);

    int columnStart = 0;
    int columnEnd = columnStart + blockSize;
    while (columnEnd <= numCols) {
      Callable<Void> task = new ExecuteTask(columnStart, columnEnd);
      tasksToExecute.add(task);
      columnStart += blockSize;
      columnEnd = columnStart + blockSize;
    }

    Thread[] threads = new Thread[members.length];

    long startTime = System.currentTimeMillis();
    System.out.println("DISTRIBUTOR ENDED DISTRIBUTION, WAITING FOR RESULTS");

    // This should automatically balance the tasks across nodes, more powerful nodes should get more tasks.
    for (int i = 0; i < members.length; i++) {
      int finalI = i;
      Thread thread = new Thread(() -> {
        int mytask = executedTasks.updateAndGet(v -> v + 1) - 1;
        while (mytask < tasksToExecute.size()) {
          try {
            executor.submitToMember(tasksToExecute.get(mytask), members[finalI]).get();
          } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
          } catch (IndexOutOfBoundsException e) {
            return;
          }
          mytask = executedTasks.updateAndGet(v -> v + 1) - 1;
        }
      });
      threads[i] = thread;
      thread.start();
    }

    // Wait for the calculations to finish
    for (Thread thread: threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    System.out.println("CALCULATIONS FINISHED");
    long endTime = System.currentTimeMillis();
    System.out.println("Finished in: " + (endTime - startTime) + "ms");

    return result;
  }

  public double[][] gatherResults() {
    IExecutorService executor = instance.getExecutorService(executorName);

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