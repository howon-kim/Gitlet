package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

public class Stage implements Serializable {
    private HashMap<String, Blob> blobs;

    public Stage() {
        blobs = new HashMap<>();
    }

    public void addBlob(String filename, Blob blob) {
        blobs.put(filename, blob);
    }

    public Boolean deleteBlob(String filename) {
        if (blobs.containsKey(filename)) {
            blobs.remove(filename);
            return true;
        } else {
            return false;
        }
    }

    public HashMap<String, Blob> getBlobs() {
        return blobs;
    }
}
