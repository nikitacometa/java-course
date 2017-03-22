/**
 * Created by wackloner on 21-Mar-17.
 */
public class CommandHandler {
    private final static String GIT_FOLDER_NAME = ".gitPRO";
    private final static String OBJECTS_FOLDER = "objects";

    private final String directory;
    private final String gitDirectory;
    private final String objectsDirectory;

    public CommandHandler(String directory) {
        this.directory = directory;
        this.gitDirectory = directory + "/" + GIT_FOLDER_NAME;
        this.objectsDirectory = gitDirectory + "/" + OBJECTS_FOLDER;
    }

    public void initRepository() {

    }

    public void loadRepository() {

    }
}
