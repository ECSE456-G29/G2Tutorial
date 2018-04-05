package backend;

import backend.diff.DiffEntry;
import backend.diff.DiffEntry.ChangeType;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Doc {

  private static final String token = "(?<=(\\/\\/<)).+?(?=(>\\/\\/))";

  /**
   * Creates a new tutorial.asdoc file.
   * @throws IOException if it fails to create and write to a new file
   */
  public static void createDoc(int step) throws IOException {
    List<String> lines = Arrays.asList("= Tutorial");
    Path file = Paths.get(step + ".adoc");
    Files.write(file, lines, Charset.forName("UTF-8"));
  }

  /**
   * Appends a step to a the tutorial asciidoc.
   * @throws IOException if it fails to create and write to a new file
   */
  public static void addStep(String url, int stepTag) throws IOException {
    String checkout = "image::https://dabuttonfactory.com/button.png?t=Checkout&f=Calibri-Bold&ts=24&tc=fff&tshs=1&t"
            + "shc=000&hp=20&vp=8&c=5&bgt=unicolored&bgc=1a1d1d&bs=1&bc=000[link='" + url + "']";

    String asciidoc = "\n\n== Step " + stepTag + "\n\n" + checkout + "\n";

    Files.write(Paths.get(stepTag + ".adoc"), asciidoc.getBytes(), StandardOpenOption.APPEND);

    createDoc(stepTag + 1);
  }


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
