package objects;

import java.util.Date;

/**
 * Created by wackloner on 22-Mar-17.
 */
public class Commit extends GitObject {
    private String treeSHA;
    private Date date;
    private String author;
}
