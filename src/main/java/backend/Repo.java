package backend;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

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
  public String getLastTag() {
    try {
      Iterable<RevCommit> commits = git.log().call();
      for (RevCommit commit : commits) {
        Map<ObjectId, String> names = git.nameRev().add(commit).addPrefix("refs/tags/").call();
        String tag = names.get(commit);
        if (tag != null) {
          return tag;
        }
      }
    } catch (NoHeadException e) {
      return null;
    } catch (GitAPIException e) {
      // TODO: handle other git Exceptions
      throw new RuntimeException(e);
    } catch (MissingObjectException e) {
      throw new RuntimeException("The commit was missing. We should never get here!");
    }
    return null;
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

  /**
   * public Set < String > diffSinceTag(String tagName) gets the set of filenames that were changed
   * since the commit tagged with tagName. Input: tagName: get diff since this name. Output: -- set
   * of filenames that have been changed since the tagged commit List of filenames that have been
   * changed since the tagged commit - Can be printed or converted to string.
   */

  public static List<DiffEntry> diffSinceTag(String tagName) throws IOException, GitAPIException {
    // Set<String> fileNames = new HashSet<>();
    List<DiffEntry> fileNames;

    FileRepositoryBuilder builder = new FileRepositoryBuilder();
    Repository repository = builder.readEnvironment() // scan environment GIT_* variables
        .findGitDir() // scan up the file system tree
        .build();

    // The {tree} will return the underlying tree-id instead of the commit-id itself!
    // For a description of what the carets do see e.g. http://www.paulboxley.com/blog/2011/06/git-caret-and-tilde
    // This means we are selecting the parent (#(Number of "^") -1 ) of current HEAD and
    // take the tree-ish of it

    ObjectId oldHead;

    if (tagName != "") { //if call the function with a specific tag, get the objectID

      oldHead = RevTag.fromString(tagName);

      // oldHead = repository.resolve("HEAD^^{tag}");

    } else {  //if call function just to have latest commit changes
      oldHead = repository.resolve("HEAD^^{tree}");
    }

    ObjectId head = repository.resolve("HEAD^{tree}");

    // prepare the two iterators to compute the diff between
    try (ObjectReader reader = repository.newObjectReader()) {
      //For the oldTree
      CanonicalTreeParser oldTreeIteration = new CanonicalTreeParser();
      oldTreeIteration.reset(reader, oldHead);
      //For the head Tree
      CanonicalTreeParser newTreeIteration = new CanonicalTreeParser();
      newTreeIteration.reset(reader, head);

      // finally get the list of changed files
      try (Git git = new Git(repository)) {
        List<DiffEntry> diffs = git.diff()
            .setNewTree(newTreeIteration)
            .setOldTree(oldTreeIteration)
            .call();
        fileNames = diffs;
      }
    }

    return fileNames;
  }

  /**
   * Tracks the uncommited changes.
   *
   * @return existing repo.
   * @throws IOException IO errors when unable to find existing repo
   */

  public static Set<String> uncommitedChanges() throws IOException, GitAPIException {

    FileRepositoryBuilder builder = new FileRepositoryBuilder();
    Repository repository = builder.readEnvironment() // scan environment GIT_* variables
        .findGitDir() // scan up the file system tree
        .build();

    System.out.println("Listing uncommitted changes:");
    Set<String> uncommittedChanges;
    try (Git git = new Git(repository)) {

      Status status = git.status().call();
      uncommittedChanges = status.getUncommittedChanges();
      for (String uncommitted : uncommittedChanges) {
        System.out.println("Uncommitted: " + uncommitted);
      }

      // Now we are printing all the other changes that happened in the repo
      // however we are only returning the uncommited files set
      // TODO : merge them all together 
      Set<String> added = status.getAdded();
      for (String add : added) {
        System.out.println("Added: " + add);
      }

      Set<String> changed = status.getChanged();
      for (String change : changed) {
        System.out.println("Change: " + change);
      }

      Set<String> missing = status.getMissing();
      for (String miss : missing) {
        System.out.println("Missing: " + miss);
      }

      Set<String> modified = status.getModified();
      for (String modify : modified) {
        System.out.println("Modification: " + modify);
      }

      Set<String> removed = status.getRemoved();
      for (String remove : removed) {
        System.out.println("Removed: " + remove);
      }

      Set<String> untracked = status.getUntracked();
      for (String untrack : untracked) {
        System.out.println("Untracked: " + untrack);
      }

      Set<String> untrackedFolders = status.getUntrackedFolders();
      for (String untrack : untrackedFolders) {
        System.out.println("Untracked Folder: " + untrack);
      }
    }

    return uncommittedChanges;
  }


}