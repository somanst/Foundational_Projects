import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.util.Duration;
import javafx.scene.image.ImageView;
import java.util.List;

import java.awt.*;

/**
 * Main class that is holds up the structure of the application.
 */
public class DuckHunt extends Application{
    static final double vol = 0.025;
    static final double SCALE = 3;
    private Stage window;
    private CustomizingSceneHolder customizingSceneHolder;
    private Scene gameScene;
    private ImageView mouseView;
    private Pane mousePane;
    private int curLevel;
    public static void DuckHunt(String[] args) {
        launch(args);
    }

    /**
     * Starts the game and sets up the initial scenes and event handlers, basically where all the game is run. We create
     * our initial scenes beforehand with the one we could, and we postpone the creating of the other scenes or in other
     * words create them and later on change the root they have, with event handlers of every scene to receive input
     * with horizontal scaling applied in game so the player can move in the level if the mouse is at the far left/right
     *
     * @param window_ The main stage/window for the game.
     */

    @Override
    public void start(Stage window_){
        window = window_;
        window.setTitle("HUBBM Duck Hunt");

        WelcomeSceneHolder welcomeSceneHolder = new WelcomeSceneHolder(SCALE, vol);
        welcomeSceneHolder.sceneCreator();
        Scene welcomeScene = welcomeSceneHolder.getScene();
        MediaView introMediaView = welcomeSceneHolder.getIntroMediaViewer();

        customizingSceneHolder = new CustomizingSceneHolder(SCALE, vol, introMediaView);
        customizingSceneHolder.sceneCreator();
        Scene customizingScene = customizingSceneHolder.getScene();

        GameSceneHolder gameSceneHolder = new GameSceneHolder(customizingSceneHolder, SCALE, vol);
        gameScene = new Scene(new Pane());

        TransitionSceneHolder transitionSceneHolder = new TransitionSceneHolder(SCALE, vol, gameSceneHolder);
        Scene transScene = new Scene(new Pane());
        Scene loseScene = new Scene(new Pane());
        Scene winScene = new Scene(new Pane());
        curLevel = 1;

        mousePane = new Pane();
        mouseView = customizingSceneHolder.crosshairImageSelector(0);
        mousePane.getChildren().add(mouseView);

        window.setScene(welcomeScene);
        welcomeScene.setOnKeyPressed(e -> {
            KeyCode keyCode = e.getCode();
            if(keyCode == KeyCode.ESCAPE){
                Platform.exit();
            } else if (keyCode == KeyCode.ENTER) {
                customizingSceneHolder.indexNullifier();
                customizingScene.setRoot(customizingSceneHolder.sceneSetter(0,0));
                window.setScene(customizingScene);
            }
        });

        Timeline horizontalScalingTimeline = new Timeline(new KeyFrame(Duration.millis(20), e -> {
            double mouseX = MouseInfo.getPointerInfo().getLocation().getX() - window.getX();
            StackPane mainStack = (StackPane) gameScene.getRoot();
            ObservableList<Node> list = mainStack.getChildren();
            Node currentStack = list.get(0);
            if(mouseX < 25 * SCALE && mouseX > 0 && currentStack.getTranslateX() - gameScene.getWidth()/2 - window.getWidth()/2 < -20 * SCALE){
                currentStack.setTranslateX(currentStack.getTranslateX() + 5 * SCALE);
            } else if (window.getWidth() > mouseX && mouseX > window.getWidth() - 25 * SCALE &&
                    -currentStack.getTranslateX() - gameScene.getWidth()/2 - window.getWidth()/2 < -20 * SCALE) {
                currentStack.setTranslateX(currentStack.getTranslateX() - 5 * SCALE);
            }
        }));

        customizingScene.setOnKeyPressed(event -> {
            KeyCode keyCode = event.getCode();
            if(keyCode ==KeyCode.ESCAPE){
                window.setScene(welcomeScene);
                customizingSceneHolder.indexNullifier();
                StackPane tempPane = customizingSceneHolder.sceneSetter(0,0);
                customizingScene.setRoot(tempPane);
            } else if (keyCode == KeyCode.RIGHT) {
                StackPane tempPane = customizingSceneHolder.sceneSetter(1,0);
                customizingScene.setRoot(tempPane);
            } else if (keyCode == KeyCode.LEFT) {
                StackPane tempPane = customizingSceneHolder.sceneSetter(-1,0);
                customizingScene.setRoot(tempPane);
            } else if (keyCode == KeyCode.UP) {
                StackPane tempPane = customizingSceneHolder.sceneSetter(0, 1);
                customizingScene.setRoot(tempPane);
            } else if (keyCode == KeyCode.DOWN) {
                StackPane tempPane = customizingSceneHolder.sceneSetter(0, -1);
                customizingScene.setRoot(tempPane);
            } else if (keyCode == KeyCode.ENTER) {
                welcomeSceneHolder.mediaPlayerStopper();
                customizingSceneHolder.gameStarter();
                StackPane playedStack = customizingSceneHolder.sceneSetter(0, 0);
                Scene playedScene = new Scene(playedStack);
                window.setScene(playedScene);
                PauseTransition pauseTransition = new PauseTransition(Duration.millis(6000));
                pauseTransition.play();
                updateMouseView();
                pauseTransition.setOnFinished(e -> {
                    StackPane root = new StackPane();
                    StackPane gamePane = gameSceneHolder.paneCreator(1);
                    StackPane textPane = gameSceneHolder.UIOrganizer();

                    gameScene.setCursor(Cursor.NONE);

                    root.getChildren().addAll(gamePane, textPane, mousePane);
                    gameScene.setRoot(root);
                    window.setScene(gameScene);
                    horizontalScalingTimeline.setCycleCount(Timeline.INDEFINITE);
                    horizontalScalingTimeline.play();
                    List<Duck> duckList = gameSceneHolder.getActiveDuckList();
                    for(Duck duck : duckList){
                        double width = customizingSceneHolder.backgroundImageSelector(0).getFitWidth();
                        double height = customizingSceneHolder.backgroundImageSelector(0).getFitHeight();
                        duck.duckSetup(width, height);
                    }
                });
            }
        });

        gameScene.setOnMousePressed(e -> {
            int gameState = gameSceneHolder.getGameStatus(e.getSceneX(), e.getSceneY());
            if(gameState == 2 && curLevel == 6){
                StackPane winStack = transitionSceneHolder.getWinningScene();
                winScene.setRoot(winStack);
                window.setScene(winScene);
                setCurLevel(1);
                gameSceneHolder.duckListResetter();
            } else if(gameState == 2) {
                StackPane transStack = transitionSceneHolder.getNormalTrans();
                transScene.setRoot(transStack);
                window.setScene(transScene);
                setCurLevel(0);
                gameSceneHolder.duckListResetter();
            } else if (gameState == 0) {
                StackPane loseStack = transitionSceneHolder.getLosingScene();
                loseScene.setRoot(loseStack);
                window.setScene(loseScene);
                setCurLevel(1);
                gameSceneHolder.duckListResetter();
            }
        });

        gameScene.setOnMouseMoved(e -> {
            Pane imagePane = (Pane) gameScene.getRoot().getChildrenUnmodifiable().get(2);
            ImageView customCursorImage = (ImageView) imagePane.getChildren().get(0);
            double mouseX = e.getX() - customCursorImage.getFitWidth() / 2;
            double mouseY = e.getY() - customCursorImage.getFitHeight() / 2;

            imagePane.setTranslateX(mouseX);
            imagePane.setTranslateY(mouseY);
        });

        transScene.setOnKeyPressed(e -> {
            KeyCode keyCode = e.getCode();
            if(keyCode == KeyCode.ENTER){
                StackPane root = new StackPane();
                StackPane gamePane = gameSceneHolder.paneCreator(curLevel);
                StackPane textPane = gameSceneHolder.UIOrganizer();

                gameScene.setCursor(Cursor.NONE);

                root.getChildren().addAll(gamePane, textPane, mousePane);
                gameScene.setRoot(root);
                window.setScene(gameScene);
                horizontalScalingTimeline.setCycleCount(Timeline.INDEFINITE);
                horizontalScalingTimeline.play();
                transitionSceneHolder.stopTransitionPlayer();
                gameSceneHolder.stopDuckDeathPlayer();
                List<Duck> duckList = gameSceneHolder.getActiveDuckList();
                for(Duck duck : duckList){
                    double width = customizingSceneHolder.backgroundImageSelector(0).getFitWidth();
                    double height = customizingSceneHolder.backgroundImageSelector(0).getFitHeight();
                    duck.duckSetup(width, height);
                }
            }
        });

        loseScene.setOnKeyPressed(e -> {
            KeyCode keyCode = e.getCode();
            handleKeyPressForLossAndWin(keyCode, gameSceneHolder, horizontalScalingTimeline, transitionSceneHolder,
                    welcomeScene, welcomeSceneHolder);
        });

        winScene.setOnKeyPressed(e -> {
            KeyCode keyCode = e.getCode();
            handleKeyPressForLossAndWin(keyCode, gameSceneHolder, horizontalScalingTimeline, transitionSceneHolder,
                    welcomeScene, welcomeSceneHolder);
        });

        window.getIcons().add(new Image("assets\\favicon\\1.png"));
        window.setOnShown(e -> {
            window.setWidth(window.getWidth());
            window.setHeight(window.getHeight());
        });
        window.show();
    }

