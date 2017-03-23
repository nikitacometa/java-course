package gitpro;

import gitpro.exceptions.GitPROException;
import gitpro.objects.*;
import gitpro.utils.ObjectIO;
import gitpro.utils.SHA1Encoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wackloner on 21-Mar-17.
 */
public class CommandHandler {
    private final static String GIT_FOLDER_NAME = ".gitPRO";
    private final static String OBJECTS_FOLDER = "objects";
    private final static String BRANCHES_FOLDER = "branches";

    private final static String HEAD_FILE_NAME = "HEAD";
    private final static String INDEX_FILE_NAME = "index";

    private final static String MASTER_BRANCH_NAME = "master";

    private final Path currentDirectory;
    private final Path gitDirectory;
    private final Path objectsDirectory;
    private final Path branchesDirectory;

    private Index index = null;

    CommandHandler(Path currentDirectory) {
        this.currentDirectory = currentDirectory;
        this.gitDirectory = getFilePath(GIT_FOLDER_NAME);
        this.objectsDirectory = getGitFilePath(OBJECTS_FOLDER);
        this.branchesDirectory = getGitFilePath(BRANCHES_FOLDER);
    }

    void initRepository() throws GitPROException {
        if (Files.exists(gitDirectory)) {
            throw new GitPROException("Fail to init new repository, repository already exists in current directory.");
        }
        try {
            Files.createDirectory(gitDirectory);
            Files.createDirectory(objectsDirectory);
            Files.createDirectory(branchesDirectory);

            Index emptyIndex = new Index();
            writeIndex(emptyIndex);

            updateHead(Branch.TYPE, MASTER_BRANCH_NAME);

            Tree emptyTree = new Tree();
            writeTree(emptyTree);

            String emptyTreeHash = SHA1Encoder.getHash(emptyTree);
            Commit initCommit = new Commit("init commit", emptyTreeHash, null);
            writeCommit(initCommit);

            String initCommitHash = SHA1Encoder.getHash(initCommit);
            Branch masterBranch = new Branch(MASTER_BRANCH_NAME, initCommitHash);
            writeBranch(masterBranch);
        } catch (IOException e) {
            throw new GitPROException("Failed to init new repository. ", e);
        }
    }

    void indexFile(String fileName) throws GitPROException {
        Path filePath = getFilePath(fileName);
        Index index = getIndex();
        index.addFile(filePath);
        if (!Files.isDirectory(filePath)) {
            index.addFile(filePath.getParent());
        }
        writeIndex(index);
    }

    void commit(String message) throws GitPROException {
        Tree currentTree = buildTree(currentDirectory);
        writeTree(currentTree);
        String treeHash = SHA1Encoder.getHash(currentTree);
        Commit newCommit = new Commit(message, treeHash, getLastRevisionHash());
        writeCommit(newCommit);
        String commitHash = SHA1Encoder.getHash(newCommit);
        Head currentHead = getHead();
        if (currentHead.getRevisionType().equals(Branch.TYPE)) {
            Branch currentBranch = getBranch(currentHead.getRevisionName());
            currentBranch.setCommitHash(commitHash);
            writeBranch(currentBranch);
        } else {
            updateHead(Commit.TYPE, commitHash);
        }
    }

    private Tree buildTree(Path directory) throws GitPROException {
        Index index = getIndex();
        try {
            List<Path> paths = Files.list(directory)
                    .filter(index::contains)
                    .collect(Collectors.toList());
            Tree tree = new Tree();
            for (Path path : paths) {
                tree.addChildren(getEdge(path));
            }
            return tree;
        } catch (IOException e) {
            throw new GitPROException("Failed to list files in directory: ", e);
        }
    }

    private Tree.Edge getEdge(Path path) throws GitPROException {
        if (Files.isDirectory(path)) {
            Tree tree = buildTree(path);
            String hash = SHA1Encoder.getHash(tree);
            return new Tree.Edge(path, Tree.TYPE, hash);
        } else {
            Blob blob = makeBlob(path);
            String hash = SHA1Encoder.getHash(blob);
            return new Tree.Edge(path, Blob.TYPE, hash);
        }
    }

    private Blob makeBlob(Path file) throws GitPROException {
        try {
            byte[] content = Files.readAllBytes(file);
            return new Blob(content);
        } catch (IOException e) {
            throw new GitPROException("Failed to read file: ", e);
        }
    }

