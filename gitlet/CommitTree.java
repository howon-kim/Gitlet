package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CommitTree implements Serializable {
    String DEFAULT_BRANCH = "master";
    HashMap<String, LinkedList<Commit>> commits;
    HashMap<String, Commit> commitsCopy;
    HashMap<String, ArrayList<String>> untracked;
    String currentBranch;

    public CommitTree() {
        commits = new HashMap<>();
        commitsCopy = new HashMap<>();
        currentBranch = DEFAULT_BRANCH;
        untracked = new HashMap<>();
    }

    public void addCommit(Commit commit) {
        if (commits.get(DEFAULT_BRANCH) == null) {
            LinkedList<Commit> commits = new LinkedList<>();
            commits.addFirst(commit);
            this.commits.put(DEFAULT_BRANCH, commits);
            ArrayList<String> deleted = new ArrayList<>();
            untracked.put(DEFAULT_BRANCH, deleted);
        } else {
            LinkedList<Commit> commits = this.commits.get(DEFAULT_BRANCH);
            commits.addFirst(commit);
            this.commits.put(DEFAULT_BRANCH, commits);
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

    public List<String> getUntrackedFile() {
        return untracked.get(DEFAULT_BRANCH);
    }

    public List<String> getUntrackedFile(String branch) {
        return untracked.get(branch);
    }

    public void clearRemovedFile() {
        ArrayList<String> deleted = new ArrayList<>();
        untracked.put(DEFAULT_BRANCH, deleted);
    }

    public void clearRemovedFile(String branch) {
        ArrayList<String> deleted = new ArrayList<>();
        untracked.put(branch, deleted);
    }

    public void removeFile(String file) {
        if (commits.containsKey(DEFAULT_BRANCH)) {
            if (commits.get(DEFAULT_BRANCH).getFirst().getFiles().containsKey(file)) {
                untracked.get(DEFAULT_BRANCH).add(file);
            }
        }
    }

    public void removeFile(String branch, String file) {
        if (commits.containsKey(branch)) {
            if (commits.get(branch).getFirst().getFiles().containsKey(file)) {
                untracked.get(branch).add(file);
            }
        }
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

    public HashMap<String, Commit> getAllCommits() {
        return this.commitsCopy;
    }

    public HashMap<String, LinkedList<Commit>> getCommitsBranch() {
        return this.commits;
    }

    public String getCurrentBranch() {
        return currentBranch;
    }



    public void putCurrentBranch(String branch) {
        currentBranch = branch;
    }

}
