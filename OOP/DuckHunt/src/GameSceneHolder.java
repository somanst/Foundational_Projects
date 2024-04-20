import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a holder for the gameScene that is the scene where the game and shooting is at.
 */
public class GameSceneHolder {
    private final CustomizingSceneHolder customizingSceneHolder;
    private HBox horizontalImages;
    private HBox horizontalForegrounds;
    private final double SCALE;
    private final double vol;
    private Text ammoCount;
    private final List<Duck> duckList;
    private int curLevel;
    private int ammoCt;
    private List<Duck> activeDuckList;
    private Pane duckPane;
    private final MediaPlayer duckDeathPlayer;

    /**
     * Constructs a new GameSceneHolder with the given parameters and gets the necessary mediaPlayer to play when a duck
     * dies.
     *
     * @param customizingSceneHolder_ The CustomizingSceneHolder instance.
     * @param scale                   The scale factor.
     * @param vol_                    The volume.
     */
    public GameSceneHolder(CustomizingSceneHolder customizingSceneHolder_, double scale, double vol_){
        customizingSceneHolder = customizingSceneHolder_;
        SCALE = scale;
        vol = vol_;
        duckList = new ArrayList<>();
        activeDuckList = new ArrayList<>();
        File duckFile = new File("assets\\effects\\DuckFalls.mp3");
        Media duckMedia = new Media(String.valueOf(duckFile.toURI()));
        duckDeathPlayer = new MediaPlayer(duckMedia);
        duckDeathPlayer.setVolume(vol);
    }

    /**
     * Creates the main game pane for the specified level, by first getting the background and foregrounds that the
     * user chose, make HBoxes that have each of them multiplied by 3 to make a game space with the chosen images,
     * while also setting up the ducks to be in the returned with the Stackpane.
     *
     * @param level The level of the game that is needed.
     * @return The StackPane representing the game scene.
     */
    public StackPane paneCreator(int level){
        curLevel = level;
        horizontalImages = new HBox();
        for (int i = 0; i < 3; i++) {
            ImageView imageView = customizingSceneHolder.backgroundImageSelector(0);
            imageView.setPreserveRatio(true);
            horizontalImages.getChildren().add(imageView);
        }

        horizontalForegrounds = new HBox();
        for (int i = 0; i < 3; i++) {
            ImageView imageView = customizingSceneHolder.foregroundImageSelector(0);
            imageView.setPreserveRatio(true);
            horizontalForegrounds.getChildren().add(imageView);
        }

        duckInitializer();
        duckPane = duckSpawner(level);

        StackPane gamePane = new StackPane();
        gamePane.getChildren().addAll(horizontalImages, duckPane, horizontalForegrounds);
        for(Duck duck : activeDuckList){
            duck.ducksMovementTimelineCreator();
        }
        return gamePane;
    }

    /**
     * Gets the width of the foreground.
     *
     * @return The width of the foreground.
     */
    public double getWidth(){
        return horizontalForegrounds.getWidth();
    }

    /**
     * Gets the height of the foreground.
     *
     * @return The height of the foreground.
     */
    public double getHeight(){
        return horizontalForegrounds.getHeight();
    }

    /**
     * Organizes the UI elements, like ammo text and level text, and returns the StackPane representing the UI.
     *
     * @return The StackPane representing the UI.
     */
    public StackPane UIOrganizer(){
        ammoCt = activeDuckList.size() * 3;
        ammoCount = new Text(String.format("Ammo Left: %d", ammoCt));
        Text levelCount = new Text(String.format("Level %d/6", curLevel));
        ammoCount.setFont(Font.font("Verdana", 8 * SCALE));
        levelCount.setFont(Font.font("Verdana", 8 * SCALE));
        ammoCount.setFill(Color.ORANGE);
        levelCount.setFill(Color.ORANGE);
        StackPane textPane = new StackPane();
        textPane.getChildren().addAll(ammoCount, levelCount);
        textPane.setPrefWidth(getWidth() / 3);
        textPane.setPrefHeight(getHeight());
        ammoCount.setTranslateX(95 * SCALE);
        ammoCount.setTranslateY( -105 * SCALE);
        levelCount.setTranslateX(0);
        levelCount.setTranslateY(-105 * SCALE);
        return textPane;
    }

    /**
     * Initializes the ducks that are going to be just like characters in our game, each duck having its own movement
     * style and specs.
     */
    public void duckInitializer(){
        Duck duck = new Duck("black", 0, SCALE, -1, 1, "right");
        Duck duck2 = new Duck("red", 0, SCALE, 1, 1, "topL");
        Duck duck3 = new Duck("blue", 0, SCALE, -1, 1, "topR");
        Duck duck4 = new Duck("red", 1, SCALE, -1, 1, "topL");
        Duck duck5 = new Duck("blue", 1, SCALE, -1, -1, "topR");
        Duck duck6 = new Duck("black", 1, SCALE, 1, 1, "left");
        duckList.addAll(Arrays.asList(duck, duck2, duck3, duck4, duck5, duck6));
    }

