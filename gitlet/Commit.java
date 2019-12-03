package gitlet;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

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
        this.hashID = Utils.sha1(files.keySet().toArray());
    }

    public String getHashID(){
        return hashID;
    }

    public boolean checkBlobEquality(String fileName, Blob compare) {
        if (files.containsKey(fileName)) {
            if (files.get(fileName).getHashID() == compare.getHashID()) {
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
