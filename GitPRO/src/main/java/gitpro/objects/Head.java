package gitpro.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * Created by wackloner on 22-Mar-17.
 */
@AllArgsConstructor
@Getter
public class Head implements Serializable {
    private String revisionType;
    private String revisionName;
}
