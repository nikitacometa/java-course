package gitpro;

import gitpro.exceptions.GitPROException;
import gitpro.utils.CommitLog;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by wackloner on 22-Mar-17.
 */

/**
 * Class having methods for working with GitPRO library.
 */
public class GitPRO {
    private CommandHandler commandHandler;

    public GitPRO(Path currentDirectory) throws GitPROException {
        if (!currentDirectory.isAbsolute()) {
            throw new GitPROException("You have to provide absolute path to directory!");
        }
        commandHandler = new CommandHandler(currentDirectory);
    }

    public GitPRO(String currentDirectory) throws GitPROException {
        this(Paths.get(currentDirectory));
    }

    /**
     * Initializes new gitPRO repository in specified directory.
     * @throws GitPROException thrown if a repository already exists or having troubles with filesystem.
     */
    public void initNewRepository() throws GitPROException {
        commandHandler.initRepository();
    }

    /**
     * Tries to initialize existing repository in current directory or in any parent directory.
     * @throws GitPROException thrown if no repository exists or having troubles with filesystem.
     */
    public void loadRepository() throws GitPROException {
        commandHandler.loadRepository();
    }

    /**
     * Adds file to repository index making it being traced.
     * @param fileName adding file name
     * @throws GitPROException
     */
    public void add(String fileName) throws GitPROException {
        commandHandler.indexFile(fileName);
    }

    /**
     * Commits changes in current branch with message. Date and author added automatically.
     * @param message commit message
     * @throws GitPROException
     */
    public void commit(String message) throws GitPROException {
        commandHandler.commit(message);
    }

    /**
     * Checks out revision: branch or commit.
     * @param revisionName commit hash for commit and branch name for branch
     * @throws GitPROException
     */
    public void checkout(String revisionName) throws GitPROException {
        commandHandler.checkout(revisionName);
    }

    /**
     * Creates new branch from current.
     * @param branchName new branch name
     * @throws GitPROException
     */
    public void createBranch(String branchName) throws GitPROException {
        commandHandler.createBranch(branchName);
    }

    /**
     * Deletes branch.
     * @param branchName deleting branch name
     * @throws GitPROException
     */
    public void deleteBranch(String branchName) throws GitPROException {
        commandHandler.deleteBranch(branchName);
    }

    /**
     * Merge branch into current one. Updates all files in current branch corresponding to ones in merging branch.
     * @param branchName merging branch name
     * @throws GitPROException
     */
    public void merge(String branchName) throws GitPROException {
        commandHandler.merge(branchName);
    }

    /**
     * Gives list of commits in current branch.
     * @return list of commits in current branch
     * @throws GitPROException
     */
    public List<CommitLog> getLog() throws GitPROException {
        return commandHandler.getLog();
    }
}
