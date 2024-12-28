package org.pieski.task4;

import java.util.ArrayList;

public class MultiplicationTaskManager {
  private static MultiplicationTaskManager instance;
  public double[][] matA;
  public double[][] matB;
  public double[][] result;
  public ArrayList<Integer> calculatedStarts = new ArrayList<>();
  public ArrayList<Integer> calculatedEnds = new ArrayList<>();

  public static MultiplicationTaskManager getInstance() {
    if (instance == null) {
      instance = new MultiplicationTaskManager();
    }
    return instance;
  }
}
