package gitlet;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Commit implements Serializable {
    String hashID;
    String comment;
    String timestamp;
    Commit parent;
    HashMap<String, Blob> files;


    public Commit() {
        comment = "initial commit";
        timestamp = Utils.makeTimeStamp(0);
        files = new HashMap<>();
        hashID = Utils.sha1(files.keySet().toArray());
        parent = this;
    }

    public Commit(String comment, Long timestamp, HashMap<String, Blob> files, Commit parent) {
        this.comment = comment;
        this.timestamp = Utils.makeTimeStamp(timestamp);
        this.files = files;
        this.parent = parent;
        List<Object> hashKeys = new ArrayList<>();
        for (Map.Entry entry: files.entrySet()) {
            String key = (String) entry.getKey();
            Blob blob = (Blob) entry.getValue();
            hashKeys.add(key);
            hashKeys.add(blob.getContent());
        }
        hashKeys.add(this.timestamp);
        this.hashID = Utils.sha1(hashKeys.toArray());
    }

    public Commit(String comment, HashMap<String, Blob> files, Commit parent) {
        this.comment = comment;
        this.timestamp = Utils.makeTimeStamp(System.currentTimeMillis());
        this.files = files;
        this.parent = parent;
        List<Object> hashKeys = new ArrayList<>();
        for (Map.Entry entry: files.entrySet()) {
            String key = (String) entry.getKey();
            Blob blob = (Blob) entry.getValue();
            hashKeys.add(key);
            hashKeys.add(blob.getContent());
        }
        this.hashID = Utils.sha1(hashKeys.toArray());
    }

    public String getHashID(){
        return hashID;
    }
    public String getTimestamp() { return timestamp; }
    public String getComment() { return comment; }

    public boolean checkBlobEquality(String fileName, Blob compare) {
        if (files.containsKey(fileName)) {
            if (files.get(fileName).getHashID().equals(compare.getHashID())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    public HashMap<String, Blob> getFiles() {
        return files;
    }




}
