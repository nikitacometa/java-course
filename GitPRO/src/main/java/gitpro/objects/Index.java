package gitpro.objects;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wackloner on 22-Mar-17.
 */
public class Index implements Serializable {
    private List<String> indexedFiles;

    public Index() {
        indexedFiles = new ArrayList<>();
    }

    public void addFile(Path filePath) {
        if (!indexedFiles.contains(filePath.toString())) {
            indexedFiles.add(filePath.toString());
        }
    }

    public boolean contains(Path filePath) {
        return indexedFiles.contains(filePath.toString());
    }
}
