import backend.Core;
import backend.Diff;
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

  static final String COMMANDS = "Available commands:\n\tgreet, init, step, diff";

  /**
   * Main entry point to G2Tutorial.
   *
   * @param  args commandline arguments
   */
  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println(COMMANDS);
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
        Path file = Paths.get("tutorial.asciidoc");
        Files.write(file, lines, Charset.forName("UTF-8"));
      } catch (IOException e) {
        System.err.println("Could not init g2t");
        e.printStackTrace();
      }
      System.exit(0);
    } else if (args[0].equals("step")) {
      // Commits the current changes as a step and starts a new step
      Core c = getCore();
      StepParser p = new StepParser(args, c);
      p.parse();
    } else if (args[0].equals("diff")) {
      Core c = getCore();
      try {
        Diff d = c.diff();
        System.out.println(d);
      } catch (IOException e) {
        e.printStackTrace();
        System.exit(1);
      }
    } else {
      System.out.println(COMMANDS);
      System.exit(1);
    }
  }

  static Core getCore() {
    Core c = null;
    try {
      c = new Core();
    } catch (IOException e) {
      System.err.println("g2t is not initialized");
      e.printStackTrace();
      System.exit(1);
    }

    return c;
  }
}
