package gitpro.utils;

import gitpro.exceptions.GitPROException;
import gitpro.objects.Commit;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wackloner on 23-Mar-17.
 */
public class CommitLog {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");

    private String hash;
    private String author;
    private String message;
    private Date date;

    public CommitLog(Commit commit) throws GitPROException {
        this.hash = SHA1Encoder.getHash(commit);
        this.author = commit.getAuthor();
        this.message = commit.getMessage();
        this.date = commit.getDate();
    }

    @Override
    public String toString() {
        return hash + " on " + DATE_FORMAT.format(date) + " by " + author + ": " + message;
    }
}
