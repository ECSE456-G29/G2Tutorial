package database;

public interface IDatabase {
  /**
   * Adds a commit SHA to the provided step record.
   * 
   * @param step the step the record should be added to
   * @param sha the SHA256 hash of the corresponding commit
   * @throws DatabaseException issues processing the database
   */
  public void addSha(int step, String sha) throws DatabaseException;
}
