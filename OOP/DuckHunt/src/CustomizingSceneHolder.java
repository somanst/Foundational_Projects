import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Holds and manages the customizing scene of the game.
 */
public class CustomizingSceneHolder {
    private static double SCALE;
    private static double vol;
    private Scene scene;
    private final MediaView introMediaViewer;
    private int imageIndex = 0;
    private List<Image> imageList;
    private List<Image> foregroundList;
    private int crosshairIndex = 0;
    private List<Image> crosshairList;
    private List<Text> visualTexts = new ArrayList<>();

    /**
     * Constructs a CustomizingSceneHolder object with the specified scale, volume, and intro media viewer.
     *
     * @param scale              The scale factor for the scene.
     * @param vol_               The volume for the media player.
     * @param introMediaViewer   The media view for the intro media.
     */
    public CustomizingSceneHolder(double scale, double vol_, MediaView introMediaViewer){
        SCALE = scale;
        vol = vol_;
        this.introMediaViewer = introMediaViewer;
    }

    /**
     * Creates the customizing scene with initial background and crosshair.
     */
    public void sceneCreator(){
        Text arrowText = new Text("USE ARROW KEYS TO NAVIGATE");
        Text enterText = new Text("PRESS ENTER TO START");
        Text escapeText = new Text("PRESS ESC TO EXIT");
        enterText.setFont(Font.font("Verdana", FontWeight.BOLD,8 * SCALE));
        escapeText.setFont(Font.font("Verdana", FontWeight.BOLD,8 * SCALE));
        arrowText.setFont(Font.font("Verdana", FontWeight.BOLD, 8 * SCALE));
        arrowText.setFill(Color.ORANGE);
        enterText.setFill(Color.ORANGE);
        escapeText.setFill(Color.ORANGE);
        visualTexts.addAll(Arrays.asList(arrowText, enterText, escapeText));

        imageList = backgroundImageCollector();
        crosshairList = crosshairImageCollector();
        foregroundList = foregroundImageCollector();

        StackPane introPane = sceneSetter( 0, 0);
        ImageView backgroundImageView = backgroundImageSelector(0);
        scene = new Scene(introPane,backgroundImageView.getFitWidth(),backgroundImageView.getFitHeight());
    }

    /**
     * Collects the background images from the specified directory.
     *
     * @return The list of background images.
     */
    private List<Image> backgroundImageCollector(){
        List<Image> imageList = new ArrayList<>();
        String backgroundFilePath = "assets\\background";
        File backgroundFile = new File(backgroundFilePath);
        for(File image : Objects.requireNonNull(backgroundFile.listFiles())){
            imageList.add(new Image(String.valueOf(image.toURI())));
        }
        return imageList;
    }

    /**
     * Collects the crosshair images from the specified directory.
     *
     * @return The list of crosshair images.
     */
    private List<Image> crosshairImageCollector(){
        List<Image> crosshairList = new ArrayList<>();
        String crosshairFilePath = "assets\\crosshair";
        File crosshairFile = new File(crosshairFilePath);
        for(File image : Objects.requireNonNull(crosshairFile.listFiles())){
            crosshairList.add(new Image(String.valueOf(image.toURI())));
        }
        return crosshairList;
    }

    /**
     * Collects the foreground images from the specified directory.
     *
     * @return The list of foreground images.
     */
    private List<Image> foregroundImageCollector(){
        List<Image> foregroundList = new ArrayList<>();
        String foregroundFilePath = "assets\\foreground";
        File foregroundFile = new File(foregroundFilePath);
        for(File image : Objects.requireNonNull(foregroundFile.listFiles())){
            foregroundList.add(new Image(String.valueOf(image.toURI())));
        }
        return foregroundList;
    }

    /**
     * Retrieves the customizing scene.
     *
     * @return The customizing scene.
     */
    public Scene getScene(){
        return scene;
    }

