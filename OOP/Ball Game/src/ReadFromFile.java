import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
public class ReadFromFile {
    //This is a simple class with the method that we are going to use to get the input
    //from the user, getting its size from the set path and adding each line to the returning string
    //(Taken from assignment's announcement)
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
}