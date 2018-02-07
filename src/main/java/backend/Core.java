package backend;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Ref;

public class Core {
  private Repo repo;

  /**
   * Constructor for the backend core.
   * 
   * @param repo wrapper for interacting with git repository
   */
  public Core(Repo repo) {
    this.repo = repo;
  }

  /**
   * Constructor for the backend core which fetches an existing repo.
   */
  public Core() throws IOException {
    this.repo = Repo.getRepo();
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

  /**
   * Returns the current step id as a String.
   *
   * @return the current step
   */
  public String currentStep() {
    Ref t = repo.getLastTag();
    if (t == null) {
      return "0";
    }

    // getName returns tags of the form "/refs/tags/1"
    Pattern p = Pattern.compile("tags/(.*?)$");
    Matcher matcher = p.matcher(t.getName());
    if (matcher.find()) {
      return matcher.group(1);
    }

    throw new IllegalStateException("Tag didn't match proper format");
  }
}
