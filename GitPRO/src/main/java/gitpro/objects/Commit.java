package gitpro.objects;

import java.util.Date;

/**
 * Created by wackloner on 22-Mar-17.
 */
public class Commit extends GitObject {
    public static final String TYPE = "commit";

    private String treeHash;
    private String message;
    private Date date;

    public Commit(String message, String treeHash) {
        this.treeHash = treeHash;
        this.date = new Date();
    }
}
