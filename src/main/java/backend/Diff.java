package backend;

import java.util.HashSet;
import java.util.Set;

public class Diff {
  private Set<String> sourceChanges;
  private Set<String> docChanges;

  public Diff(Set<String> sourceChanges, Set<String> docChanges) {
    this.sourceChanges = sourceChanges;
    this.docChanges = docChanges;
  }

  @Override
  public String toString() {
    Set<String> deltaSourceChanges = new HashSet<>(sourceChanges);
    deltaSourceChanges.removeAll(docChanges);

    Set<String> deltaDocChanges = new HashSet<>(docChanges);
    deltaDocChanges.removeAll(sourceChanges);

    StringBuilder sb = new StringBuilder();
    sb.append("Source not in Docs:\n");
    for (String ds : deltaSourceChanges) {
      sb.append("\t" + ds);
    }

    sb.append("Docs not in Source:\n");
    for (String dd : deltaDocChanges) {
      sb.append("\t" + dd);
    }

    return sb.toString();
  }
}
