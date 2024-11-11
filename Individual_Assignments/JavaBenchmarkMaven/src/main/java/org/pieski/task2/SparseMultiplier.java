package org.pieski.task2;

import com.jmatio.types.MLSparse;

public class SparseMultiplier implements SparseMatrixMultiplier {
  private MLSparse result;
  private MLSparse matA;
  private MLSparse matB;

  public SparseMultiplier(MLSparse matA, MLSparse matB) {
    this.result = SparseMatrixMultiplier.generateEmpty(matA.getN());
    this.matA = matA;
    this.matB = matB;
  }
  @Override
  public MLSparse multiply() {
    MultiplicationMethods.sparseMultiply(this.matA, this.matB, this.result);
    return this.result;
  }
}
