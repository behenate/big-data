package org.pieski.task4;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class PrepareExecutionTask implements Callable<Void>{
  private double[][] matA;
  private double[][] matB;

  public PrepareExecutionTask(double[][] matA, double[][] matB) {
    this.matA = matA;
    this.matB = matB;
  }

  @Override
  public Void call() {
    MultiplicationTaskManager instance = MultiplicationTaskManager.getInstance();
    instance.matA = this.matA;
    instance.matB = this.matB;
    instance.calculatedStarts = new ArrayList<>();
    instance.calculatedEnds = new ArrayList<>();
    instance.result = Task4Main.generateMatrix(this.matA.length, true);
    return null;
  }

  public double[][] getMatA() {
    return matA;
  }

  public double[][] getMatB() {
    return matB;
  }
}
