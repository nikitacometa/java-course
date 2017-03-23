package gitpro.objects;

import lombok.Getter;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wackloner on 22-Mar-17.
 */
public class Tree extends TreeNode {
    public static final String TYPE = "tree";

    @Getter
    private List<Edge> children;

    public Tree() {
        children = new ArrayList<>();
    }

    public void addChildren(Edge edge) {
        children.add(edge);
    }

    public static class Edge implements Serializable {
        private String path;
        @Getter
        private String nodeType;
        @Getter
        private String nodeHash;

        public Edge(Path path, String nodeType, String nodeHash) {
            this.path = path.toString();
            this.nodeType = nodeType;
            this.nodeHash = nodeHash;
        }

        public Path getPath() {
            return Paths.get(path);
        }
    }
}
