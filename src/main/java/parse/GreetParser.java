package parse;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class GreetParser {
  private String[] args = null;
  private Options options = new Options();

  /**
   * The greet command says hi with the option of a name.
   *
   * @param args commandline arguments
   */
  public GreetParser(String[] args) {
    this.args = args;

    options.addOption("h", "help", false, "show help.");
    options.addOption("n", "name", true, "Name to be greeted by.");
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

      String name = null;
      if (cmd.hasOption("n")) {
        name = cmd.getOptionValue("n");
      } else {
        System.out.println("Hello, World");
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