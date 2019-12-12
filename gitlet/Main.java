package gitlet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    static String FILEPATH = ".gitlet";
    static String STAGEPATH = ".gitlet/.STAGE";
    static String COMMITPATH = ".gitlet/.COMMIT";
    static String WORKINGPATH = System.getProperty("user.dir");

    public static void main(String... args) {

        if (args.length <= 0) {
            throw Utils.error("Please enter a command.");
        }

        switch (args[0]) {
            case "init":
                cmdinit();
                break;
            case "add":
                if (args.length <= 1) { throw Utils.error("Incorrect operands"); }
                cmdadd(args[1]);
                break;
            case "commit":
                if (args.length <= 1 || args[1].isEmpty()) { System.out.println(Utils.error("Please enter a commit message.").getMessage()); }
                else { cmdcommit(args[1]); }
                break;
            case "checkout":
                if (args.length <= 1) { System.out.println(Utils.error("Please enter a commit message.").getMessage()); }
                if (args.length > 2 && !args[2].equals("--")) {System.out.println(Utils.error("Incorrect operands").getMessage()); }
                else { cmdcheckout(Arrays.copyOfRange(args, 1, args.length)); }
                break;
            case "log":
                cmdlog();
                break;
            case "global-log":
                cmdgloballog();
                break;
            case "find":
                if (args.length <= 1) { throw Utils.error("Incorrect operands"); }
                cmdfind(args[1]);
                break;
            case "status":
                cmdstatus();
                break;
            case "rm":
                if (args.length <= 1) { throw Utils.error("Incorrect operands"); }
                cmdrm(args[1]);
                break;
            case "branch":
                if (args.length <= 1) { throw Utils.error("Incorrect operands"); }
                cmdbranch(args[1]);
                break;
            case "reset":
                if (args.length <= 1) { throw Utils.error("Incorrect operands"); }
                cmdreset(args[1]);
                break;
            default:
                throw Utils.error("No command with that name exists.");
        }




    }

    public static void cmdinit(String... args) {
        File file = new File(FILEPATH);
        if (file.exists()) {
            System.out.println(Utils.error(" A Gitlet version-control system already exists in the current directory.").getMessage());
        } else {
            file.mkdir();
            //new File(STAGEPATH).mkdir();
            Commit commit = new Commit();
            CommitTree commitTree = new CommitTree();
            //file = Utils.join(FILEPATH, commit.getHashID());
            //Utils.writeObject(file, commit);
            commitTree.addCommit(commit);
            file = new File(COMMITPATH);
            Utils.writeObject(file, commitTree);

            Stage stage = new Stage();
            Utils.writeObject(new File(STAGEPATH), stage);
        }
    }

    public static void cmdadd(String... args) {
        File file = new File(FILEPATH);
        File stageFile = new File(STAGEPATH);
        File addedFile = new File(args[0]);
        if (!file.exists()) {
            System.out.println(Utils.error("Not in an initialized Gitlet directory.").getMessage());
        } else if (!addedFile.exists()) {
            System.out.println(Utils.error("File does not exist.").getMessage());
        } else {
            Stage stage = new Stage();
            stage = Utils.readObject(stageFile, Stage.class);

            Blob blob = new Blob(args[0]);
            CommitTree commitTree = Utils.readObject(new File(COMMITPATH), CommitTree.class);
            Commit current = commitTree.findCommit(commitTree.head.get(commitTree.currentBranch));
            //File filepath = Utils.join(STAGEPATH, blob.getHashID());
            //Utils.writeObject(filepath, blob);
            //Utils.readObject(filepath, Blob.class);

            if (!current.checkBlobEquality(args[0], blob)) {
                stage.addBlob(args[0], blob);
                Utils.writeObject(stageFile, stage);
            } else {
                if(current.getFiles().get(args[0]).addTrack()) {
                    Utils.writeObject(new File(COMMITPATH), commitTree);
                }
            }
        }
    }

    public static void cmdcommit(String... args) {
        File file = new File(FILEPATH);
        File stageFile = new File(STAGEPATH);
        File commitFile = new File(COMMITPATH);
        if (Utils.readObject(stageFile, Stage.class).getBlobs().isEmpty() && Utils.readObject(commitFile, CommitTree.class).getUntrackedFile().isEmpty()) {
            System.out.println(Utils.error("No changes added to the commit.").getMessage());
        } else {
            CommitTree commitTree = Utils.readObject(new File(COMMITPATH), CommitTree.class);
            Commit current = commitTree.findCommit(commitTree.head.get(commitTree.currentBranch));
            Stage stage = Utils.readObject(stageFile, Stage.class);
            HashMap<String, Blob> files = current.getFiles();

            if (!stage.getBlobs().isEmpty()) {
                for (Map.Entry entry : stage.getBlobs().entrySet()) {
                    String key = (String) entry.getKey();
                    Blob blob = (Blob) entry.getValue();
                    files.put(key, blob);
                }
            }
            if (!commitTree.getUntrackedFile().isEmpty()) {
                for (String fileName : commitTree.getUntrackedFile()) {
                    files.remove(fileName);
                }
                commitTree.clearRemovedFile();
            }

            Commit newCommit = new Commit(args[0], files, current);
            commitTree.addCommit(newCommit);
            commitTree.head.put(commitTree.currentBranch, newCommit.getHashID());
            Utils.writeObject(commitFile, commitTree);
            Utils.writeObject(stageFile, new Stage());
        }


    }

    public static void cmdcheckout(String... args) {
        File file = new File(FILEPATH);
        File commitFile = new File(COMMITPATH);
        File stageFile = new File(STAGEPATH);

        if (args[0].equals("--")) {
            Commit current = Utils.readObject(commitFile, CommitTree.class).getCurrentCommit();
            HashMap<String, Blob> files = current.getFiles();
            if (files.containsKey(args[1])) {
                Utils.writeContents(new File(args[1]), files.get(args[1]).getContent());
            } else {
                throw Utils.error("File does not exist in that commit.");
            }
        } else if (args.length > 1 && args[1].equals("--")) {
            CommitTree commitTree = Utils.readObject(commitFile, CommitTree.class);
            Commit foundCommit = commitTree.findCommit(args[0]);
            if (foundCommit != null) {
                HashMap<String, Blob> files = foundCommit.getFiles();
                if (files.containsKey(args[2])) {
                    Utils.writeContents(new File(args[2]), files.get(args[2]).getContent());
                } else {
                    System.out.println(Utils.error("File does not exist in that commit.").getMessage());
                }
            } else {
                System.out.println(Utils.error("No commit with that id exists.").getMessage());
            }
        } else {
            CommitTree commitTree = Utils.readObject(commitFile, CommitTree.class);
            Commit foundBranch = commitTree.commitsCopy.get(commitTree.head.get(args[0]));

            if (commitTree.getCurrentBranch().equals(args[0])) {
                System.out.println(Utils.error("No need to checkout the current branch.").getMessage());
            } else if (foundBranch != null) {
                if(stageFile.exists()) {
                    Stage stage = Utils.readObject(stageFile, Stage.class);
                    HashMap<String, Blob> stageFiles = stage.getBlobs();
                    for (Map.Entry entry: foundBranch.getFiles().entrySet()) {
                        String key = (String) entry.getKey();
                        Blob blob = (Blob) entry.getValue();
                        if (stageFiles.containsKey(key)) {
                            throw Utils.error("There is an untracked file in the way; delete it or add it first.");
                        }
                    }
                }
                /**
                for (Map.Entry entry: foundBranch.getFiles().entrySet()) {
                    String key = (String) entry.getKey();
                    Blob blob = (Blob) entry.getValue();
                    Utils.writeObject(new File(key), blob);
                }
                 **/
                Stage stage = Utils.readObject(stageFile, Stage.class);
                HashMap<String, Blob> files = foundBranch.getFiles();
                List<String> CurrentFiles = Utils.plainFilenamesIn(new File(WORKINGPATH));
                for (Map.Entry entry : stage.getBlobs().entrySet()) {
                    Utils.writeObject(new File((String) entry.getKey()), (Blob) entry.getValue());
                    if (CurrentFiles.contains((String) entry.getKey())) {
                        CurrentFiles.remove((String) entry.getKey());
                    }
                }
                for (String c : CurrentFiles){
                    Utils.restrictedDelete(new File(c));
                }
                Utils.writeObject(new File(STAGEPATH), new Stage());
                commitTree.putCurrentBranch(args[0]);
                commitTree.head.put(args[0], foundBranch.getHashID());
                Utils.writeObject(commitFile, commitTree);


            } else {
                System.out.println(Utils.error( "No such branch exists.").getMessage());
            }
        }
    }

    /***
     * Starting at the current head commit, display information about each commit
     */
    public static void cmdlog() {
        File commitFile = new File(COMMITPATH);
        CommitTree commitTree = Utils.readObject(commitFile, CommitTree.class);
        LinkedList<Commit> commits = commitTree.getCurrentBranchCommits();
        ListIterator commitsIter = commits.listIterator();

        while (commitsIter.hasNext()) {
            Commit commit = (Commit) commitsIter.next();
            System.out.println("===");
            System.out.println("commit " + commit.hashID);
            System.out.println("Date: " + commit.getTimestamp());
            System.out.println(commit.getComment());
            System.out.println();
        }
    }

    /***
     * Like log, except displays information about all commits ever made.
     * The order of the commits does not matter.
     */
    public static void cmdgloballog() {
        File commitFile = new File(COMMITPATH);
        CommitTree commitTree = Utils.readObject(commitFile, CommitTree.class);
        HashMap<String, Commit> commits = commitTree.getAllCommits();

        for (Commit entry : commits.values()) {
            System.out.println("===");
            System.out.println("commit " + entry.getHashID());
            System.out.println("Date: " + entry.getTimestamp());
            System.out.println(entry.getComment());
            System.out.println();
    }
}

    /***
     * Prints out the ids of all commits that have the given commit message, one per line.
     */
    public static void cmdfind(String ...args) {
        File commitFile = new File(COMMITPATH);
        CommitTree commitTree = Utils.readObject(commitFile, CommitTree.class);
        HashMap<String, Commit> commits = commitTree.getAllCommits();
        Boolean isFound = false;

        for (Commit entry : commits.values()) {
            if (entry.getComment().equals(args[0])) {
                isFound = true;
                System.out.println(entry.getHashID());
            }
        }

        if (isFound == false) {
            System.out.println(Utils.error("Found no commit with that message.").getMessage());
        }
    }

    /***
     * Displays what branches currently exist, and marks the current branch with a *.
     * Also displays what files have been staged or marked for untracking.
     */
    public static void cmdstatus() {
        //for (String name: Utils.plainFilenamesIn(WORKINGPATH)) {
         //   System.out.println(name);
        //}
        File commitFile = new File(COMMITPATH);
        File stageFile = new File(STAGEPATH);
        CommitTree commitTree = Utils.readObject(commitFile, CommitTree.class);
        HashMap<String, LinkedList<Commit>> commits = commitTree.getCommitsBranch();
        System.out.println("=== Branches ===");
        for (String branch: commits.keySet()) {
            if (branch == commitTree.currentBranch) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }

        Stage stage = Utils.readObject(stageFile, Stage.class);
        System.out.println("\n=== Staged Files ===");
        List<String> stageFiles = new ArrayList<>();
        for (String fileName: stage.getBlobs().keySet()) {
            stageFiles.add(fileName);
        }
        Collections.sort(stageFiles);
        for (String fileName: stageFiles) {
            System.out.println(fileName);
        }

        Commit currentCommit = commitTree.getCurrentCommit();
        HashMap<String, Blob> currentFiles = currentCommit.getFiles();
        System.out.println("\n=== Removed Files ===");
        for (Map.Entry entry: currentFiles.entrySet()) {
            String f = (String) entry.getKey();
            Blob b = (Blob) entry.getValue();
            if (b.getIsTracked() == false) {
                System.out.println(f);
            }
        }

        System.out.println("\n=== Modifications Not Staged For Commit ===");
        System.out.println("\n=== Untracked Files ===");


//!!!! EXTRA CREDIT SESSION


    }

    public static void cmdrm(String ...args) {
        CommitTree commitTree = Utils.readObject(new File(COMMITPATH), CommitTree.class);
        Stage stage = Utils.readObject(new File(STAGEPATH), Stage.class);
        Boolean isDeleted = false;
        if (stage.deleteBlob(args[0])) {
            Utils.writeObject(new File(STAGEPATH), stage);
            isDeleted = true;
        }

        Commit currentCommit = commitTree.getCurrentCommit();
        if (currentCommit.getFiles().containsKey(args[0])) {
            currentCommit.getFiles().get(args[0]).removeTrack();
            Utils.restrictedDelete(args[0]);
            commitTree.removeFile(args[0]);
            Utils.writeObject(new File(COMMITPATH), commitTree);
            isDeleted = true;
        }

        if (isDeleted == false) {
            System.out.println(Utils.error("No reason to remove the file.").getMessage());
        }

    }

    public static void cmdbranch(String ...args) {
        CommitTree commitTree = Utils.readObject(new File(COMMITPATH), CommitTree.class);
        commitTree.addBranch(args[0]);
        commitTree.currentBranch = args[0];
        Utils.writeObject(new File(COMMITPATH), commitTree);
    }

    public static void cmdreset(String ...args) {
        CommitTree commitTree = Utils.readObject(new File(COMMITPATH), CommitTree.class);
        if (!commitTree.commitsCopy.containsKey(args[0])) {
            System.out.println(Utils.error(" No commit with that id exists.").getMessage());
        }
        Commit commit = commitTree.commitsCopy.get(args[0]);
        List<String> workingFiles = Utils.plainFilenamesIn(WORKINGPATH);
        HashMap<String, Blob> files = commit.getFiles();

        for (int i = 0; i < workingFiles.size() ; i++) {
            if (!files.containsKey(workingFiles.get(i))) {
                System.out.println(workingFiles.get(i));
                throw Utils.error("There is an untracked file in the way; delete it or add it first");
            }
        }

        Utils.writeObject(new File(STAGEPATH), new Stage());
        commitTree.head.put(commitTree.currentBranch, args[0]);
        Utils.writeObject(new File(COMMITPATH), commitTree);



    }



}
