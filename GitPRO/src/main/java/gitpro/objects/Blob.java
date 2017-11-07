package gitpro.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by wackloner on 22-Mar-17.
 */
@AllArgsConstructor
@Getter
public class Blob extends TreeNode {
    public static final String TYPE = "blob";

    private final byte[] content;
}
