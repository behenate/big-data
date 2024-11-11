package org.pieski.task2;

import com.jmatio.types.MLSparse;

public interface SparseMatrixMultiplier {
  MLSparse multiply();

  static MLSparse generateEmpty(int size) {
    return new MLSparse(null, new int[] {size, size} , 0, 0); // Initialize a new sparse matrix for the result
  }
}