    void checkout(String revisionName) throws GitPROException {
        if (branchExists(revisionName)) {
            Branch toBranch = getBranch(revisionName);

        } else if (commitExists(revisionName)) {
            Commit toCommit = getCommit(revisionName);

        } else {
            throw new GitPROException("Failed to checkout revision " + revisionName + "! No such revision exists.");
        }
    }

    void merge(String branchName) throws GitPROException {
        if (!branchExists(branchName)) {
            throw new GitPROException("Failed to merge! Branch " + branchName + " doesn't exist!");
        }
        Branch mergingBranch = getBranch(branchName);
        Commit mergingCommit = getCommit(mergingBranch.getCommitHash());
        Tree mergingTree = getTree(mergingCommit.getTreeHash());

        Commit currentCommit;
        if (getHead().getRevisionType().equals(Commit.TYPE)) {
            currentCommit = getCommit(getHead().getRevisionName());
        } else {
            Branch currentBranch = getBranch(getHead().getRevisionName());
            currentCommit = getCommit(currentBranch.getCommitHash());
        }
        Tree currentTree = getTree(currentCommit.getTreeHash());

        Tree newTree = mergeTrees(currentTree, mergingTree);
        writeTree(newTree);

        updateFileSystem(newTree);

        String treeHash = SHA1Encoder.getHash(newTree);
        String previousCommitHash = SHA1Encoder.getHash(currentCommit);
        Commit newCommit = new Commit("merge branch " + branchName, treeHash, previousCommitHash);
        writeCommit(newCommit);

        String newCommitHash = SHA1Encoder.getHash(newCommit);
        if (getHead().getRevisionType().equals(Commit.TYPE)) {
            updateHead(Commit.TYPE, newCommitHash);
        } else {
            Branch currentBranch = getBranch(getHead().getRevisionName());
            currentBranch.setCommitHash(newCommitHash);
            writeBranch(currentBranch);
        }
    }

    private Tree mergeTrees(Tree currentTree, Tree mergingTree) throws GitPROException {
        Tree resultTree = new Tree();

        List<Tree.Edge> currentChildren = currentTree.getChildren();
        List<Tree.Edge> mergingChildren = mergingTree.getChildren();
        currentChildren.sort(Comparator.comparing(Tree.Edge::getPath));
        mergingChildren.sort(Comparator.comparing(Tree.Edge::getPath));

        int i = 0, j = 0;
        while (i < currentChildren.size() && j < mergingChildren.size()) {
            Tree.Edge first = currentChildren.get(i);
            Tree.Edge second = mergingChildren.get(j);
            int compare = first.getPath().compareTo(second.getPath());
            if (compare == 0) {
                if (first.getNodeType().equals(Blob.TYPE)) {
                    resultTree.addChildren(second);
                } else {
                    Tree firstTree = getTree(first.getNodeHash());
                    Tree secondTree = getTree(second.getNodeHash());

                    Tree yoloTree = mergeTrees(firstTree, secondTree);
                    writeTree(yoloTree);
                    String yoloTreeHash = SHA1Encoder.getHash(yoloTree);
                    Tree.Edge newEdge = new Tree.Edge(first.getPath(), Tree.TYPE, yoloTreeHash);
                    resultTree.addChildren(newEdge);
                }
                i++;
                j++;
            } else if (compare < 0) {
                resultTree.addChildren(first);
                i++;
            } else {
                resultTree.addChildren(second);
                j++;
            }
        }
        while (i < currentChildren.size()) {
            resultTree.addChildren(currentChildren.get(i++));
        }
        while (j < mergingChildren.size()) {
            resultTree.addChildren(mergingChildren.get(j++));
        }
        return resultTree;
    }

    private void updateFileSystem(Tree tree) throws GitPROException {
        updateIndex(tree);
    }

    private void updateIndex(Tree tree) throws GitPROException {
        Index index = getIndex();
        index.clear();
        fillIndex(tree, index);
        writeIndex(index);
    }

    private void fillIndex(Tree tree, Index index) throws GitPROException {
        for (Tree.Edge edge : tree.getChildren()) {
            index.addFile(edge.getPath());
            if (edge.getNodeType().equals(Tree.TYPE)) {
                Tree childTree = getTree(edge.getNodeHash());
                fillIndex(childTree, index);
            }
        }
    }

    void createBranch(String branchName) throws GitPROException {
        if (branchExists(branchName)) {
            throw new GitPROException("Failed to create branch: branch with specified name already exists");
        }
        String lastRevisionHash = getLastRevisionHash();
        Branch newBranch = new Branch(branchName, lastRevisionHash);
        Path branchPath = getBranchPath(branchName);
        ObjectIO.writeObject(branchPath, newBranch);
    }

