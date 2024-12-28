package org.pieski.task4;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.io.IOException;

public class FinishExecutionTaskResultSerializer implements StreamSerializer<FinishExecutionTaskResult> {
  @Override
  public void write(ObjectDataOutput out, FinishExecutionTaskResult finishExecutionTaskResult) throws IOException {
    out.writeIntArray(finishExecutionTaskResult.calculatedStarts());
    out.writeIntArray(finishExecutionTaskResult.calculatedEnds());

    out.writeInt(finishExecutionTaskResult.result().length);
    for (double[] row: finishExecutionTaskResult.result()) {
      out.writeDoubleArray(row);
    }
  }

  @Override
  public FinishExecutionTaskResult read(ObjectDataInput in) throws IOException {
    int[] starts = in.readIntArray();
    int[] ends = in.readIntArray();
    int size = in.readInt();
    double[][] result = new double[size][];

    for (int i = 0; i < size; i++) {
      result[i] = in.readDoubleArray();
    }
    return new FinishExecutionTaskResult(result, starts, ends);
  }

  @Override
  public int getTypeId() {
    return 123;
  }
}
