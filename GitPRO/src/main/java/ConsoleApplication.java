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
        if (args.length == 0) {
            showMessageMissing("Command ");
            return;
        }
        String command = args[0];
        if (command.equals(HELP_COMMAND)) {
            showHelp();
            return;
        }
        if (command.equals(INIT_COMMAND)) {
            initRepository();
            return;
        }
        // TODO load repo
        switch (command) {
            case COMMIT_COMMAND:
                if (args.length == 1) {
                    showMessageMissing("Commit message");
                } else {
                    String commitMessage = args[1];
                    // TODO
                }
                break;
            case CHECKOUT_COMMAND:
                if (args.length == 1) {
                    showMessageMissing("Branch name");
                } else {
                    String branchName = args[1];
                    // TODO
                }
                break;
            case MERGE_COMMAND:
                if (args.length == 1) {
                    showMessageMissing("Branch name");
                } else {
                    String branchName = args[1];
                    // TODO
                }
                break;
            case LOG_COMMAND:
                // TODO
                break;
            case ADD_COMMAND:
                if (args.length == 1) {
                    showMessageMissing("File name");
                } else {
                    for (int i = 1; i < args.length; i++) {
                        String fileName = args[i];
                        // TODO
                    }
                }
                break;
            case NEW_BRANCH_COMMAND:
                if (args.length == 1) {
                    showMessageMissing("Branch name");
                } else {
                    String branchName = args[1];
                    // TODO
                }
                break;
            case REMOVE_BRANCH_COMMAND:
                if (args.length == 1) {
                    showMessageMissing("Branch name");
                } else {
                    String branchName = args[1];
                    // TODO
                }
                break;
            default:
                System.out.println("Error, invalid argument. " + SUGGEST_HELP_LINE);
        }
    }

    private static void showMessageMissing(String argumentName) {
        System.out.println("Error, argument is missing! " + argumentName + " is required. " + SUGGEST_HELP_LINE);
    }

    private static void initRepository() {

    }

    private static void showHelp() {

    }
}
