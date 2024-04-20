
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
/**
 * The ReadFromFile class provides a static method for reading all lines from a file and returning them as an array of Strings.
 */
public class ReadFromFile {

    /**
     * Reads all lines from the specified file and returns them as an array of Strings.
     * @param path the path of the file to read
     * @return an array of Strings representing the lines of the file
     */
    public static String[] readFile(String path) {
        try {
            int i = 0;
            int length = Files.readAllLines(Paths.get(path)).size();
            String[] results = new String[length];
            for (String line : Files.readAllLines(Paths.get(path))) {
                results[i++] = line;

            }
            return results;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * the test of the method.
     * @param args the command line arguments (unused)
     */
    public static void main(String[] args) {
        String[] lines = readFile("testfile.txt");
        for (String line : lines) {
            System.out.println(line);
        }

    }
}