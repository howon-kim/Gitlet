package gitlet;

import java.io.File;
import java.io.Serializable;

public class Blob<T> implements Serializable {

    private byte[] content;
    private String hashID;
    //private Boolean isChanged;

    public Blob() {
        content = null;
        hashID = null;
        //isChanged = false;
    }

    public Blob(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println(Utils.error("File does not exist.").getMessage());
        } else {
            content = Utils.readContents(file);
            //isChanged = true;
            hashID = Utils.sha1(content);
        }
    }

    public String getHashID() {
        return hashID;
    }

    public byte[] getContent() {
        return content;
    }

}
