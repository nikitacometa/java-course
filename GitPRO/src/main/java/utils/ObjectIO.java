package utils;

import exceptions.GitPROException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;

/**
 * Created by wackloner on 22-Mar-17.
 */
public class ObjectIO {
    public static Object readObject(Path objectPath) throws GitPROException {
        try (FileInputStream fis = new FileInputStream(objectPath.toString());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return ois.readObject();
        } catch (Exception e) {
            throw new GitPROException("Failed to read object.", e);
        }
    }

    public static void writeObject(Path objectPath, Object object) throws GitPROException {
        try (FileOutputStream fos = new FileOutputStream(objectPath.toString());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(object);
        } catch (Exception e) {
            throw new GitPROException("Failed to write object.", e);
        }
    }
}
