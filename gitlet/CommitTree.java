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
    HashMap<String, String> head;


    public CommitTree() {
        commits = new HashMap<>();
        commitsCopy = new HashMap<>();
        currentBranch = DEFAULT_BRANCH;
        untracked = new HashMap<>();
        head = new HashMap<>();
    }

    public void addBranch(String branch) {
        if (commits.containsKey(branch)) {
            System.out.println(Utils.error("A branch with that name already exists.").getMessage());
        } else {
            LinkedList<Commit> newBranch = new LinkedList<>();
            commits.put(branch, new LinkedList<>());
            head.put(branch, head.get(currentBranch));
            untracked.put(branch, new ArrayList<>());
        }
    }

    public void addCommit(Commit commit) {
        if (commits.get(currentBranch) == null) {
            LinkedList<Commit> commits = new LinkedList<>();
            commits.addFirst(commit);
            this.commits.put(currentBranch, commits);
            ArrayList<String> deleted = new ArrayList<>();
            untracked.put(currentBranch, deleted);
        } else {
            LinkedList<Commit> commits = this.commits.get(currentBranch);
            commits.addFirst(commit);
            this.commits.put(currentBranch, commits);
        }
        head.put(currentBranch, commit.getHashID());
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
        head.put(branch, commit.getHashID());
        commitsCopy.put(commit.getHashID(), commit);
    }

    public List<String> getUntrackedFile() {
        return untracked.get(currentBranch);
    }

    public List<String> getUntrackedFile(String branch) {
        return untracked.get(branch);
    }

    public void clearRemovedFile() {
        ArrayList<String> deleted = new ArrayList<>();
        untracked.put(currentBranch, deleted);
    }

    public void clearRemovedFile(String branch) {
        ArrayList<String> deleted = new ArrayList<>();
        untracked.put(branch, deleted);
    }

    public void removeFile(String file) {
        if (commits.containsKey(currentBranch)) {
            if (commits.get(currentBranch).getFirst().getFiles().containsKey(file)) {
                untracked.get(currentBranch).add(file);
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
