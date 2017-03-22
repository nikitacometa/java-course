package gitpro.objects;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

/**
 * Created by wackloner on 22-Mar-17.
 */
public class Index implements Serializable {
    private List<Path> indexedFiles;

    public Index() {
        indexedFiles = Collections.emptyList();
    }

    public void addFile(Path filePath) {
        if (!indexedFiles.contains(filePath)) {
            indexedFiles.add(filePath);
        }
    }

    public boolean contains(Path filePath) {
        return indexedFiles.contains(filePath);
    }
}