    void deleteBranch(String branchName) throws GitPROException {
        if (!branchExists(branchName)) {
            throw new GitPROException("Failed to delete branch: branch with specified name doesn't exist.");
        }
        Head head = getHead();
        if (head.getRevisionType().equals(Branch.TYPE) && head.getRevisionName().equals(branchName)) {
            throw new GitPROException("Failed to delete branch: currently on specified branch.");
        }
        Path branchPath = getBranchPath(branchName);
        try {
            Files.delete(branchPath);
        } catch (IOException e) {
            throw new GitPROException("Failed to delete branch: ", e);
        }
    }

    List<String> getLog() throws GitPROException {
        List<String> result = new ArrayList<>();
        String commitHash = getLastRevisionHash();
        while (true) {
            Commit commit = getCommit(commitHash);
            result.add(commitToString(commit));
            commitHash = commit.getPreviousCommitHash();
            if (commitHash == null) {
                break;
            }
        }
        return result;
    }

    private void updateHead(String type, String name) throws GitPROException {
        Head newHead = new Head(type, name);
        writeHead(newHead);
    }

    private String getLastRevisionHash() throws GitPROException {
        Head head = getHead();
        if (head.getRevisionType().equals(Commit.TYPE)) {
            return head.getRevisionName();
        } else {
            Branch currentBranch = getBranch(head.getRevisionName());
            return currentBranch.getCommitHash();
        }
    }

    void loadRepository() {

    }

    private boolean branchExists(String branchName) {
        Path branchPath = getBranchPath(branchName);
        return Files.exists(branchPath);
    }

    private boolean commitExists(String commitHash) throws GitPROException {
        Path commitPath = getGitObjectPath(commitHash);
        if (!Files.exists(commitPath)) {
            return false;
        }
        Object object = ObjectIO.readObject(commitPath);
        return object instanceof Commit;
    }

    private Branch getBranch(String branchName) throws GitPROException {
        Path path = getBranchPath(branchName);
        return (Branch) ObjectIO.readObject(path);
    }

    private void writeBranch(Branch branch) throws GitPROException {
        Path path = getBranchPath(branch.getName());
        ObjectIO.writeObject(path, branch);
    }

    private Tree getTree(String treeHash) throws GitPROException {
        Path treePath = getGitObjectPath(treeHash);
        return (Tree) ObjectIO.readObject(treePath);
    }

    private void writeTree(Tree tree) throws GitPROException {
        String hash = SHA1Encoder.getHash(tree);
        Path path = getGitObjectPath(hash);
        ObjectIO.writeObject(path, tree);
    }

    private Commit getCommit(String hash) throws GitPROException {
        return (Commit) ObjectIO.readObject(getGitObjectPath(hash));
    }

    private void writeCommit(Commit commit) throws GitPROException {
        String hash = SHA1Encoder.getHash(commit);
        Path path = getGitObjectPath(hash);
        ObjectIO.writeObject(path, commit);
    }

    private Head getHead() throws GitPROException {
        return (Head) ObjectIO.readObject(getGitFilePath(HEAD_FILE_NAME));
    }

    private void writeHead(Head head) throws GitPROException {
        ObjectIO.writeObject(getGitFilePath(HEAD_FILE_NAME), head);
    }

    private Index getIndex() throws GitPROException {
        if (index == null) {
            index = (Index) ObjectIO.readObject(getGitFilePath(INDEX_FILE_NAME));
        }
        return index;
    }

    private void writeIndex(Index index) throws GitPROException {
        ObjectIO.writeObject(getGitFilePath(INDEX_FILE_NAME), index);
    }

    private Path getFilePath(String fileName) {
        return Paths.get(currentDirectory.toString(), fileName);
    }

    private Path getGitFilePath(String fileName) {
        return Paths.get(gitDirectory.toString(), fileName);
    }

    private Path getGitObjectPath(String objectName) {
        return Paths.get(objectsDirectory.toString(), objectName);
    }

    private Path getBranchPath(String branchName) {
        return Paths.get(branchesDirectory.toString(), branchName);
    }

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");

    private String commitToString(Commit commit) throws GitPROException {
        String commitHash = SHA1Encoder.getHash(commit);
        return commitHash + " on " + DATE_FORMAT.format(commit.getDate()) + " by " +
                commit.getAuthor() + ": " + commit.getMessage();
    }
}
