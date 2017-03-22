package gitpro.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by wackloner on 22-Mar-17.
 */
@AllArgsConstructor
@Getter
public class Branch extends GitObject {
    public static final String TYPE = "branch";

    private final String name;
    @Setter
    private String commitHash;
}
