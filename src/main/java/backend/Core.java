package backend;

import java.io.IOException;

public class Core {
  private Repo repo;
  private int step;

  /**
   * Constructor for the backend core.
   * 
   * @param repo wrapper for interacting with git repository
   */
  public Core(Repo repo) {
    this.step = 0;
    this.repo = repo;
  }

  /**
   * Initializes the core backend including the git repository and database for storing steps.
   * 
   * @param path location of project
   * @return backend core for managing the project
   * @throws IOException result of being unable to create repo at the given path
   */
  public static Core initCore(String path) throws IOException {
    Repo repo = Repo.initRepo(path);
    return new Core(repo);
  }

  /**
   * Same as {@link #initCore(String) initCore} except path defaults to current working directory.
   * 
   * @return see {@link #initCore(String) initCore}
   * @throws IOException see {@link #initCore(String) initCore}
   */
  public static Core initCore() throws IOException {
    String path = System.getProperty("user.dir");
    return initCore(path);
  }
}
