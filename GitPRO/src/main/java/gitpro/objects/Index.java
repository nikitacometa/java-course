package gitpro.objects;


import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<Path> getIndexedFiles() {
        return indexedFiles.stream()
                .map(fileName -> Paths.get(fileName))
                .collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return indexedFiles.isEmpty();
    }

    public void clear() {
        indexedFiles.clear();
    }

    public boolean contains(Path filePath) {
        return indexedFiles.contains(filePath.toString());
    }
}
