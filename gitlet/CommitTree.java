package gitlet;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CommitTree implements Serializable {
    String DEFAULT_BRANCH = "master";

    HashMap<String, ArrayList<Commit>> commits;
    HashMap<String, Commit> commitsCopy;
    HashMap<String, ArrayList<String>> untracked;
    HashMap<String, Commit> head;

    String currentBranch;

    public CommitTree() {
        commits = new HashMap<>();
        commitsCopy = new HashMap<>();
        currentBranch = DEFAULT_BRANCH;
        untracked = new HashMap<>();
        head = new HashMap<>();

    }

    public void addCommit(Commit commit) {
        if (commits.get(currentBranch) == null) {
            ArrayList<Commit> commits = new ArrayList<>();
            commits.add(0, commit);
            this.commits.put(currentBranch, commits);
            ArrayList<String> deleted = new ArrayList<>();
            untracked.put(currentBranch, deleted);
        } else {
            ArrayList<Commit> commits = this.commits.get(currentBranch);
            commits.add(0, commit);
            this.commits.put(currentBranch, commits);
        }
        commitsCopy.put(commit.getHashID(), commit);
        head.put(currentBranch, commit);
    }

    public void addBranch(String branch) {
        ArrayList<Commit> commits = new ArrayList<>();
        commits.add(0, head.get(currentBranch));
        this.commits.put(branch, commits);
        ArrayList<String> deleted = new ArrayList<>();
        untracked.put(branch, deleted);
        head.put(branch, head.get(currentBranch));
    }

    public Boolean findBranch (String branch) {
        return commits.containsKey(branch);
    }


    public List<String> getUntrackedFile() {
        return untracked.get(currentBranch);
    }


    public void clearRemovedFile() {
        ArrayList<String> deleted = new ArrayList<>();
        untracked.put(currentBranch, deleted);
    }

    public void removeFile(String file) {
        if (commits.containsKey(currentBranch)) {
            if (commits.get(currentBranch).get(0).getFiles().containsKey(file)) {
                untracked.get(currentBranch).add(file);
            }
        }
    }

    public Commit getCurrentCommit() {
        return this.commits.get(currentBranch).get(0);
    }

    public Commit getCurrentHead() {
        return this.head.get(currentBranch);
    }

    public Commit findCommit(String commitID) {
        if (commitsCopy.containsKey(commitID)) {
            return commitsCopy.get(commitID);
        } else {
            return null;
        }
    }

    public Commit findBranchCommit(String branch) {
        if (head.containsKey(branch)) {
            return head.get(branch);
        } else {
            return null;
        }
    }

    public ArrayList<Commit> getCurrentBranchCommits() {
        return this.commits.get(currentBranch);
    }

    public HashMap<String, Commit> getAllCommits() {
        return this.commitsCopy;
    }

    public HashMap<String, ArrayList<Commit>> getCommitsBranch() {
        return this.commits;
    }

    public String getCurrentBranch() {
        return currentBranch;
    }


    public void putCurrentBranch(String branch) {
        currentBranch = branch;
    }

}
