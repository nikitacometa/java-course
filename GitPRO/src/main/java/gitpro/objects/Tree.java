package gitpro.objects;

import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;
import java.nio.file.Path;
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

    public void addChildren(Edge edge) {
        children.add(edge);
    }

    @AllArgsConstructor
    public static class Edge implements Serializable {
        private Path path;
        private String nodeType;
        private String nodeHash;
    }
}
