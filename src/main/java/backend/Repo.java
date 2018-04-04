package backend;

import backend.diff.DiffEntry.ChangeType;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
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
   * Pushes the branch.
   *
   * @return Iterable containing info on the push
   */
  public Iterable<PushResult> push() {
    // push to remote:
    PushCommand pushCommand = git.push();
    Scanner scanner = new Scanner(System.in);
    System.out.println("GitHub Username: ");
    String username = scanner.next();

    Console console = System.console();
    String password =
            new String(console.readPassword("Please enter your GitHub password: "));

    pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password));
    // you can add more settings here if needed

    Iterable<PushResult> pushResults = new Iterable<PushResult>() {
      @Override
      public Iterator<PushResult> iterator() {
        return null;
      }
    };

    try {
      pushResults = pushCommand.call();
    } catch (GitAPIException e) {
      //todo handle this exception
      e.printStackTrace();
    }
    return pushResults;
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

    //todo implement some sort of catch for an incorrect URL
    Scanner scanner = new Scanner(System.in);
    System.out.print("Please enter https url of remote github repository: ");
    String url = scanner.next();

    RemoteAddCommand remoteAddCommand = git.remoteAdd();
    remoteAddCommand.setName("origin");
    try {
      remoteAddCommand.setUri(new URIish(url));
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    try {
      remoteAddCommand.call();
    } catch (GitAPIException e) {
      e.printStackTrace();
    }

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
   * Fetches an existing repository.
   *
   * @return existing repository.
   * @throws IOException IO errors when unable to find existing repo
   */
  public static Repository getRepostory() throws IOException {
    Repository repo;
    FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
    repo = repositoryBuilder.findGitDir().build();

    return repo;
  }

  /**
   * public Set < DiffEntry > diffSinceTag(String tagName) gets the set of filenames that were
   * changed since the commit tagged with tagName. Input: tagName: get diff since this name.
   *
   * @return set of filenames that have been changed since the tagged commit List of filenames that
   *         have been changed since the tagged commit - Can be printed or converted to string.
   */
  public Set<backend.diff.DiffEntry> diffSinceTag(String tagName)
      throws IOException, GitAPIException {
    Set<backend.diff.DiffEntry> diffEntries = new HashSet<>();

    Repository repository = git.getRepository();

    // The {tree} will return the underlying tree-id instead of the commit-id itself!
    // For a description of what the carets do see e.g. http://www.paulboxley.com/blog/2011/06/git-caret-and-tilde
    // This means we are selecting the parent (#(Number of "^") -1 ) of current HEAD and
    // take the tree-ish of it

    ObjectId oldHead;

    if (tagName != "") { // if call the function with a specific tag, get the objectID
      Ref tag = repository.exactRef("/refs/tags/" + tagName); //get a reference to the tag
      RevWalk walk = new RevWalk(repository);
      RevTree tree = walk.parseTree(tag.getObjectId()); //convert the tag to a tree instance

      oldHead = tree.toObjectId(); //return the ObjectID of the tree

    } else {  // otherwise just to have latest commit changes
      oldHead = repository.resolve("HEAD^^{tree}");
    }

    ObjectId head = repository.resolve("HEAD^{tree}");

    // prepare the two iterators to compute the diff
    ObjectReader reader = repository.newObjectReader();

    CanonicalTreeParser oldTreeIteration = new CanonicalTreeParser();
    oldTreeIteration.reset(reader, oldHead);

    CanonicalTreeParser newTreeIteration = new CanonicalTreeParser();
    newTreeIteration.reset(reader, head);

    // Get the list of changed files
    List<DiffEntry> diffs = git.diff()
        .setNewTree(newTreeIteration)
        .setOldTree(oldTreeIteration)
        .call();

    // Convert to custom diff type
    for (DiffEntry d : diffs) {
      ChangeType changeType = convertChangeType(d.getChangeType());
      backend.diff.DiffEntry diffEntry = new backend.diff.DiffEntry(d.getNewPath(), changeType);
      diffEntries.add(diffEntry);
    }

    return diffEntries;
  }

  private static ChangeType convertChangeType(DiffEntry.ChangeType jgitChangeType) {
    switch (jgitChangeType) {
      case ADD:
        return ChangeType.ADD;
      case COPY:
        return ChangeType.ADD;
      case RENAME:
        return ChangeType.ADD;
      case MODIFY:
        return ChangeType.MODIFY;
      case DELETE:
        return ChangeType.DELETE;
      default:
        throw new IllegalArgumentException("invalid jgit change type");
    }
  }

  /**
   * Tracks the uncommited changes.
   *
   * @return existing repo.
   * @throws IOException IO errors when unable to find existing repo
   */
  public Set<backend.diff.DiffEntry> uncommitedChanges() throws IOException, GitAPIException {
    Set<backend.diff.DiffEntry> changes = new HashSet<>();
    Status status = git.status().call();

    Set<backend.diff.DiffEntry> added = getChanges(status.getUntracked(), ChangeType.ADD);
    changes.addAll(added);

    Set<String> modifiedSet = new HashSet(status.getModified());
    Set<String> uncommittedChanges = new HashSet(status.getUncommittedChanges());
    modifiedSet.addAll(uncommittedChanges);
    Set<backend.diff.DiffEntry> modified = getChanges(modifiedSet, ChangeType.MODIFY);
    changes.addAll(modified);

    Set<backend.diff.DiffEntry> deleted = getChanges(status.getMissing(), ChangeType.DELETE);
    changes.addAll(deleted);

    return changes;
  }

  private static Set<backend.diff.DiffEntry> getChanges(
      Set<String> changes, ChangeType changeType) {
    Set<backend.diff.DiffEntry> r = new HashSet<>();
    for (String s : changes) {
      r.add(new backend.diff.DiffEntry(s, changeType));
    }

    return r;
  }


}