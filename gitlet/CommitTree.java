package gitlet;

import java.io.Serializable;
import java.util.LinkedList;

public class CommitTree implements Serializable {
    LinkedList<Commit> commits;

    public CommitTree() {
        commits = new LinkedList();
    }

    public void addCommit(Commit commit) {
        commits.addFirst(commit);
    }

    public Commit getCurrentCommit() {
        return commits.getFirst();
    }
}
