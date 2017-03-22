package gitpro.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by wackloner on 22-Mar-17.
 */
@AllArgsConstructor
public class Branch extends GitObject {
    public static final String TYPE = "branch";

    @Getter
    private String name;
    @Setter
    private String commitHash;
}
