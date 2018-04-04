package backend;

import backend.diff.DiffEntry;
import backend.diff.DiffEntry.ChangeType;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Doc {

  private static final String token = "(?<=(\\/\\/<)).+?(?=(>\\/\\/))";


  /**
   * Parses the specified asciidoc step file and returns the set of files that are mentioned as
   * changed.
   * @param fname name of step asciidoc
   * @return set of filenames that are mentioned as changed in the asciidoc step file
   */
  public static Set<DiffEntry> filesChanged(String fname) {
    Set<DiffEntry> changes = new HashSet<>();
    Pattern p = Pattern.compile(token);
    try {
      BufferedReader reader = new BufferedReader(new FileReader(fname));
      String line;

      while ((line = reader.readLine()) != null) {

        Matcher m = Pattern.compile(token)
                .matcher(line);
        while (m.find()) {
          DiffEntry diffEntry = new DiffEntry(m.group(), ChangeType.ADD);
          changes.add(diffEntry);
        }

      }
    } catch (Exception e) {
      System.err.format("Exception occurred trying to read '%s'.", fname);
      e.printStackTrace();
    }
    return changes;
  }
}
