package cs3500.threetrios.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Reads and parses board configuration files for the ThreeTrios game.
 */
public class BoardConfigReader {
  /**
   * Reads a board configuration file and creates a Grid.
   * @param filePath path to the board configuration file
   * @return Grid configured according to the file
   * @throws IllegalArgumentException if file format is invalid
   * @throws IOException if file cannot be read
   */
  public static Grid readBoardConfig(String filePath) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line = reader.readLine();
      while (line != null && line.trim().startsWith("//")) {
        line = reader.readLine();
      }
      if (line == null) {
        throw new IllegalArgumentException("File is empty or contains only comments");
      }
      String[] dimensions = line.trim().split(" ");
      if (dimensions.length != 2) {
        throw new IllegalArgumentException("Invalid dimensions format");
      }
      int rows = Integer.parseInt(dimensions[0]);
      int cols = Integer.parseInt(dimensions[1]);
      boolean[][] holes = new boolean[rows][cols];
      for (int i = 0; i < rows; i++) {
        line = reader.readLine();
        if (line == null || line.length() != cols) {
          throw new IllegalArgumentException("Invalid grid layout");
        }
        for (int j = 0; j < cols; j++) {
          holes[i][j] = line.charAt(j) == 'X';
        }
      }
      return new ThreeTriosGrid(rows, cols, holes);
    }
  }
}