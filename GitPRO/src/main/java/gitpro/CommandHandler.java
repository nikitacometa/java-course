package gitpro;

import gitpro.exceptions.GitPROException;
import gitpro.objects.*;
import gitpro.utils.CommitLog;
import gitpro.utils.ObjectIO;
import gitpro.utils.SHA1Encoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wackloner on 21-Mar-17.
 */
class CommandHandler {
    private final static String GIT_FOLDER_NAME = ".gitPRO";
    private final static String OBJECTS_FOLDER = "objects";
    private final static String BRANCHES_FOLDER = "branches";

    private final static String HEAD_FILE_NAME = "HEAD";
    private final static String INDEX_FILE_NAME = "index";

    private final static String MASTER_BRANCH_NAME = "master";

    private Path currentDirectory;
    private Path repositoryDirectory;
    private Path gitDirectory;
    private Path objectsDirectory;
    private Path branchesDirectory;

    private Index index = null;

    CommandHandler(Path currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    void initRepository() throws GitPROException {
        try {
            repositoryDirectory = currentDirectory;
            initDirectories();
            if (Files.exists(gitDirectory)) {
                throw new GitPROException("Repository already exists in current directory!");
            }

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

    private void initDirectories() {
        gitDirectory = Paths.get(repositoryDirectory.toString(), GIT_FOLDER_NAME);
        objectsDirectory = getGitFilePath(OBJECTS_FOLDER);
        branchesDirectory = getGitFilePath(BRANCHES_FOLDER);
    }

    void loadRepository() throws GitPROException {
        repositoryDirectory = findRepository(currentDirectory);
        if (repositoryDirectory == null) {
            throw new GitPROException("No gitPRO repository was found!");
        }
        initDirectories();
    }

    private Path findRepository(Path directory) {
        if (directory == null) {
            return null;
        }
        Path gitPath = Paths.get(directory.toString(), GIT_FOLDER_NAME);
        if (Files.exists(gitPath)) {
            return directory;
        } else {
            return findRepository(directory.getParent());
        }
    }

    void indexFile(String fileName) throws GitPROException {
        Path filePath = Paths.get(fileName);
        if (!filePath.isAbsolute()) {
            filePath = getFilePath(fileName);
        } else if (!filePath.startsWith(repositoryDirectory)) {
            throw new GitPROException("This file isn't inside repository!");
        }
        if (!Files.exists(filePath)) {
            throw new GitPROException("File " + fileName + " doesn't exist!");
        }
        if (filePath.startsWith(gitDirectory)) {
            throw new GitPROException("No, please, no.");
        }
        Index index = getIndex();
        if (Files.isDirectory(filePath)) {
            try {
                Files.walk(filePath).forEach(index::addFile);
            } catch (IOException e) {
                throw new GitPROException("Unable to walt the directory " + filePath.toString() + " :(", e);
            }
        }
        while (!filePath.equals(repositoryDirectory)) {
            index.addFile(filePath);
            filePath = filePath.getParent();
        }
        writeIndex(index);
    }

    void commit(String message) throws GitPROException {
        Tree currentTree = buildTree(repositoryDirectory);
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
            writeTree(tree);
            String hash = SHA1Encoder.getHash(tree);
            return new Tree.Edge(path, Tree.TYPE, hash);
        } else {
            Blob blob = makeBlob(path);
            writeBlob(blob);
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
        if (!branchExists(revisionName) && !commitExists(revisionName)) {
            throw new GitPROException("Failed to checkout revision " + revisionName + "! No such revision exists.");
        }
        cleanFileSystem(getIndex());
        Commit toCommit;
        if (branchExists(revisionName)) {
            Branch toBranch = getBranch(revisionName);
            toCommit = getCommit(toBranch.getCommitHash());
            updateHead(Branch.TYPE, revisionName);
        } else {
            toCommit = getCommit(revisionName);
            updateHead(Commit.TYPE, revisionName);
        }
        Tree toTree = getTree(toCommit.getTreeHash());
        fillFileSystem(toTree);
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

        fillFileSystem(newTree);

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

    private void cleanFileSystem(Index index) throws GitPROException {
        List<Path> paths = index.getIndexedFiles().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
        for (Path path : paths) {
            try {
                if (Files.isDirectory(path)) {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        // directory isn't empty
                    }
                } else {
                    Files.deleteIfExists(path);
                }
            } catch (IOException e) {
                throw new GitPROException("Can't delete file " + path.toString() + " :(", e);
            }
        }
    }

    private void fillFileSystem(Tree tree) throws GitPROException {
        Index index = new Index();
        fillFileSystemRec(tree, index);
        writeIndex(index);
    }

    private void fillFileSystemRec(Tree tree, Index index) throws GitPROException {
        for (Tree.Edge edge : tree.getChildren()) {
            index.addFile(edge.getPath());
            if (edge.getNodeType().equals(Tree.TYPE)) {
                Tree childTree = getTree(edge.getNodeHash());

                Path dirPath = edge.getPath();
                if (!Files.exists(dirPath)) {
                    try {
                        Files.createDirectory(dirPath);
                    } catch (IOException e) {
                        throw new GitPROException("Failed to create folder :(", e);
                    }
                }

                fillFileSystemRec(childTree, index);
            } else {
                try {
                    Path filePath = edge.getPath();
                    if (!Files.exists(filePath)) {
                        Files.createFile(filePath);
                    }
                    Blob blob = getBlob(edge.getNodeHash());
                    Files.write(filePath, blob.getContent());
                } catch (IOException e) {
                    throw new GitPROException("Failed to update file!", e);
                }
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

    List<CommitLog> getLog() throws GitPROException {
        List<CommitLog> result = new ArrayList<>();
        String commitHash = getLastRevisionHash();
        while (true) {
            Commit commit = getCommit(commitHash);
            result.add(new CommitLog(commit));
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

    private Blob getBlob(String blobHash) throws GitPROException {
        Path path = getGitObjectPath(blobHash);
        return (Blob) ObjectIO.readObject(path);
    }

    private void writeBlob(Blob blob) throws GitPROException {
        String hash = SHA1Encoder.getHash(blob);
        Path path = getGitObjectPath(hash);
        ObjectIO.writeObject(path, blob);
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
}
