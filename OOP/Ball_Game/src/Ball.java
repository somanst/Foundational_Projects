import java.awt.Point;
//This is the ball class which is the basis of our objects in our game, every Ball object has
//its attributes of score effect when collided with, its location on our grid, the symbol representing
//it on the grid, and its type (being player, score, normal, wall, or a hole).
public class Ball {

    int score;
    Point location;
    String symbol;
    String type;
    //And we have our methods that identify the mentioned attributes of the object based on its
    //symbol given in the board.txt input file, and the second method simply changes the location since
    //we are going to use this when changing places of the playerBall and the targetBall.
    public void typeIdentifier(int x, int y){
        switch (symbol) {
            case "B":
                type = "score";
                score = -5;
                break;
            case "R":
                type = "score";
                score = 10;
                break;
            case "Y":
                type = "score";
                score = 5;
                break;
            case "*":
                type = "player";
                score = 0;
                break;
            case "H":
                type = "hole";
                score = 0;
                break;
            case "W":
                type = "wall";
                score = 0;
                break;
            default:
                type = "normal";
                score = 0;
                break;
        }
        location = new Point(x,y);

    }
    public void setLocation(Point point){
        location = point;
    }
}
