package backend.diff;

import java.util.HashSet;
import java.util.Set;

public class Diff {
  private Set<DiffEntry> sourceChanges;
  private Set<DiffEntry> docChanges;

  public Diff(Set<DiffEntry> sourceChanges, Set<DiffEntry> docChanges) {
    this.sourceChanges = sourceChanges;
    this.docChanges = docChanges;
  }

  @Override
  public String toString() {
    Set<DiffEntry> deltaSourceChanges = new HashSet<>(sourceChanges);
    deltaSourceChanges.removeAll(docChanges);

    StringBuilder sb = new StringBuilder();
    sb.append("The following source changes are not present in the tutorial text:\n");
    for (DiffEntry ds : deltaSourceChanges) {
      sb.append(String.format("\t%s\n", ds));
    }

    return sb.toString();
  }
}
