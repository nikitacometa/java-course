import exceptions.GitPROException;
import objects.*;
import utils.ObjectIO;
import utils.SHA1Encoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    private final Path directory;
    private final Path gitDirectory;
    private final Path objectsDirectory;
    private final Path branchesDirectory;

    CommandHandler(String directory) {
        this.directory = Paths.get(directory);
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

            Head initHead = new Head(Branch.TYPE, MASTER_BRANCH_NAME);
            writeHead(initHead);

            Tree emptyTree = new Tree();
            writeTree(emptyTree);

            String emptyTreeHash = SHA1Encoder.getHash(emptyTree);
            Commit initCommit = new Commit(emptyTreeHash);
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
        writeIndex(index);
    }

    void createBranch(String branchName) throws GitPROException {
        Path branchPath = getBranchPath(branchName);
        if (Files.exists(branchPath)) {
            throw new GitPROException("Failed to create branch: branch with specified name already exists");
        }
        String lastRevisionHash = getLastRevisionHash();
        Branch newBranch = new Branch(branchName, lastRevisionHash);
        ObjectIO.writeObject(branchPath, newBranch);
    }

    private String getLastRevisionHash() {
        // TODO
        return null;
    }

    void loadRepository() {

    }

    private void writeBranch(Branch branch) throws GitPROException {
        Path path = getBranchPath(branch.getName());
        ObjectIO.writeObject(path, branch);
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
        return (Index) ObjectIO.readObject(getGitFilePath(INDEX_FILE_NAME));
    }

    private void writeIndex(Index index) throws GitPROException {
        ObjectIO.writeObject(getGitFilePath(INDEX_FILE_NAME), index);
    }

    private Path getFilePath(String fileName) {
        return Paths.get(directory.toString(), fileName);
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
