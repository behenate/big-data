//package org.pieski.task4;
//
//import com.hazelcast.nio.ObjectDataInput;
//import com.hazelcast.nio.ObjectDataOutput;
//import com.hazelcast.nio.serialization.StreamSerializer;
//
//import java.io.IOException;
//
//public class DoubleArraySerializer implements StreamSerializer<double[]> {
//  @Override
//  public int getTypeId() {
//    return 1; // Unique identifier for this serializer
//  }
//
//  @Override
//  public double[] read(ObjectDataInput in) throws IOException {
//    int length = in.readInt();
//    double[] array = new double[length];
//    for (int i = 0; i < length; i++) {
//      array[i] = in.readDouble();
//    }
//    return array;
//  }
//
//  @Override
//  public void destroy() {
//    // cleanup resources if needed
//  }
//
//  @Override
//  public void write(ObjectDataOutput objectDataOutput, double[] doubles) throws IOException {
//    objectDataOutput.writeInt(doubles.length);
//    for (double value : doubles) {
//      objectDataOutput.writeDouble(value);
//    }
//  }
//}