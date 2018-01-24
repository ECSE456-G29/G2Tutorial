import parse.GreetParser;

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
    if (args.length > 0 && args[0].equals("greet")) {
      GreetParser p = new GreetParser(args);
      p.parse();
    } else {
      System.out.println("Available commands:\n\tgreet");
      System.exit(1);
    }
  }
}
