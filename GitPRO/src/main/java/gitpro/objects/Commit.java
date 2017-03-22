package gitpro.objects;

import lombok.Getter;

import java.util.Date;

/**
 * Created by wackloner on 22-Mar-17.
 */
@Getter
public class Commit extends GitObject {
    public static final String TYPE = "commit";

    private String treeHash;
    private String message;
    private String author;
    private String previousCommitHash;
    private Date date;

    public Commit(String message, String treeHash, String previousCommitHash) {
        this.message = message;
        this.treeHash = treeHash;
        this.previousCommitHash = previousCommitHash;
        this.author = System.getProperty("user.name");
        this.date = new Date();
    }
}
