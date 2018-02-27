package backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Set;

public class Doc {


  /**
   * Parses the specified asciidoc step file and returns the set of files that are mentioned as
   * changed.
   * @param fname name of step asciidoc
   * @return set of filenames that are mentioned as changed in the asciidoc step file
   */
  public static Set<String> filesChanged(String fname) {
    // TODO: Implement this method
    try {
      BufferedReader reader = new BufferedReader(new FileReader(fname));
      String line;
      while((line = reader.readLine()) != null){
        /*TODO: implement the following
        Check for the WARNING: keyword at the beginning of the line. If the warning found then parse the file path
        out of the line and add it the set
         */
      }
    } catch (Exception e) {
      System.err.format("Exception occurred trying to read '%s'.", fname);
      e.printStackTrace();
    }
    throw new UnsupportedOperationException("Not implemented yet");
  }
}
