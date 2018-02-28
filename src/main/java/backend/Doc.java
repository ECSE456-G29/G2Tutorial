package backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

public class Doc {

  private final static String token = "WARNING:";

  /**
   * Parses the specified asciidoc step file and returns the set of files that are mentioned as
   * changed.
   * @param fname name of step asciidoc
   * @return set of filenames that are mentioned as changed in the asciidoc step file
   */
  public static Set<String> filesChanged(String fname) {
    Set<String> changes = new HashSet<String>();

    try {
      BufferedReader reader = new BufferedReader(new FileReader(fname));
      String line;

      while((line = reader.readLine()) != null){
        System.out.println(line);

        String[] words = line.split("\\s");

        if(words[0].equalsIgnoreCase(token)){
          changes.add(words[1]);
        }
      }
    } catch (Exception e) {
      System.err.format("Exception occurred trying to read '%s'.", fname);
      e.printStackTrace();
    }
    return changes;
  }
}