    /**
     * Selects the background image based on the index difference, which is actually the method that gets the next
     * background when user presses one of the arrow keys
     *
     * @param indexDifference The difference to apply to the image index. 1 for right, -1 for left
     * @return The selected background image view.
     */
    public ImageView backgroundImageSelector(int indexDifference){
        imageIndex += indexDifference;
        if(imageIndex == -1) imageIndex = imageList.size() - 1;
        else if (imageIndex == imageList.size())  imageIndex = 0;
        Image targetImage = imageList.get(imageIndex);
        ImageView targetImageView = new ImageView(targetImage);
        targetImageView.setFitWidth(targetImage.getWidth() * SCALE);
        targetImageView.setFitHeight(targetImage.getHeight() * SCALE);
        return targetImageView;
    }

    /**
     * Selects the crosshair image based on the index difference.
     *
     * @param indexDifference The difference to apply to the crosshair index, same process.
     * @return The selected crosshair image view.
     */
    public ImageView crosshairImageSelector(int indexDifference){
        crosshairIndex += indexDifference;;
        if(crosshairIndex == -1) crosshairIndex = crosshairList.size() - 1;
        else if (crosshairIndex == crosshairList.size())  crosshairIndex = 0;
        Image targetCrosshair = crosshairList.get(crosshairIndex);
        ImageView targetCrosshairView = new ImageView(targetCrosshair);
        targetCrosshairView.setFitWidth(targetCrosshair.getWidth() * SCALE);
        targetCrosshairView.setFitHeight(targetCrosshair.getHeight() * SCALE);
        return targetCrosshairView;
    }

    /**
     * Just like the prior ones, selects the foreground image based on the index difference.
     *
     * @param indexDifference The difference to apply to the image index.
     * @return The selected foreground image view.
     */
    public ImageView foregroundImageSelector(int indexDifference){
        imageIndex += indexDifference;
        Image targetImage = foregroundList.get(imageIndex);
        ImageView targetImageView = new ImageView(targetImage);
        targetImageView.setFitWidth(targetImage.getWidth() * SCALE);
        targetImageView.setFitHeight(targetImage.getHeight() * SCALE);
        return targetImageView;
    }

    /**
     * Collects what background/crosshair the user wants and according to that produces a stackPane with texts that
     * are for that phase of the game.
     *
     * @param backgroundIndexDifference The difference to apply to the background image index according to user.
     * @param crosshairIndexDifference  The difference to apply to the crosshair image index according to user.
     * @return The customizing scene pane.
     */
    public StackPane sceneSetter(int backgroundIndexDifference, int crosshairIndexDifference){
        Text arrowText = visualTexts.get(0), enterText = visualTexts.get(1), escapeText = visualTexts.get(2);
        ImageView backgroundImageView =  backgroundImageSelector(backgroundIndexDifference);
        ImageView crosshairImageView = crosshairImageSelector(crosshairIndexDifference);
        StackPane introPane = new StackPane();
        introPane.getChildren().addAll(backgroundImageView, crosshairImageView, escapeText, enterText, arrowText, introMediaViewer);
        arrowText.setTranslateX(introPane.getWidth()/2);
        arrowText.setTranslateY(-105 * SCALE);
        enterText.setTranslateX(introPane.getWidth()/2);
        enterText.setTranslateY(-95 * SCALE);
        escapeText.setTranslateX(introPane.getWidth()/2);
        escapeText.setTranslateY(-85 * SCALE);
        return introPane;
    }

    /**
     * Resets the image indexes to zero.
     */
    public void indexNullifier(){
        imageIndex = 0;
        crosshairIndex = 0;
    }

    /**
     * Starts the game by playing the intro music.
     */
    public void gameStarter(){
        File introMusic = new File("assets\\effects\\Intro.mp3");
        Media introMedia = new Media(introMusic.toURI().toString());
        MediaPlayer introPlayer  = new MediaPlayer(introMedia);
        introPlayer.setVolume(vol);
        introPlayer.play();
    }
}
