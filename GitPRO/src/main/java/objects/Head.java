package objects;

import lombok.AllArgsConstructor;

import java.io.Serializable;

/**
 * Created by wackloner on 22-Mar-17.
 */
@AllArgsConstructor
public class Head implements Serializable {
    private String revisionType;
    private String revisionName;
}
