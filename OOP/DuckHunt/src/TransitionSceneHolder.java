import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import java.io.File;

/**
 * The TransitionSceneHolder class represents a holder for different transition scenes in the game.
 * It provides methods to retrieve different transition scenes such as normal transition, losing scene, and winning scene.
 */

public class TransitionSceneHolder {
    private static double SCALE;

    private final GameSceneHolder gameSceneHolder;

    private final MediaPlayer levelFinishPlayer;

    private final MediaPlayer levelLostPlayer;

    private final MediaPlayer gameWonPlayer;


    /**
     * Constructs a TransitionSceneHolder object with the specified scale of the game, volume, and GameSceneHolder to
     * specify the background that the user has chosen, while also setting up the mediaPlayers that will be used in
     * the transition scenes.
     *
     * @param scale                  the scale value
     * @param vol                    the volume value
     */
    public TransitionSceneHolder(double scale, double vol, GameSceneHolder gameSceneHolder_) {
        SCALE = scale;
        gameSceneHolder = gameSceneHolder_;

        File levelFinish = new File("assets\\effects\\LevelCompleted.mp3");
        Media levelFinishMedia = new Media(String.valueOf(levelFinish.toURI()));
        levelFinishPlayer = new MediaPlayer(levelFinishMedia);
        levelFinishPlayer.setVolume(vol);

        File levelLost = new File("assets\\effects\\GameOver.mp3");
        Media levelLostMedia = new Media(String.valueOf(levelLost.toURI()));
        levelLostPlayer = new MediaPlayer(levelLostMedia);
        levelLostPlayer.setVolume(vol);

        File gameWon = new File("assets\\effects\\GameCompleted.mp3");
        Media gameWonMedia = new Media(String.valueOf(gameWon.toURI()));
        gameWonPlayer = new MediaPlayer(gameWonMedia);
        gameWonPlayer.setVolume(vol);
    }

    /**
     * Sets up the normal transition stackpane after a level that isn't the last level is won to be later set to the
     * appropriate scene.
     *
     * @return the StackPane representing the normal transition scene
     */
    public StackPane getNormalTrans() {
        HBox background = gameSceneHolder.getHImages();

        Pane duckPane = gameSceneHolder.getDuckPane();

        HBox foreground = gameSceneHolder.getHorizontalForegrounds();
        Text winText = new Text("YOU WIN!");
        Text enterText = new Text("Press ENTER to play next level");
        enterText.setFont(Font.font("Verdana", FontWeight.BOLD,12 * SCALE));
        winText.setFont(Font.font("Verdana", FontWeight.BOLD,12 * SCALE));
        enterText.setFill(Color.ORANGE);
        winText.setFill(Color.ORANGE);

        MediaView levelFinishView = new MediaView(levelFinishPlayer);

        StackPane stackPane = new StackPane(background, duckPane, foreground, enterText, winText, levelFinishView);
        duckPane.setTranslateX(gameSceneHolder.getWidth() / 3);
        winText.setTranslateY(-20 * SCALE);
        levelFinishPlayer.play();

        KeyFrame secondBlink = new KeyFrame(Duration.seconds(1), e -> enterText.setVisible(!enterText.isVisible()));
        Timeline textBlinkTimeline = new Timeline(secondBlink);
        textBlinkTimeline.setCycleCount(Animation.INDEFINITE);
        textBlinkTimeline.play();

        return stackPane;
    }

    /**
     * Sets up the normal transition stackpane after a losing a level to later set it to the appropriate scene.
     *
     * @return the StackPane representing the losing scene
     */
    public StackPane getLosingScene(){
        HBox background = gameSceneHolder.getHImages();

        Pane duckPane = gameSceneHolder.getDuckPane();

        HBox foreground = gameSceneHolder.getHorizontalForegrounds();
        Text loseText = new Text("GAMEOVER!!");
        Text enterText = new Text("Press ENTER to play again");
        Text escapeText = new Text("PRESS ESC TO EXIT");
        enterText.setFont(Font.font("Verdana", FontWeight.BOLD, 12 * SCALE));
        loseText.setFont(Font.font("Verdana", FontWeight.BOLD,12 * SCALE));
        escapeText.setFont(Font.font("Verdana", FontWeight.BOLD,12 * SCALE));
        enterText.setFill(Color.ORANGE);
        loseText.setFill(Color.ORANGE);
        escapeText.setFill(Color.ORANGE);

        MediaView levelLostView = new MediaView(levelLostPlayer);

        StackPane stackPane = new StackPane(background, duckPane, foreground, enterText, loseText,escapeText, levelLostView);
        duckPane.setTranslateX(gameSceneHolder.getWidth() / 3);
        loseText.setTranslateY(-20 * SCALE);
        levelLostPlayer.play();
        escapeText.setTranslateY(20 * SCALE);

        KeyFrame secondBlink = new KeyFrame(Duration.seconds(1), e -> {
            enterText.setVisible(!enterText.isVisible());
            escapeText.setVisible(!escapeText.isVisible());
        } );
        Timeline textBlinkTimeline = new Timeline(secondBlink);
        textBlinkTimeline.setCycleCount(Animation.INDEFINITE);
        textBlinkTimeline.play();

        return stackPane;
    }


    /**
     * Just like the ones before, this also sets up a transition stackpane, which is going to be the winning pane of the
     * last level, and is going to be set to the required scene.
     *
     * @return the StackPane representing the winning scene
     */
    public StackPane getWinningScene(){
        HBox background = gameSceneHolder.getHImages();

        Pane duckPane = gameSceneHolder.getDuckPane();

        HBox foreground = gameSceneHolder.getHorizontalForegrounds();

        Text loseText = new Text("You have completed the game!");
        Text enterText = new Text("Press ENTER to play again");
        Text escapeText = new Text("PRESS ESC TO EXIT");
        enterText.setFont(Font.font("Verdana", FontWeight.BOLD, 12 * SCALE));
        loseText.setFont(Font.font("Verdana", FontWeight.BOLD,12 * SCALE));
        escapeText.setFont(Font.font("Verdana", FontWeight.BOLD,12 * SCALE));
        enterText.setFill(Color.ORANGE);
        loseText.setFill(Color.ORANGE);
        escapeText.setFill(Color.ORANGE);

        MediaView gameWonView = new MediaView(gameWonPlayer);

        StackPane stackPane = new StackPane(background, duckPane, foreground, enterText, loseText,escapeText, gameWonView);
        duckPane.setTranslateX(gameSceneHolder.getWidth() / 3);
        loseText.setTranslateY(-20 * SCALE);
        gameWonPlayer.play();
        escapeText.setTranslateY(20 * SCALE);

        KeyFrame secondBlink = new KeyFrame(Duration.seconds(1), e -> {
            enterText.setVisible(!enterText.isVisible());
            escapeText.setVisible(!escapeText.isVisible());
        } );
        Timeline textBlinkTimeline = new Timeline(secondBlink);
        textBlinkTimeline.setCycleCount(Animation.INDEFINITE);
        textBlinkTimeline.play();

        return stackPane;
    }


    /**
     * Stops all mediaPlayers if any is playing so that switching off transition scenes doesn'tt let sounds to keep
     * playing.
     */
    public void stopTransitionPlayer(){
        levelFinishPlayer.stop();
        levelLostPlayer.stop();
        gameWonPlayer.stop();
    }

}
