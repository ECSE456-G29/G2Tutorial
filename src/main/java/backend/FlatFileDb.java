package backend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class FlatFileDb implements IDatabase {
  private String dbPath;
  
  public FlatFileDb(String dbPath) {
    this.dbPath = dbPath;
  }
  
  /**
   * Initializes a database for storing step commit SHAs.
   * 
   * @param path location to store the database
   * @return a database for storing step commit SHAs
   */
  public static FlatFileDb initDb(String path) {
    File db = new File(path, ".db");
    db.mkdir();
    return new FlatFileDb(path);
  }

  @Override
  public void addSha(int step, String sha) {
    File f = new File(this.dbPath, String.valueOf(step));
    try {
      f.createNewFile();
      Files.write(f.toPath(), sha.getBytes(), StandardOpenOption.APPEND);
    } catch (IOException e) {
      System.out.println("Error writing to file");
    }
  }

}