package gitlet;

import java.util.HashMap;

public class Tree {
    HashMap<String, Commit> branch;

    public Tree() {
        branch = new HashMap<>();
        branch.put("master", new Commit());
    }
}
