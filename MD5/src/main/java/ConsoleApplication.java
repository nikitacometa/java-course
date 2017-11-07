import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

/**
 * Created by wackloner on 30-Mar-17.
 */
public class ConsoleApplication {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        if (args.length == 0) {
            System.err.print("No directory path specified!");
            return;
        }

        String directoryName = args[0];

        Path directoryPath = Paths.get(directoryName);

        Timer timer = new Timer();

        timer.start();
        byte[] oneThreadBytes = new OneThreadEncoder().encode(directoryPath);
        System.out.println("OneThread:");
        System.out.println(Utils.bytesToString(oneThreadBytes));
        System.out.println(timer.getTime() + "ms");

        System.out.println();

        timer.start();
        byte[] forkJoinBytes = new ForkJoinEncoder().encode(directoryPath);
        System.out.println("ForkJoin:");
        System.out.println(Utils.bytesToString(forkJoinBytes));
        System.out.println(timer.getTime() + "ms");
    }
}
