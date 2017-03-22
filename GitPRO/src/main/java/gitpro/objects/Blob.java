package gitpro.objects;

import java.io.Serializable;

/**
 * Created by wackloner on 22-Mar-17.
 */
public class Blob extends TreeNode implements Serializable {
    public static final String TYPE = "blob";

    private byte[] content;
}
