package objects;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by wackloner on 22-Mar-17.
 */
public class Tree extends TreeNode implements Serializable {
    public static final String TYPE = "tree";

    private List<Edge> children;

    public Tree() {
        children = Collections.emptyList();
    }

    public class Edge implements Serializable {
        private String name;
        private String nodeType;
        private String nodeHash;
    }
}
