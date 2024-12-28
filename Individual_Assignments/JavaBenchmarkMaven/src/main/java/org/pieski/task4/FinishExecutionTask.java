package org.pieski.task4;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

record FinishExecutionTaskResult(double[][] result, int[] calculatedStarts, int[] calculatedEnds) {}

public class FinishExecutionTask implements Callable<FinishExecutionTaskResult> {

  @Override
  public FinishExecutionTaskResult call() throws Exception {
    MultiplicationTaskManager instance = MultiplicationTaskManager.getInstance();
    Integer[] calculatedStarts = instance.calculatedStarts.toArray(new Integer[0]);
    Integer[] calculatedEnds = instance.calculatedEnds.toArray(new Integer[0]);

    return new FinishExecutionTaskResult(instance.result, convertToIntArray(calculatedStarts) , convertToIntArray(calculatedEnds));
  }

  private int[] convertToIntArray(Integer[] integerArray) {
    if (integerArray == null) return null;
    int[] intArray = new int[integerArray.length];
    for (int i = 0; i < integerArray.length; i++) {
      intArray[i] = integerArray[i]; // Auto-unboxing
    }
    return intArray;
  }
}
