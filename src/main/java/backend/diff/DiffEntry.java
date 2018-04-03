package backend.diff;

public class DiffEntry {
  public enum ChangeType {
    ADD, MODIFY, DELETE
  }

  private ChangeType changeType;
  private String filename;

  public DiffEntry(String filename, ChangeType changeType) {
    this.filename = filename;
    this.changeType = changeType;
  }

  @Override
  public String toString() {
    String s;
    switch (changeType) {
      case ADD:
        s = "Added: ";
        break;

      case MODIFY:
        s = "Modified: ";
        break;

      case DELETE:
        s = "Deleted: ";
        break;

      default:
        throw new IllegalStateException("Change type must not be null");
    }

    return s + filename;
  }

  @Override
  public int hashCode() {
    return changeType.hashCode() * filename.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }

    final DiffEntry other = (DiffEntry) obj;
    return this.hashCode() == other.hashCode();
  }
}
