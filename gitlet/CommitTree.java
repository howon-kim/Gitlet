package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

public class CommitTree implements Serializable {
    HashMap<String, LinkedList<Commit>> commits;
    HashMap<String, Commit> commitsCopy;
    String currentBranch;

    public CommitTree() {
        commits = new HashMap<>();
        commitsCopy = new HashMap<>();
        currentBranch = "master";
    }

    public void addCommit(Commit commit) {
        if (commits.get("master") == null) {
            LinkedList<Commit> commits = new LinkedList<>();
            commits.addFirst(commit);
            this.commits.put("master", commits);
        } else {
            LinkedList<Commit> commits = this.commits.get("master");
            commits.addFirst(commit);
            this.commits.put("master", commits);
        }
        commitsCopy.put(commit.getHashID(), commit);
    }

    public void addCommit(Commit commit, String branch) {
        if (commits.get(branch) == null) {
            LinkedList<Commit> commits = new LinkedList<>();
            commits.addFirst(commit);
            this.commits.put(branch, commits);
        } else {
            LinkedList<Commit> commits = this.commits.get(branch);
            commits.addFirst(commit);
            this.commits.put(branch, commits);
        }
        commitsCopy.put(commit.getHashID(), commit);
    }

    public Commit getCurrentCommit() {
        return this.commits.get(currentBranch).getFirst();
    }

    public Commit getCurrentCommit(String branch) {
        return this.commits.get(branch).getFirst();
    }

    public Commit findCommit(String commitID) {
        if (commitsCopy.containsKey(commitID)) {
            return commitsCopy.get(commitID);
        } else {
            return null;
        }
    }

    public Commit findBranchCommit(String branch) {
        if (commits.containsKey(branch)) {
            return commits.get(branch).getFirst();
        } else {
            return null;
        }
    }

    public LinkedList<Commit> getCurrentBranchCommits() {
        return this.commits.get(currentBranch);
    }

    public String getCurrentBranch() {
        return currentBranch;
    }



    public void putCurrentBranch(String branch) {
        currentBranch = branch;
    }

}
