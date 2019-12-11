package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob<T> implements Serializable {

    private byte[] content;
    private String hashID;
    private Boolean isTracked;

    public Blob() {
        content = null;
        hashID = null;
        isTracked = true;
    }

    public Blob(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println(Utils.error("File does not exist.").getMessage());
        } else {
            content = Utils.readContents(file);
            isTracked = true;
            hashID = Utils.sha1(content);
        }
    }

    public String getHashID() {
        return hashID;
    }

    public byte[] getContent() {
        return content;
    }

    public Boolean getIsTracked() {
        return isTracked;
    }

    public Boolean removeTrack() {
        if (isTracked == false) {
            return false;
        } else {
            isTracked = false;
            return true;
        }
    }

    public Boolean addTrack() {
        if (isTracked == true) {
            return false;
         } else {
            isTracked = true;
            return true;
        }
    }
}
