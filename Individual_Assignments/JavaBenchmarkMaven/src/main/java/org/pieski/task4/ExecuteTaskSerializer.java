package org.pieski.task4;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;

import java.io.IOException;

public class ExecuteTaskSerializer implements StreamSerializer<ExecuteTask> {

  @Override
  public int getTypeId() {
    return 10;
  }

  @Override
  public void write(ObjectDataOutput out, ExecuteTask node) throws IOException {
    out.writeInt(node.getColumnStart());
    out.writeInt(node.getColumnEnd());
  }

  @Override
  public ExecuteTask read(ObjectDataInput in) throws IOException {
    return new ExecuteTask(in.readInt(), in.readInt());
  }

  @Override
  public void destroy() {
    // Optionally clean up resources
  }
}