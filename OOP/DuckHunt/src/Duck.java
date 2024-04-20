import javafx.animation.*;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Objects;

/**
 * The Duck class represents all ducks objects in the game, having their own animation frames and specifications, with
 * timelines to achieve their movements and animations.
 */
public class Duck {
    private final String color;
    private final int mode;
    private final double SCALE;
    private final List<Image> animationFramesList;
    private List<Image> flightFrames;
    private List<Image> deathFrames;
    private Timeline animationTimeline;
    private ImageView currentFrame;
    private Timeline ducksMovement;
    private int xDirection;
    private int yDirection;
    private int leftBorder;
    private int rightBorder;
    private int upperborder = -10;
    private int buttomborder = 190;
    private int speed = 20;
    private  final String spawnLoc;

    /**
     * Constructs a Duck object with the specified parameters.
     *
     * @param color_      the color of the duck
     * @param mode_       the mode of the duck, meaning is it only horizontal moving or horizontal and vertical
     * @param scale      the scale of the size of the duck
     * @param xDirection_ the x-direction of the duck
     * @param yDirection_ the y-direction of the duck
     * @param spawnLoc_   the spawn location of the duck that also specifies the borders of the map according to this
     *                    this duck
     */
    public Duck(String color_, int mode_, double scale, int xDirection_, int yDirection_, String spawnLoc_){
        color = color_;
        mode = mode_;
        SCALE = scale;
        xDirection = xDirection_;
        yDirection = yDirection_;
        spawnLoc = spawnLoc_;
        if(spawnLoc_.equals("left") || spawnLoc_.equals("topL")){
            leftBorder = -10;
            rightBorder = 700;
        } else if (spawnLoc_.equals("right") || spawnLoc_.equals("topR")) {
            leftBorder = -700;
            rightBorder = 0;
        }
        if(!spawnLoc.contains("top")){
            upperborder -= 90;
            buttomborder -= 80;
        }
        animationFramesList = new ArrayList<>();
        animationCollector();
        animationSorter();
        timelineCreator();
    }


    /**
     * Collects the animation frames for the duck from the specified folder taking into account color.
     */
    public void animationCollector(){
        File file = new File(String.format("assets\\duck_%s", color));
        for(File someFile : Objects.requireNonNull(file.listFiles())){
            animationFramesList.add(new Image(String.valueOf(someFile.toURI())));
        }
    }

    /**
     * Sorts the animation frames based on the duck's mode.
     */
    public void animationSorter(){
        if(mode == 0) flightFrames = animationFramesList.subList(3,6);
        if(mode == 1)flightFrames = animationFramesList.subList(0,3);
        deathFrames = animationFramesList.subList(6,8);
    }

    /**
     * Creates the timeline for the duck's animation.
     */
    public void timelineCreator(){
        currentFrame = new ImageView(flightFrames.get(0));
        currentFrame.setScaleX(SCALE * xDirection);
        currentFrame.setScaleY(SCALE * yDirection);
        animationTimeline = new Timeline();
        KeyFrame keyFrame1 = new KeyFrame(Duration.seconds(0), new KeyValue(currentFrame.imageProperty(), flightFrames.get(0)));
        KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(0.5), new KeyValue(currentFrame.imageProperty(), flightFrames.get(1)));
        KeyFrame keyFrame3 = new KeyFrame(Duration.seconds(1), new KeyValue(currentFrame.imageProperty(), flightFrames.get(2)));
        KeyFrame keyFrame4 = new KeyFrame(Duration.seconds(1.5), new KeyValue(currentFrame.imageProperty(), flightFrames.get(2)));
        animationTimeline.getKeyFrames().addAll(keyFrame1, keyFrame2, keyFrame3, keyFrame4);
        animationTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Gets the current frame of the duck's animation.
     *
     * @return the ImageView representing the current frame
     */
    public ImageView getCurrentFrame(){
        return currentFrame;
    }

    /**
     * Inverts the duck's frame in the x-direction.
     */
    public void frameXInverter(){
        currentFrame.setScaleX(currentFrame.getScaleX() * -1);
        xDirection *= -1;
    }

    /**
     * Inverts the duck's frame in the y-direction.
     */
    public void frameYInverter(){
        currentFrame.setScaleY(currentFrame.getScaleY() * -1);
        yDirection *= -1;
    }

    /**
     * Creates the timeline for the duck's visible movement, taking into account duck's placement and mode. It checks
     * every 0.5 secs where the duck is located at and processes whether it has passed the border of the screen. If so
     * invert the duck on the border's axis, if not then add a translation of speed * SCALE in the xDirection it needs
     * to go.
     */
    public void ducksMovementTimelineCreator(){
        ImageView imageView = currentFrame;
        Parent parent = imageView.getParent();
        ducksMovement = new Timeline(new KeyFrame(Duration.millis(500) , e -> {
            if(mode == 0){
                if(parent.getTranslateX() < leftBorder * SCALE || parent.getTranslateX() > rightBorder * SCALE) frameXInverter();
                parent.setTranslateX(parent.getTranslateX() + xDirection * speed * SCALE);
            } else if (mode == 1) {
                if(parent.getTranslateX() < leftBorder * SCALE || parent.getTranslateX() > rightBorder * SCALE) frameXInverter();
                if(parent.getTranslateY() > buttomborder * SCALE || parent.getTranslateY() < upperborder * SCALE) frameYInverter();
                parent.setTranslateX(parent.getTranslateX() + xDirection * speed * SCALE);
                parent.setTranslateY(parent.getTranslateY() + yDirection * -speed * SCALE);
            }
        }));
            ducksMovement.setCycleCount(Timeline.INDEFINITE);
            animationTimeline.play();
            ducksMovement.play();
        }

    /**
     * Executes the duck's death animation.
     */
    public void duckDeath(){
        if(yDirection == -1)  frameYInverter();
        Duration halfSecond = Duration.millis(500);
        Parent parent = currentFrame.getParent();
        currentFrame.setImage(deathFrames.get(0));
        KeyFrame keyFrame2 = new KeyFrame(halfSecond, e -> {
            currentFrame.setImage(deathFrames.get(1));
            parent.setTranslateY(parent.getTranslateY() + 20 * SCALE);
        });

        ducksMovement.stop();
        animationTimeline.stop();
        Timeline timeline = new Timeline(keyFrame2);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(false);

        timeline.play();
    }

    /**
     * Sets up the initial position of the duck.
     *
     * @param width  the width of the game area
     * @param height the height of the game area
     */
    public void duckSetup(double width, double height){
        width *= 3;
        if(spawnLoc.equals("topL")){
            currentFrame.setTranslateX(30 * SCALE);
            currentFrame.setTranslateY(40 * SCALE);
        } else if (spawnLoc.equals("left")) {
            currentFrame.setTranslateX(30 *SCALE);
            currentFrame.setTranslateY(height/2);
        } else if(spawnLoc.equals("right")){
            currentFrame.setTranslateX(width - 30 * SCALE);
            currentFrame.setTranslateY(height/2 - 20 *SCALE);
        }else if(spawnLoc.equals("topR")) {
            currentFrame.setTranslateX(width - 30 * SCALE);
            currentFrame.setTranslateY(20 * SCALE);
        }
    }

    /**
     * Increases the speed of the duck's movement.
     *
     * @param speed the amount to increase the speed by
     */
    public void speedUp(int speed){
        this.speed += speed;
    }
}
