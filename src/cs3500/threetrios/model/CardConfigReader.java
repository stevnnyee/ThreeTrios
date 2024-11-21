package cs3500.threetrios.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and parses card configuration files for the ThreeTrios game.
 */
public class CardConfigReader {
  /**
   * Reads a card configuration file and creates a list of Cards.
   *
   * @param filePath path to the card configuration file
   * @return List of Cards configured according to the file
   * @throws IllegalArgumentException if file format is invalid
   * @throws IOException if file cannot be read
   */
  public static List<Card> readCardConfig(String filePath) throws IOException {
    List<Card> cards = new ArrayList<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line = reader.readLine();

      while (line != null) {
        if (!line.trim().startsWith("//")) {
          String[] parts = line.trim().split(" ");
          if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid card format: " + line);
          }

          String name = parts[0];
          int north = parseAttackValue(parts[1]);
          int south = parseAttackValue(parts[2]);
          int east = parseAttackValue(parts[3]);
          int west = parseAttackValue(parts[4]);

          cards.add(new ThreeTriosCard(name, north, south, east, west));
        }
        line = reader.readLine();
      }
    }
    return cards;
  }

  /**
   * Parses an attack value, converting 'A' to 10.
   *
   * @param value string value to parse
   * @return integer attack value
   * @throws IllegalArgumentException if value is invalid
   */
  private static int parseAttackValue(String value) {
    if (value.equals("A")) {
      return 10;
    }
    try {
      int num = Integer.parseInt(value);
      if (num < 1 || num > 10) {
        throw new IllegalArgumentException("Attack value must be between 1 and 10 or 'A'");
      }
      return num;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid attack value: " + value);
    }
  }
}