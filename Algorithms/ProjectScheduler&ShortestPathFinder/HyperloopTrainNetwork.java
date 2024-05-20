import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HyperloopTrainNetwork implements Serializable {
    static final long serialVersionUID = 11L;
    public double averageTrainSpeed;
    public final double averageWalkingSpeed = 1000 / 6.0;;
    public int numTrainLines;
    public Station startPoint;
    public Station destinationPoint;
    public List<TrainLine> lines;

    /**
     * Method with a Regular Expression to extract integer numbers from the fileContent
     * @return the result as int
     */
    public int getIntVar(String varName, String fileContent) {
        Pattern p = Pattern.compile("[\\t ]*" + varName + "[\\t ]*=[\\t ]*([0-9]+)");
        Matcher m = p.matcher(fileContent);
        m.find();
        return Integer.parseInt(m.group(1));
    }

    /**
     * Write the necessary Regular Expression to extract string constants from the fileContent
     * @return the result as String
     */
    public String getStringVar(String varName, String fileContent) {
        // TODO: Your code goes here

        Pattern p = Pattern.compile("[\\t ]*" + varName + "[\\t ]*=[\\t ]*\"(.+)\"");
        Matcher m = p.matcher(fileContent);
        m.find();
        return m.group(1);
    }

    /**
     * Write the necessary Regular Expression to extract floating point numbers from the fileContent
     * Your regular expression should support floating point numbers with an arbitrary number of
     * decimals or without any (e.g. 5, 5.2, 5.02, 5.0002, etc.).
     * @return the result as Double
     */
    public Double getDoubleVar(String varName, String fileContent) {
        // TODO: Your code goes here

        Pattern p = Pattern.compile("[\\t ]*" + varName + "[\\t ]*=[\\t ]*([0-9.]+)");
        Matcher m = p.matcher(fileContent);
        m.find();
        return Double.parseDouble(m.group(1));
    }

    /**
     * Write the necessary Regular Expression to extract a Point object from the fileContent
     * points are given as an x and y coordinate pair surrounded by parentheses and separated by a comma
     * @return the result as a Point object
     */
    public Point getPointVar(String varName, String fileContent) {
        Point p = new Point(0, 0);
        // TODO: Your code goes here

        Pattern pa = Pattern.compile("[\\t ]*" + varName + "[\\t ]*=[\\t ]*\\([\\t ]*([0-9]+)[\\t ]*,[\\t ]*([0-9]+)[\\t ]*\\)");
        Matcher m = pa.matcher(fileContent);
        m.find();
        p.x = Integer.parseInt(m.group(1));
        p.y = Integer.parseInt(m.group(2));

        return p;
    } 

    /**
     * Function to extract the train lines from the fileContent by reading train line names and their 
     * respective stations.
     * @return List of TrainLine instances
     */
    public List<TrainLine> getTrainLines(String fileContent) {
        List<TrainLine> trainLines = new ArrayList<>();

        // TODO: Your code goes here
        String[] lineList = fileContent.split("\n");
        String curName = "";
        for(int i = 4; i < lineList.length; i++){
            if(i % 2 == 0){
                curName = getStringVar("train_line_name" ,lineList[i]);
            } else {
                List<Station> stations = lineStationizer(lineList[i], curName);
                trainLines.add(new TrainLine(curName, stations));
            }
        }

        return trainLines;
    }

    private List<Station> lineStationizer(String trainLine, String trainName){
        List<Station> stations = new ArrayList<>();
        String regexPattern = "[\\t ]*\\([\\t ]*([0-9]+)[\\t ]*,[\\t ]*([0-9]+)[\\t ]*\\)";

        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(trainLine);

        int i = 1;
        while (matcher.find()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            Point point = new Point(x, y);
            Station station = new Station(point, trainName + " Line Station " + i);
            stations.add(station);
            i++;
        }

        return stations;
    }

    /**
     * Function to populate the given instance variables of this class by calling the functions above.
     */
    public void readInput(String filename) {

        // TODO: Your code goes here
        String fileString;
        try {
            byte[] fileBytes = Files.readAllBytes(Paths.get(filename));
            fileString = new String(fileBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String[] splitString = fileString.split("\n");
        this.numTrainLines = getIntVar("num_train_lines" ,splitString[0]);

        this.startPoint = new Station(getPointVar("starting_point", splitString[1]), "Starting Point");

        this.destinationPoint = new Station(getPointVar("destination_point", splitString[2]), "Final Destination");

        this.averageTrainSpeed = getDoubleVar("average_train_speed", splitString[3]) * 1000 / 60;
        lines = getTrainLines(fileString);

    }
}