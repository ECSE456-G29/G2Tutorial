package backend;

import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class Repo {
  private Git git;

  public Repo(Git git) {
    this.git = git;
  }
  
  /**
   * Commits all files and returns the hash.
   * 
   * @return SHA256 hash of the commit
   */
  public String commitAll() {
    try {
      this.git.add()
        .addFilepattern(".")
        .call();
      RevCommit commit = this.git.commit().call();
      return commit.getId().name();
    } catch (GitAPIException e) {
      e.printStackTrace();
      System.exit(-1); // Can't recover
    } 
    
    return null;
  }
  
  /**
   * Initializes and returns a repo in the given directory.
   * 
   * @return newly initialized repo
   */
  public static Repo initRepo(String path) throws IOException {
    Repository repo = FileRepositoryBuilder.create(new File(path, ".git"));
    repo.create();
    Git git = new Git(repo);
    return new Repo(git);
  }
}
