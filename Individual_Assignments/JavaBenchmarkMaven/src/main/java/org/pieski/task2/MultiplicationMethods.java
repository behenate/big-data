package org.pieski.task2;

import com.jmatio.types.MLSparse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MultiplicationMethods {
  public static double[][] standardMultiply(double[][] matrix1, double[][] matrix2, double[][] result) {
    int rowM1 = matrix1.length;
    int colM1 = matrix1[0].length;
    int colM2 = matrix2[0].length;

    for (int i = 0; i < rowM1; i++) {
      for (int j = 0; j < colM2; j++) {
        for (int k = 0; k < colM1; k++) {
          result[i][j] += matrix1[i][k] * matrix2[k][j];
        }
      }
    }

    return result;
  }

  public static void blockedColumnMultiply(double[][] A, double[][] B, double[][] C) {
    int N = A.length;
    int block_size = 256;

    for (int col_block = 0; col_block < N; col_block += block_size) {
      for (int row = 0; row < N; row++) {
        for (int tile = 0; tile < N; tile += block_size) {
          for (int tile_row = 0; tile_row < block_size && tile_row + tile < N; tile_row++) {
            for (int idx = 0; idx < block_size && col_block + idx < N; idx++) {
              C[row][col_block + idx] +=
                  A[row][tile + tile_row] *
                      B[tile + tile_row][col_block + idx];
            }
          }
        }
      }
    }
  }

  public static void blockedMultiply(double[][] A, double[][] B, double[][] C) {
    int N = A.length;
    int block_size = 256;
    for (int row = 0; row < N; row++) {
      for (int block = 0; block < N; block += block_size) {
        for (int chunk = 0; chunk < N; chunk += block_size) {
          for (int sub_chunk = 0; sub_chunk < block_size && chunk + sub_chunk < N; sub_chunk++) {
            for (int idx = 0; idx < block_size && block + idx < N; idx++) {
              C[row][block + idx] +=
                  A[row][chunk + sub_chunk] *
                      B[chunk + sub_chunk][block + idx];
            }
          }
        }
      }
    }
  }

  public static void sparseMultiply(MLSparse matA, MLSparse matB, MLSparse result) {
    HashMap<Integer, ArrayList<SparseMatrixUtils.Tuple>> procMat1 = SparseMatrixUtils.preprocessSparseMatrix(matA);
    System.out.println("Sparse multiply preprocessing finished 1/2");
    HashMap<Integer, ArrayList<SparseMatrixUtils.Tuple>> procMat2 = SparseMatrixUtils.preprocessSparseMatrix(matB);
    System.out.println("Sparse multiply preprocessing finished, multiplying");

    long startMillis = System.currentTimeMillis();

    for (Integer i : procMat1.keySet()) {
      List<SparseMatrixUtils.Tuple> row = procMat1.get(i);
      for (SparseMatrixUtils.Tuple aElement : row) {
        int k = aElement.columnIndex;
        if (procMat2.containsKey(k)) {
          List<SparseMatrixUtils.Tuple> bRow = procMat2.get(k);
          for (SparseMatrixUtils.Tuple bElement : bRow) {
            int j = bElement.columnIndex;
            double currentValue;
            Double storedValue = (Double)result.get(i, j);
            if(storedValue == null)
              currentValue = 0;
            else
              currentValue = storedValue;

            double addValue = aElement.value * bElement.value;
            result.set(currentValue + addValue,i, j);
          }
        }
      }
    }
    long endMillis = System.currentTimeMillis();
    System.out.println("Finished actual matrix multiplication in: " + (endMillis-startMillis) + "ms");
  }
}


class SparseMatrixUtils {
  public static HashMap<Integer, ArrayList<Tuple>> preprocessSparseMatrix(MLSparse matrix) {
    int[] jc = matrix.getJC();
    int[] ir = matrix.getIR();
    Double[] data = matrix.exportReal();
    HashMap<Integer, ArrayList<Tuple>> processedData = new HashMap<>();

    for (int col = 0; col < jc.length - 1; col++) {
      for (int idx = jc[col]; idx < jc[col + 1]; idx++) {
        int row = ir[idx];
        double value = data[idx];

        if (processedData.get(row) == null) {
          processedData.put(row, new ArrayList<>());
        }
        ArrayList<Tuple> rowList = processedData.get(row);
        rowList.add(new Tuple(col, value));
      }
      System.out.println("Preprocess progress: " + col + "/" + jc.length);
    }
    return processedData;
  }


  static class Tuple {
    int columnIndex;
    double value;

    public Tuple(int columnIndex, double value) {
      this.columnIndex = columnIndex;
      this.value = value;
    }
  }
}