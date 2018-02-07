package backend;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Ref;
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
   * @throws GitAPIException git issues
   */
  public String commitAll(String message) {
    RevCommit commit = null;
    try {
      this.git.add()
        .addFilepattern(".")
        .call();
      commit = this.git.commit().setMessage(message).call();
    } catch (NoFilepatternException e) {
      // Should never happen because "." is given as default
      throw new NullPointerException();
    } catch (GitAPIException e) {
      // TODO: handle git Exceptions
      throw new RuntimeException(e);
    }
    return commit.getId().name();
  }

  /**
   * Returns the latest tag from the current head.
   *
   * @return latest tag from the current head or null if no tag exist
   * @throws GitAPIException git issues
   */
  public Ref getLastTag() {
    List<Ref> tags = null;
    try {
      tags = git.tagList().call();
    } catch (NoHeadException e) {
      return null;
    } catch(GitAPIException e) {
      // TODO: handle other git Exceptions
      throw new RuntimeException(e);
    }
    if (tags.isEmpty()) {
      return null;
    }
    return tags.get(tags.size() - 1);
  }

  /**
   * Tags the latest commit.
   */
  public void tagCommit(String tag) {
    try {
      git.tag().setName(tag).call();
    } catch (GitAPIException e) {
      // TODO: handle git Exceptions
      throw new RuntimeException(e);
    }
  }

  /**
   * Initializes and returns a repo in the given directory.
   * 
   * @return newly initialized repo
   * @throws IOException IO errors when creating the git repo
   */
  public static Repo initRepo(String path) throws IOException {
    Repository repo = FileRepositoryBuilder.create(new File(path, ".git"));
    repo.create();
    Git git = new Git(repo);
    return new Repo(git);
  }

  /**
   * Fetches an existing repo.
   *
   * @return existing repo.
   * @throws IOException IO errors when unable to find existing repo
   */
  public static Repo getRepo() throws IOException {
    Repository repo;
    FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
    repo = repositoryBuilder.findGitDir().build();
    Git git = new Git(repo);
    return new Repo(git);
  }
}
