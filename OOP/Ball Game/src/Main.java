import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.Point;
public class Main {

    //Let's start with the additional methods. First, we have a method that simply is responsible for the ball
    //falling out of boundaries of the grid, making it change the targeted location to the other side of the board
    //, with our grid have the coordinates of positive x to the right and positive y to the bottom. Therefore, if our ball
    //This method is going to be used after every target point conclusion to make sure that nothing unnatural happens.
    public static Point pointPurifier(Point targetLocation, int colNum, int rowNum){
        if(targetLocation.x >= colNum){
            targetLocation.x = (targetLocation.x - colNum);
        } else if (targetLocation.x < 0) {
            targetLocation.x = colNum + targetLocation.x;
        } else if (targetLocation.y >= rowNum) {
            targetLocation.y = (targetLocation.y - rowNum);
        } else if (targetLocation.y == -1) {
            targetLocation.y = colNum + targetLocation.y;
        }
        return targetLocation;
    }
    //Then comes our other method that based on user's input and player's location give a "purified" target point (using the purifier
    //method), creates a null ball and finds the ball from the main ball list that we create later on based on the target location
    //we found at the start and returns it.
    public static Ball targetDefiner(String move, Ball playerBall, int colNum, int rowNum, List<Ball> ballList){
        Point targetLocation = new Point();
        switch (move) {
            case "R":
                targetLocation = new Point(playerBall.location.x + 1, playerBall.location.y);
                break;
            case "L":
                targetLocation = new Point(playerBall.location.x - 1, playerBall.location.y);
                break;
            case "U":
                targetLocation = new Point(playerBall.location.x, playerBall.location.y - 1);
                break;
            case "D":
                targetLocation = new Point(playerBall.location.x, playerBall.location.y + 1);
                break;
        }
        targetLocation = pointPurifier(targetLocation, colNum, rowNum);

        Ball targetBall = new Ball();
        for(Ball ball : ballList){
            if(ball.location.x == targetLocation.x && ball.location.y == targetLocation.y){
                targetBall = ball;
            }
        }
        return targetBall;
    }
    //After that comes the final side method which is mainly responsible for the encountering of
    //our player with a ball.it first checks what side did the player hit the wall from then sends
    //him back in the opposite direction 2 steps, and again returns a purified location point with
    //our purifier method.
    public static Point typeReaction(String move, Ball playerBall, int colNum, int rowNum, List<Ball> ballList){
        Ball targetBall = targetDefiner(move, playerBall, colNum, rowNum, ballList);
        Point targetPoint = new Point(targetBall.location);
        if(targetBall.type.equals("wall")){
            switch (move) {
                case "R":
                    targetPoint = new Point(targetPoint.x - 2, targetPoint.y);
                    break;
                case "L":
                    targetPoint = new Point(targetPoint.x + 2, targetPoint.y);
                    break;
                case "U":
                    targetPoint = new Point(targetPoint.x, targetPoint.y + 2);
                    break;
                case "D":
                    targetPoint = new Point(targetPoint.x, targetPoint.y - 2);
                    break;
            }
            targetPoint = pointPurifier(targetPoint, colNum, rowNum);
            return targetPoint;
        } else {
            targetPoint = pointPurifier(targetPoint, colNum, rowNum);
            return new Point(targetPoint);
        }
    }
    //Here comes our starting of the main method. We first declare a couple of important variables
    //like the ballList that will contain all ball class objects, initial board information, total score,
    //and initialization of row and column quantities.
    public static void main(String[] args) throws IOException {
        String[] lines = ReadFromFile.readFile(args[0]);
        List<Ball> ballList = new ArrayList<>();
        int score = 0;
        int colNum = 0;
        int rowNum = 0;
        //Then comes the filtering of the initial board where we extract grid size and ball information like
        //their colors and types using the typeIdentifier method from the class Ball, and insert
        //them into our main ballList.
        for (String line : lines) {
            colNum = 0;
            String[] splitLine = line.split(" ");
            for (String symbolRep : splitLine) {
                Ball tempBall = new Ball();
                tempBall.symbol = symbolRep;
                tempBall.typeIdentifier(colNum, rowNum);
                ballList.add(tempBall);
                colNum++;
            }
            rowNum++;
        }
        //Here we are simply getting a printed view of our initial board for the user output.
        String startingBoardView = "";
        for(int f = 0; f < rowNum; f++){
            for(int n = 0; n < colNum -1; n++){
                startingBoardView += (ballList.get(f * colNum + n).symbol + " ");
            }
            startingBoardView += (ballList.get(f * colNum + colNum -1).symbol + "\n");
        }
        //After that we find the playerBall in the ballList indicated by its type
        Ball playerBall = new Ball();
        for (Ball object : ballList) {
            if (object.type.equals("player")) {
                playerBall = object;
            }
        }
        //After initializing a few variables like losing status (whether the player fell into a hole or not),
        //moves performed until falling, and moves from the moves file, the main block of the program comes where
        //the movement of the balls in the grid and the calling of the methods will occur.
        String playedMoves = "";
        boolean lost = false;
        String[] moveSet = ReadFromFile.readFile(args[1]);
        for (String moves : moveSet) {
            String[] moveSplit = moves.split(" ");
            //We will first declare some of the needed variables that we are going to use like the location
            //that our ball will go to, a temporary ball object to use it making the transferring of the player
            //and the destined ball easier, and the target ball object. Then we use the typeReaction method
            //to get a reaction based on the type of the target ball after calculating the target location
            //and returning it purified as we explained previously.
            for (String move : moveSplit) {
                Point targetLocation;
                Ball tempBall = new Ball();
                Ball targetBall = new Ball();
                targetLocation = typeReaction(move, playerBall, colNum, rowNum, ballList);
                for(Ball ball : ballList){
                    if(ball.location.x == targetLocation.x && ball.location.y == targetLocation.y){
                        targetBall = ball;
                    }
                }
                //After we have made sure that the target location is correct we relate it to the ball it is supposed
                //to change places with from the ballList, we add its score if any to the player's total score and add
                //the move as a verified one, then check if the target is a hole so the game can end after that or if
                //it is a score ball so that it is no longer abusable for more score.
                score += targetBall.score;
                playedMoves += move + " ";
                if(targetBall.type.equals("hole")){
                    lost = true;
                    break;
                } else if (targetBall.type.equals("score")) {
                    targetBall.symbol = "X";
                    targetBall.score  = 0;
                }
                //And after that comes the point where we switch places of the target ball and the player ball on the grid
                //and in the ballList so that it is always updated when a new move happens.
                int playerBallIndex = colNum * playerBall.location.y + playerBall.location.x;
                int targetBallIndex = colNum * targetBall.location.y + targetBall.location.x;
                tempBall.setLocation(playerBall.location);
                playerBall.setLocation(targetBall.location);
                targetBall.setLocation(tempBall.location);
                Collections.swap(ballList, targetBallIndex, playerBallIndex);

            }
        }//In the end, we create an output file and print out all the results into it.
        File output = new File("output.txt");
        FileWriter w = new FileWriter(output);
        w.write("Game board:\n" + startingBoardView + "\n");
        w.write("Your movement is:\n" + playedMoves + "\n\n");
        w.write("Your output is:\n");
        for(int f = 0; f < rowNum; f++){
            for(int n = 0; n < colNum -1; n++){
                w.write(ballList.get(f * colNum + n).symbol + " ");
            }
            w.write(ballList.get(f * colNum + colNum -1).symbol + "\n");
        }
        w.write("\n");
        if(lost){
        w.write("Game Over!\n");
        }
        w.write("Score: " + score);
        w.close();
    }
}