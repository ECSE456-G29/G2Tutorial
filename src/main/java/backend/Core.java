package backend;

import database.DatabaseException;
import database.FlatFileDb;
import database.IDatabase;
import java.io.IOException;
import org.eclipse.jgit.api.errors.GitAPIException;

public class Core {
  private Repo repo;
  private IDatabase db;
  private int step;

  /**
   * Constructor for the backend core.
   * 
   * @param repo wrapper for interacting with git repository
   * @param db database for storing relationships between commits and tutorial steps
   */
  public Core(Repo repo, IDatabase db) {
    this.step = 0;
    this.repo = repo;
    this.db = db;
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
    IDatabase db = FlatFileDb.initDb(path);
    return new Core(repo, db);
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
   * commits all current changes to active tutorial step. Changes are added as a commit and the hash
   * is stored in the database.
   *
   * @throws GitAPIException issues with git
   * @throws DatabaseException database processing issues
   */
  public void updateStep() throws GitAPIException, DatabaseException {
    String hash = this.repo.commitAll();
    this.db.addSha(this.step, hash);
  }
}
