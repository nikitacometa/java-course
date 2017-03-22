package gitpro;

import gitpro.exceptions.GitPROException;
import gitpro.objects.*;
import gitpro.utils.ObjectIO;
import gitpro.utils.SHA1Encoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wackloner on 21-Mar-17.
 */
public class CommandHandler {
    private final static String GIT_FOLDER_NAME = ".gitPRO";
    private final static String OBJECTS_FOLDER = "gitpro/objects";
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
            throw new GitPROException("Fail to init new repository, repository already exists in current currentDirectory.");
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
            Commit initCommit = new Commit("init commit", emptyTreeHash);
            writeCommit(initCommit);

            String initCommitHash = SHA1Encoder.getHash(initCommit);
            Branch masterBranch = new Branch(MASTER_BRANCH_NAME, initCommitHash);
            writeBranch(masterBranch);
        } catch (IOException e) {
            throw new GitPROException("Failed to init new repository. ", e);
        }
    }

    void addFile(String fileName) throws GitPROException {
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
        Commit newCommit = new Commit(message, treeHash);
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

    private void updateHead(String type, String name) throws GitPROException {
        Head newHead = new Head(type, name);
        writeHead(newHead);
    }

    private String getLastRevisionHash() {
        // TODO
        return null;
    }

    void loadRepository() {

    }

    private boolean branchExists(String branchName) {
        Path branchPath = getBranchPath(branchName);
        return Files.exists(branchPath);
    }

    private void writeBranch(Branch branch) throws GitPROException {
        Path path = getBranchPath(branch.getName());
        ObjectIO.writeObject(path, branch);
    }

    private Branch getBranch(String branchName) throws GitPROException {
        Path path = getBranchPath(branchName);
        return (Branch) ObjectIO.readObject(path);
    }

    private void writeTree(Tree tree) throws GitPROException {
        String hash = SHA1Encoder.getHash(tree);
        Path path = getGitObjectPath(hash);
        ObjectIO.writeObject(path, tree);
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