    /**
     * Sets the current level based on the situation the method was called from, to either move up a level or reset to
     * level 1.
     *
     * @param mode The mode to set the current level. 0 to increment the level, 1 to reset to level 1.
     */
    public void setCurLevel(int mode){
        if(mode == 0) curLevel++;
        else if(mode == 1) curLevel = 1;
    }

    /**
     * Updates the mouse cursor view in the game scene, so it is the one that the user chose at some time.
     */
    public void updateMouseView(){
        mouseView = customizingSceneHolder.crosshairImageSelector(0);
        mousePane.getChildren().remove(mouseView);
        mousePane.getChildren().add(mouseView);
    }

    /**
     * Handles key presses for the loss and win scenes. Since they both have very similar behaviours 1 method with many
     * parameters since they are private rather than having many getters or making variables public.
     *
     * @param keyCode               The KeyCode corresponding to the key pressed by the user.
     * @param gameSceneHolder       The GameSceneHolder instance.
     * @param horizontalScalingTimeline The timeline for horizontal scaling of the game scene.
     * @param transitionSceneHolder The TransitionSceneHolder instance.
     * @param welcomeScene          The welcome scene.
     * @param welcomeSceneHolder    The WelcomeSceneHolder instance.
     */
    public void handleKeyPressForLossAndWin(KeyCode keyCode, GameSceneHolder gameSceneHolder, Timeline horizontalScalingTimeline,
                               TransitionSceneHolder transitionSceneHolder, Scene welcomeScene,
                               WelcomeSceneHolder welcomeSceneHolder) {
        if (keyCode == KeyCode.ENTER) {
            gameSceneHolder.duckListResetter();
            StackPane root = new StackPane();
            StackPane gamePane = gameSceneHolder.paneCreator(curLevel);
            StackPane textPane = gameSceneHolder.UIOrganizer();

            gameScene.setCursor(Cursor.NONE);

            root.getChildren().addAll(gamePane, textPane, mousePane);
            gameScene.setRoot(root);
            window.setScene(gameScene);
            horizontalScalingTimeline.setCycleCount(Timeline.INDEFINITE);
            horizontalScalingTimeline.play();
            transitionSceneHolder.stopTransitionPlayer();
            gameSceneHolder.stopDuckDeathPlayer();
            List<Duck> duckList = gameSceneHolder.getActiveDuckList();
            for (Duck duck : duckList) {
                double width = customizingSceneHolder.backgroundImageSelector(0).getFitWidth();
                double height = customizingSceneHolder.backgroundImageSelector(0).getFitHeight();
                duck.duckSetup(width, height);
            }
        } else if (keyCode == KeyCode.ESCAPE) {
            window.setScene(welcomeScene);
            welcomeSceneHolder.mediaPlayerRunner();
            gameSceneHolder.duckListResetter();
            transitionSceneHolder.stopTransitionPlayer();
            gameSceneHolder.stopDuckDeathPlayer();
        }
    }
}

