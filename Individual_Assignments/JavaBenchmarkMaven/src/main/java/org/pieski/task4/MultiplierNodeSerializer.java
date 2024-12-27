package org.pieski.task4;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.io.IOException;

public class MultiplierNodeSerializer implements StreamSerializer<MultiplierNode> {

  @Override
  public int getTypeId() {
    return 10;  // Ensure this type ID is unique within your project
  }

  @Override
  public void write(ObjectDataOutput out, MultiplierNode node) throws IOException {
    int matALength = node.getMatA().length;
    int matBLength = node.getMatB().length;
    int resultLength = node.getResult().length;
    int columnStart = node.getColumnStart();
    int columnEnd = node.getColumnEnd();
    out.writeInt(matALength);
    out.writeInt(matBLength);
    out.writeInt(resultLength);
    out.writeInt(columnStart);
    out.writeInt(columnEnd);

    for (int i = 0; i < matALength; i++) {
      out.writeDoubleArray(node.getMatA()[i]);
    }
    for (int i = 0; i < matBLength; i++) {
      out.writeDoubleArray(node.getMatB()[i]);
    }
    for (int i = 0; i < resultLength; i++) {
      out.writeDoubleArray(node.getResult()[i]);
    }
  }

  @Override
  public MultiplierNode read(ObjectDataInput in) throws IOException {
    int matALength = in.readInt();
    int matBLength = in.readInt();
    int resultLength = in.readInt();
    int columnStart = in.readInt();
    int columnEnd = in.readInt();

    double[][] matA = new double[matALength][];
    double[][] matB = new double[matBLength][];
    double[][] result = new double[resultLength][];

    for (int i = 0; i < matALength; i++) {
      matA[i] = in.readDoubleArray();
    }
    for (int i = 0; i < matBLength; i++) {
      matB[i] = in.readDoubleArray();
    }
    for (int i = 0; i < resultLength; i++) {
      result[i] = in.readDoubleArray();
    }

    return new MultiplierNode(matA, matB, result, columnStart, columnEnd);
  }

  @Override
  public void destroy() {
    // Optionally clean up resources
  }
}