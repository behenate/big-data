package org.pieski.task2;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLSparse;
import com.jmatio.types.MLStructure;

import java.io.IOException;

public class MatFileLoader {

  public static MLSparse loadMc2Depi() {
    try {
      // Hardcoded, cause I can't be bothered
      String filePath = "/Users/wojciechdrozdz/uni/Magisterka/Erasmus/Big Data/Benchmarking_1/JavaBenchmarkMaven/mc2depi.mat";
      MatFileReader matReader = new MatFileReader(filePath);

      MLArray mlArrayProblem = matReader.getMLArray("Problem");
      if (mlArrayProblem instanceof MLStructure mlStructure) {
        return (MLSparse) mlStructure.getField("A");
      } else {
        System.out.println("The data under 'Problem' is not a structure.");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
