import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.Scene;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import java.io.File;

/**
 * Holds and manages the welcome scene of the game, or the scene that is at the start of the game.
 */
public class WelcomeSceneHolder{
    private static double SCALE;
    private static double vol;
    private Scene scene;
    private MediaView introMediaViewer;
    private MediaPlayer titlePlayer;

    /**
     * Constructs a WelcomeSceneHolder object with the specified scale and volume.
     *
     * @param scale The scale factor for the scene.
     * @param vol_  The volume for the media player.
     */
    public WelcomeSceneHolder(double scale, double vol_) {
        SCALE = scale;
        vol = vol_;
    }

    /**
     * Creates the welcome scene with the flashing texts, background, and intro music.
     */
    public void sceneCreator(){
        Text enterText = new Text("PRESS ENTER TO START");
        Text escapeText = new Text("PRESS ESC TO EXIT");
        enterText.setFont(Font.font("Verdana", 16 * SCALE));
        escapeText.setFont(Font.font("Verdana", 16 * SCALE));
        enterText.setFill(Color.ORANGE);
        escapeText.setFill(Color.ORANGE);

        Image introImage = new Image("assets\\welcome\\1.png");
        ImageView introImageView = new ImageView(introImage);
        introImageView.setFitWidth(introImage.getWidth() * SCALE);
        introImageView.setFitHeight(introImage.getHeight() * SCALE);

        KeyFrame secondBlink = new KeyFrame(Duration.seconds(1), e -> {
            enterText.setVisible(!enterText.isVisible());
            escapeText.setVisible(!escapeText.isVisible());
        } );
        Timeline textBlinkTimeline = new Timeline(secondBlink);
        textBlinkTimeline.setCycleCount(Animation.INDEFINITE);
        textBlinkTimeline.play();

        File introMusic = new File("assets\\effects\\Title.mp3");
        Media introMedia = new Media(introMusic.toURI().toString());
        titlePlayer = new MediaPlayer(introMedia);
        introMediaViewer = new MediaView(titlePlayer);
        titlePlayer.setVolume(vol);
        titlePlayer.setAutoPlay(true);
        titlePlayer.setOnEndOfMedia(() -> titlePlayer.seek(Duration.ZERO));

        StackPane introPane = new StackPane();
        introPane.getChildren().addAll(introImageView, escapeText, enterText, introMediaViewer);
        enterText.setTranslateX(introPane.getWidth()/2);
        enterText.setTranslateY(introPane.getHeight()/2 + 43 * SCALE);
        escapeText.setTranslateX(introPane.getWidth()/2);
        escapeText.setTranslateY(introPane.getHeight()/2 + 60 *SCALE);

        this.scene = new Scene(introPane, introImageView.getFitWidth(), introImageView.getFitHeight());
    }

    /**
     * Gets the welcome scene.
     *
     * @return The welcome scene.
     */
    public Scene getScene(){
        return scene;
    }

    /**
     * Gets the media view for the intro media.
     *
     * @return The media view.
     */
    public MediaView getIntroMediaViewer(){
        return introMediaViewer;
    }

    /**
     * Stops the media player of the intro.
     */
    public void mediaPlayerStopper(){
        titlePlayer.stop();
    }

    /**
     * Resumes playing the media player.
     */
    public void mediaPlayerRunner(){ titlePlayer.play();}
}
