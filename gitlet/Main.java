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
                if (args.length <= 1) { System.out.println(Utils.error("Please enter a commit message.").getMessage()); }
                else { cmdcommit(args[1]); }
                break;
            case "checkout":
                if (args.length <= 1) { System.out.println(Utils.error("Please enter a commit message.").getMessage()); }
                else { cmdcheckout(Arrays.copyOfRange(args, 1, args.length)); }
                break;
            case "log":
                cmdlog();
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

        }
    }

    public static void cmdadd(String... args) {
        File file = new File(FILEPATH);
        File stageFile = new File(STAGEPATH);
        if (!file.exists()) {
            System.out.println(Utils.error("Not in an initialized Gitlet directory."));
        } else {
            Stage stage = new Stage();
            if (stageFile.exists()) {
                stage = Utils.readObject(stageFile, Stage.class);
            }

            Blob blob = new Blob(args[0]);
            Commit current = Utils.readObject(new File(COMMITPATH), CommitTree.class).getCurrentCommit();
            //File filepath = Utils.join(STAGEPATH, blob.getHashID());
            //Utils.writeObject(filepath, blob);
            //Utils.readObject(filepath, Blob.class);


            if (!current.checkBlobEquality(args[0], blob)) {
                stage.addBlob(args[0], blob);
                Utils.writeObject(stageFile, stage);
            }
        }
    }

    public static void cmdcommit(String... args) {
        File file = new File(FILEPATH);
        File stageFile = new File(STAGEPATH);
        File commitFile = new File(COMMITPATH);
        if (!stageFile.exists()) {
            System.out.println(Utils.error("No changes added to the commit."));
        } else {
            Commit current = Utils.readObject(new File(COMMITPATH), CommitTree.class).getCurrentCommit();
            Stage stage = Utils.readObject(stageFile, Stage.class);
            HashMap<String, Blob> files = current.getFiles();

            for (Map.Entry entry: stage.getBlobs().entrySet()) {
                String key = (String) entry.getKey();
                Blob blob = (Blob) entry.getValue();
                files.put(key, blob);
            }

            Commit newCommit = new Commit(args[0], Instant.now().getEpochSecond(), files, current);
            CommitTree commitTree = Utils.readObject(commitFile, CommitTree.class);
            commitTree.addCommit(newCommit);
            Utils.writeObject(commitFile, commitTree);
            stageFile.delete();
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
        } else if (args[1].equals("--")) {
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
            Commit foundBranch = commitTree.findBranchCommit(args[0]);

            if (commitTree.getCurrentBranch() == args[0]) {
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
                for (Map.Entry entry: foundBranch.getFiles().entrySet()) {
                    String key = (String) entry.getKey();
                    Blob blob = (Blob) entry.getValue();
                    Utils.writeObject(new File(key), blob);
                }
                commitTree.putCurrentBranch(args[0]);
                Utils.writeObject(commitFile, commitTree);
                stageFile.delete();
            } else {
                System.out.println(Utils.error( "No such branch exists.").getMessage());
            }
        }
    }

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



}
