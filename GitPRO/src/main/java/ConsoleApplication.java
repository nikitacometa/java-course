import gitpro.GitPRO;
import gitpro.exceptions.GitPROException;
import gitpro.utils.CommitLog;

import java.util.List;

/**
 * Created by wackloner on 21-Mar-17.
 */
public class ConsoleApplication {
    private static final String APPLICATION_NAME = "gitpro";

    private static final String COMMIT_COMMAND = "commit";
    private static final String CHECKOUT_COMMAND = "checkout";
    private static final String MERGE_COMMAND = "merge";
    private static final String LOG_COMMAND = "log";
    private static final String ADD_COMMAND = "add";
    private static final String NEW_BRANCH_COMMAND = "newbranch";
    private static final String REMOVE_BRANCH_COMMAND = "removebranch";
    private static final String HELP_COMMAND = "help";
    private static final String INIT_COMMAND = "init";

    private static final String SUGGEST_HELP_LINE =
            "Try \'" + APPLICATION_NAME + " " + HELP_COMMAND + "\' for usage info.";

    public static void main(String[] args) {
        try {
            parseArguments(args);
        } catch (GitPROException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void parseArguments(String[] args) throws GitPROException {
        if (args.length == 0) {
            showMessageMissing("Command ");
            return;
        }

        String command = args[0];

        if (command.equals(HELP_COMMAND)) {
            showHelp();
            return;
        }

        String currentDirectory = System.getProperty("user.dir");
        GitPRO gitPRO = new GitPRO(currentDirectory);

        if (command.equals(INIT_COMMAND)) {
            gitPRO.initNewRepository();
            showSuccessMessage("Empty repository was created!");
            return;
        }

        gitPRO.loadRepository();

        switch (command) {
            case COMMIT_COMMAND:
                if (args.length == 1) {
                    showMessageMissing("Commit message");
                } else {
                    String commitMessage = args[1];
                    gitPRO.commit(commitMessage);
                    showSuccessMessage("Commit was made!");
                }
                break;

            case CHECKOUT_COMMAND:
                if (args.length == 1) {
                    showMessageMissing("Branch name");
                } else {
                    String branchName = args[1];
                    gitPRO.checkout(branchName);
                    showSuccessMessage("Switched to branch " + branchName + ".");
                }
                break;

            case MERGE_COMMAND:
                if (args.length == 1) {
                    showMessageMissing("Branch name");
                } else {
                    String branchName = args[1];
                    gitPRO.merge(branchName);
                    showSuccessMessage("Merged current branch into branch " + branchName + ".");
                }
                break;

            case LOG_COMMAND:
                List<CommitLog> log = gitPRO.getLog();
                log.forEach(System.out::println);
                break;

            case ADD_COMMAND:
                if (args.length == 1) {
                    showMessageMissing("File name");
                } else {
                    for (int i = 1; i < args.length; i++) {
                        String fileName = args[i];
                        gitPRO.add(fileName);
                        showSuccessMessage(fileName + " was added!");
                    }
                }
                break;

            case NEW_BRANCH_COMMAND:
                if (args.length == 1) {
                    showMessageMissing("Branch name");
                } else {
                    String branchName = args[1];
                    gitPRO.createBranch(branchName);
                    showSuccessMessage("Branch '" + branchName + "' was created!");
                }
                break;

            case REMOVE_BRANCH_COMMAND:
                if (args.length == 1) {
                    showMessageMissing("Branch name");
                } else {
                    String branchName = args[1];
                    gitPRO.deleteBranch(branchName);
                    showSuccessMessage("Branch '" + branchName + "' was removed!");
                }
                break;

            default:
                showErrorMessage("Invalid argument! " + SUGGEST_HELP_LINE);
        }
    }

    private static void showErrorMessage(String message) {
        System.out.println("Error! " + message + "\n");
    }

    private static void showSuccessMessage(String message) {
        System.out.println("Success! " + message + "\n");
    }

    private static void showMessageMissing(String argumentName) {
        showErrorMessage("Argument is missing! " + argumentName + " is required. " + SUGGEST_HELP_LINE + "\n");
    }

    private static void showHelp() {
        // FIXME
        System.out.println("come on, man, you know how it works, you've watched the code");
    }
}
