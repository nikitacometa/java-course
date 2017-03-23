package gitpro;

import gitpro.exceptions.GitPROException;
import gitpro.utils.CommitLog;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by wackloner on 22-Mar-17.
 */
public class GitPRO {
    private CommandHandler commandHandler;

    public GitPRO(Path currentDirectory) {
        commandHandler = new CommandHandler(currentDirectory);
    }

    public GitPRO(String currentDirectory) {
        this(Paths.get(currentDirectory));
    }

    public void initNewRepository() throws GitPROException {
        commandHandler.initRepository();
    }

    public void loadRepository() throws GitPROException {
        commandHandler.loadRepository();
    }

    public void indexFile(String fileName) throws GitPROException {
        commandHandler.indexFile(fileName);
    }

    public void commit(String message) throws GitPROException {
        commandHandler.commit(message);
    }

    public void checkout(String branchName) throws GitPROException {
        commandHandler.checkout(branchName);
    }

    public void createBranch(String branchName) throws GitPROException {
        commandHandler.createBranch(branchName);
    }

    public void deleteBranch(String branchName) throws GitPROException {
        commandHandler.deleteBranch(branchName);
    }

    public void merge(String branchName) throws GitPROException {
        commandHandler.merge(branchName);
    }

    public List<CommitLog> getLog() throws GitPROException {
        return commandHandler.getLog();
    }
}