    /**
     * Spawns ducks based on the specified level, with each level having more and faster ducks.
     *
     * @param level The level of the game.
     * @return The StackPane containing the spawned ducks.
     */
    public StackPane duckSpawner(int level){

        StackPane duckPane = new StackPane();
        if(level == 1){
            Duck duck = duckList.get(0);
            duckSetter(duck, duckPane);
        }
        else if (level == 2) {
            Duck duck1 = duckList.get(0);
            duckSetter(duck1, duckPane);

            Duck duck2 = duckList.get(1);
            duckSetter(duck2, duckPane);
        }
        else if(level == 3){
            Duck duck1 = duckList.get(2);
            duckSetter(duck1, duckPane);

            Duck duck2 = duckList.get(5);
            duckSetter(duck2, duckPane);
        }
        else if(level == 4){
            Duck duck1 = duckList.get(3);
            duckSetter(duck1, duckPane);

            Duck duck2 = duckList.get(4);
            duckSetter(duck2, duckPane);

            duck1.speedUp(10);
        }
        else if(level == 5) {
            Duck duck1 = duckList.get(2);
            duckSetter(duck1, duckPane);

            Duck duck2 = duckList.get(1);
            duckSetter(duck2, duckPane);

            Duck duck3 = duckList.get(3);
            duckSetter(duck3, duckPane);

            duck1.speedUp(10);
            duck2.speedUp(10);
            duck3.speedUp(10);
        }
        else if(level == 6){
            Duck duck1 = duckList.get(2);
            duckSetter(duck1, duckPane);

            Duck duck2 = duckList.get(1);
            duckSetter(duck2, duckPane);

            Duck duck3 = duckList.get(4);
            duckSetter(duck3, duckPane);

            Duck duck4 = duckList.get(3);
            duckSetter(duck4, duckPane);

            Duck duck5 = duckList.get(5);
            duckSetter(duck5, duckPane);

            duck2.speedUp(10);
            duck4.speedUp(5);
            duck1.speedUp(10);
            duck3.speedUp(20);
            duck5.speedUp(30);
        }

        return duckPane;
    }

    /**
     * A helper method that sets up the specified duck and adds it to the activeDuckList where the game's current alive
     * ducks are available to be identified.
     *
     * @param duck     The duck to set up.
     * @param duckPane The duck pane.
     */
    public void duckSetter(Duck duck, StackPane duckPane){
        Pane duck1Pane = new Pane();
        ImageView duck1View = duck.getCurrentFrame();
        duck1Pane.getChildren().add(duck1View);
        duckPane.getChildren().add(duck1Pane);
        BorderPane.setMargin(duck1Pane, new Insets(40));
        activeDuckList.add(duck);
    }

    /**
     * Gets the game status based on the specified coordinates. 2 means a level won, 1 means duck was shot but another
     * duck wasn't or the shot not hitting, while 0 means ammo running out while ducks are still alive, aka losing.
     *
     * @param xCor The x-coordinate.
     * @param yCor The y-coordinate.
     * @return The game status: 0 if ammo is depleted, 1 if ducks are still active, 2 if all ducks are shot.
     */
    public int getGameStatus(double xCor, double yCor){
        File gunFile = new File("assets\\effects\\Gunshot.mp3");
        Media media = new Media(String.valueOf(gunFile.toURI()));
        MediaPlayer gunPlayer = new MediaPlayer(media);
        gunPlayer.setVolume(vol);
        gunPlayer.play();
        ammoCt--;
        ammoUpdate();
        List<Duck> duckShot = new ArrayList<>();
        for(Duck duck : activeDuckList){
            ImageView node = duck.getCurrentFrame();
            Bounds duckBounds = node.localToScene(node.getBoundsInLocal());
            if(duckBounds.contains(xCor, yCor)){
                duckDeathPlayer.seek(Duration.ZERO);
                duckDeathPlayer.play();

                duck.duckDeath();
                duckShot.add(duck);
            }
        }
        activeDuckList.removeAll(duckShot);
        if(activeDuckList.isEmpty()) return 2;
        else if(ammoCt == 0) return 0;
        else return 1;
    }

    /**
     * Updates the ammo count on the UI.
     */
    public void ammoUpdate(){
        ammoCount.setText(String.format("Ammo Left: %d", ammoCt));
    }

    /**
     * Gets the duck pane. (useful in transition scene creating)
     *
     * @return The duck pane.
     */
    public Pane getDuckPane(){
        return duckPane;
    }

    /**
     * Resets the duck list and active duck list which is used either at the start of a new level or when the game end.
     */
    public void duckListResetter(){
        for(Duck duck : duckList){
            duck = null;
        }
        for(Duck duck : activeDuckList){
            duck = null;
        }
        duckList.removeAll(duckList);
        activeDuckList.removeAll(activeDuckList);
        System.gc();
    }

    /**
     * Gets the horizontal images, which are the background of the gameScene.
     *
     * @return The horizontal background.
     */
    public HBox getHImages(){
        return horizontalImages;
    }

    /**
     * Gets the horizontal foregrounds, which are the background of the gameScene.
     *
     * @return The horizontal foregrounds.
     */
    public HBox getHorizontalForegrounds(){
        return horizontalForegrounds;
    }

    /**
     * Stops the duck death mediaPlayer.
     */
    public void stopDuckDeathPlayer(){
        duckDeathPlayer.stop();
    }

    /**
     * Gets the living duck list.
     *
     * @return The active duck list.
     */
    public List<Duck> getActiveDuckList(){
        return activeDuckList;
    }

}
