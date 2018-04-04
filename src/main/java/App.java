import backend.Core;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import parse.GreetParser;
import parse.StepParser;

public class App {
  public String getGreeting() {
    return "Hello world.";
  }

  /**
   * Main entry point to G2Tutorial.
   *
   * @param  args commandline arguments
   */
  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Available commands:\n\tgreet, init, step");
      System.exit(1);
    }


    if (args[0].equals("greet")) {
      // Greet just exists as a template for parsing
      GreetParser p = new GreetParser(args);
      p.parse();
    } else if (args[0].equals("init")) {
      // Initializes an empty git repo
      try {
        Core.initCore();
        List<String> lines = Arrays.asList("= EMPTY ASCIIDOC");
        Path file = Paths.get("tutorial.adoc");
        Files.write(file, lines, Charset.forName("UTF-8"));
      } catch (IOException e) {
        System.err.println("Could not init g2t");
        e.printStackTrace();
      }
      System.exit(0);
    } else if (args[0].equals("step")) {
      // Commits the current changes as a step and starts a new step
      Core c = null;
      try {
        c = new Core();
      } catch (IOException e) {
        System.err.println("g2t is not initialized");
        e.printStackTrace();
      }
      StepParser p = new StepParser(args, c);
      p.parse();
    } else {
      System.out.println("Available commands:\n"
          + "\tgreet, init, step");
      System.exit(1);
    }
  }
}
