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

    public HashMap<String, Blob> getBlobs() {
        return blobs;
    }
}
