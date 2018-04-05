package backend;

import static backend.Repo.getRepo;
import static backend.Repo.getRepostory;

import backend.diff.Diff;
import backend.diff.DiffEntry;
import java.io.IOException;
import java.util.Set;
import org.eclipse.jgit.api.errors.GitAPIException;




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
    this.repo = getRepo();
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
      return "1";
    }
    int tmp = Integer.parseInt(t) + 1;
    return Integer.toString(tmp);
  }

  /**
   * Adds the current changes as a new step.
   *
   * @return the new current step id after step is added
   */
  public String addStep(String message) {
    int stepTag = Integer.parseInt(currentStep());
    if (message == null) {
      message = String.format("End of step %s", stepTag);
    }

    final String sha = repo.commitAll(message);
    repo.tagCommit(Integer.toString(stepTag));
    repo.push();

    String url = "";

    try {
      url += getRepostory().getConfig().getString("remote", "origin", "url");
    } catch (IOException e) {
      e.printStackTrace();
    }

    url = url.replace(".git", "/archive/" +  sha + ".zip");


    try {
      Doc.addStep(url, stepTag);
    } catch (IOException e) {
      e.printStackTrace();
      //TODO: handle this
    }
    int returnStepTag = stepTag + 1;
    return Integer.toString(returnStepTag);
  }

  /**
   * For the current step, compares the set of files changed in the source code and the set files
   * marked as changed in the ascii docs. The output is the the difference.
   *
   * @return Diff between two sets
   */
  public Diff diff() throws IOException {
    Set<DiffEntry> deltaSource;
    Set<DiffEntry> deltaDoc;

    String lastStepTag = repo.getLastTag();

    try {
      deltaSource = repo.diffSinceTag(lastStepTag);
      deltaSource.addAll(repo.uncommitedChanges());
    } catch (GitAPIException e) {
      throw new RuntimeException("Errors interfacing with git", e);
    } catch (IOException e) {
      throw new IOException("g2t not intialized", e);
    }

    String currentStepTag = currentStep();
    String docFname = currentStepTag + ".adoc";
    deltaDoc = Doc.filesChanged(docFname);

    return new Diff(deltaSource, deltaDoc);
  }
}
