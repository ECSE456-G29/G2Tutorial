package backend;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;

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
    String t = repo.getLastTag();
    if (t == null) {
      return "0";
    }
    return t;
  }

  /**
   * Adds the current changes as a new step.
   *
   * @return the step id just added
   */
  public String addStep(String message) {
    int stepTag = Integer.parseInt(currentStep()) + 1;
    if (message == null) {
      message = String.format("Step %s", stepTag);
    }
    repo.commitAll(message);
    repo.tagCommit(Integer.toString(stepTag));
    return Integer.toString(stepTag);
  }

  /**
   * For the current step, compares the set of files changed in the source code and the set files
   * marked as changed in the ascii docs. The output is the the difference.
   *
   * @return Diff between two sets
   */
  public Diff diff() throws IOException {
    Set<String> deltaSource = new HashSet<String>();
    try {
      List<DiffEntry> diffEntries = repo.diffSinceTag("");
      for (DiffEntry de : diffEntries) {
        deltaSource.add(de.getNewPath());
      }
    } catch (GitAPIException e) {
      throw new RuntimeException("Could not connect to git", e);
    } catch (IOException e) {
      throw new IOException("g2t not intialized", e);
    }

    String step = currentStep();
    String docFname = step + ".asciidoc";
    Set<String> deltaDoc = Doc.filesChanged(docFname);

    return new Diff(deltaSource, deltaDoc);
  }
}
