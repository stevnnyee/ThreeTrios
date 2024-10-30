package cs3500.threetrios.model;

/**
 * An interface to read the grid from a config file.
 */
public interface GridConfigReader {

  /**
   * Reads the grid provided its file name.
   */
  Grid readGrid(String filename);
}