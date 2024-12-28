package org.pieski.task4;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.io.IOException;

public class PrepareExecutionTaskSerializer implements StreamSerializer<PrepareExecutionTask> {
  @Override
  public int getTypeId() {
    return 11;
  }

  @Override
  public void write(ObjectDataOutput out, PrepareExecutionTask task) throws IOException {
    out.writeInt(task.getMatA().length);
    out.writeInt(task.getMatA()[0].length);
    out.writeInt(task.getMatB().length);
    out.writeInt(task.getMatB()[0].length);

    for (double[] row: task.getMatA()) {
      out.writeDoubleArray(row);
    }

    for (double[] row: task.getMatB()) {
      out.writeDoubleArray(row);
    }
  }

  @Override
  public PrepareExecutionTask read(ObjectDataInput in) throws IOException {
    int matALength = in.readInt();
    int matAWidth = in.readInt();

    int matBLength = in.readInt();
    int matBWidth = in.readInt();


    double[][] matA = new double[matALength][matAWidth];
    double[][] matB = new double[matBLength][matBWidth];

    for (int i = 0; i < matALength; i++) {
      matA[i] = in.readDoubleArray();
    }
    for (int i = 0; i < matBLength; i++) {
      matB[i] = in.readDoubleArray();
    }

    return new PrepareExecutionTask(matA, matB);
  }

  @Override
  public void destroy() {
    // Optionally clean up resources
  }
}