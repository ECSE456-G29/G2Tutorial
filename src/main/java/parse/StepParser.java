package parse;

import backend.Core;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class StepParser {
  private String[] args = null;
  private Core core;
  private Options options = new Options();

  /**
   * The greet command says hi with the option of a name.
   *
   * @param args commandline arguments
   * @param core instance of object for working with the backend
   */
  public StepParser(String[] args, Core core) {
    this.args = args;
    this.core = core;

    options.addOption("h", "help", false, "show help.");
    options.addOption("a", "add", false,"Add a step.");
  }

  /**
   * Parses and runs the greet command.
   *
   * <p>If the name option is provided, the application will say hello.
   * Otherwise, it says hello to the world.</p>
   */
  public void parse() {
    CommandLineParser parser = new DefaultParser();

    CommandLine cmd = null;
    try {
      cmd = parser.parse(options, args);

      if (cmd.hasOption("h")) {
        help();
      }

      String step = core.currentStep();
      if (cmd.hasOption("a")) {
        System.out.println("Finished step " + step);
        // TODO: implement adding a step
      } else {
        System.out.println("Current step: " + step);
      }

    } catch (ParseException e) {
      help();
    }
    System.exit(0);
  }

  private void help() {
    // This prints out some help
    HelpFormatter formater = new HelpFormatter();

    formater.printHelp("Main", options);
    System.exit(0);
  }
}