package gitpro.objects;

import lombok.AllArgsConstructor;

import java.io.Serializable;

/**
 * Created by wackloner on 22-Mar-17.
 */
@AllArgsConstructor
public class Blob extends TreeNode implements Serializable {
    public static final String TYPE = "blob";

    private final byte[] content;
}
