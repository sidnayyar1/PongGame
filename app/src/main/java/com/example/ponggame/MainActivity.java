package com.example.ponggame;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    final static String TAG = "PONG-GAME";

    // variables for drawing
    // ---------------------
    Canvas canvas;
    PongView gameView;
    Display display;
    Point screenSize;
    int screenWidth;
    int screenHeight;

    // variables for FPS
    // -----------------
    long timeOfLastFrame;
    long fps;


    // game objects - sprites!
    // -----------------------
    Point ballPosition;
    int ballWidth;

    Point racketPosition;
    int racketWidth;
    int racketHeight;


    // movement variables
    // -----------------------
    boolean ballIsMovingUp;
    boolean ballIsMovingDown;

    boolean racketIsMovingLeft;
    boolean racketIsMovingRight;

    // game statistics (score, lives, etc)
    // -----------------------------------
    int score = 0;


    // variable for sounds
    // ------------------
    SoundPool soundPool;
    int sound1 = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // @TODO: Do your game setup here

        // Setup this activity to use the PongView as its context
        this.gameView = new PongView(this);
        setContentView(this.gameView);

        // Setup the screen size for your game
        this.display = this.getWindowManager().getDefaultDisplay();
        this.screenSize = new Point();
        this.display.getSize(screenSize);
        this.screenHeight = screenSize.y;
        this.screenWidth = screenSize.x;

        // Setup sound, initial position of objects, score, lives, etc

        // Setup the ball
        // ---------------------

        // 1. size of ball
        this.ballWidth = this.screenWidth / 35;

        // 2. initial position of the ball on the screen
        ballPosition = new Point();
        ballPosition.x = this.screenWidth / 2;   // x position = middle
        //ballPosition.y = 1 + this.ballWidth;     // OPTION 1: y position = somewhere near the top of the screen
        ballPosition.y = this.screenHeight / 2;    // OPTION :  y position = middle of screen

        // Setup the tennis racket
        // -----------------------
        this.racketWidth = this.screenWidth / 4;    // some random width. Change it if you want.

        // -------------------------------------
        // 0a. Make the racket bigger so it's easier to work with
        // -------------------------------------
        this.racketHeight = 100;

        this.racketPosition  = new Point();
        this.racketPosition.x = this.screenWidth / 2;
        this.racketPosition.y = this.screenHeight - 400;     // somewhere near the bottom of the screen


        // Movement setup
        // ----------------

        // Setup initial direction of ball
        this.ballIsMovingDown = true;


        // Add Sounds to your game
        // -----------------
        this.soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            AssetManager assetManager = getAssets();
            AssetFileDescriptor descriptor = assetManager.openFd("bounce.wav");
            this.sound1 = soundPool.load(descriptor, 0);
        }
        catch(IOException e) {

        }
    }


    // Android lifecycle functions
    // ----------------------------

    @Override
    protected void onStop() {
        super.onStop();
        //@TODO: What do you want to happen when the user stops the APP?
    }

    @Override
    protected void onPause() {
        super.onPause();

        //@TODO: What should happen when the APP is paused?
    }

    @Override
    protected void onResume() {
        super.onResume();
        //@TODO: What should happen when the APP is resumed?

        // this is how you start the game
        this.gameView.resumeGame();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
        //@TODO: What should happen when user presses the BACK button on their phone?
    }

    // ----------------------------------------------------------------
    // Create an internal class that is used to manage all the drawing
    // ----------------------------------------------------------------
    class PongView extends SurfaceView implements Runnable {

        Thread gameThread = null;
        SurfaceHolder holder;
        boolean gameIsRunning;
        Paint paintbrush;

        public PongView(Context context) {
            super(context);
            this.holder = this.getHolder();
            paintbrush = new Paint();

            // @TODO: setup the ball

            // @TODO: send the ball in a random direction
        }



        @Override
        public void run() {
            while (gameIsRunning == true) {
                this.updatePositions();
                this.drawPositions();
                this.setFPS();
            }
        }

        boolean ballMovingDown = true;
        boolean ballMovingUp = false;


        // -------------------------------------
        // 0b. Add a helper function to check if ball is touching racket.
        // -------------------------------------
        public boolean ballIsTouchingRacket(Point position) {

            /**
             * To calculate if the ball is inside the racket, you need to compare
             * the ball's position to the racket
             *
             * Here is the racket:
             *
             *    w = width of racket
             *    h = height of racket
             *
             *    (x,y) .---------------. (x+w, y)
             *          |               |
             *          |               |
             *  (x+h,y) .---------------. (x+w, y+h)
             *
             *
             * If the ball is INSIDE any of these coordinates, then ball is touching racket.
             * Else, ball is NOT touching racket
             */


            // -------------------------------------
            // 1. Setup some variables to keep track of if ball is inside the racket
            // -------------------------------------
            boolean ballInsideRacketWidth = false;
            boolean ballInsideRacketHeight = false;


            // -------------------------------------
            // 2. Get the coordinates of the racket.
            // Specifically, I am using the top left corner of the racket as my (x,y)
            // Note - if you compare this to drawPositions() function,
            // you will see I used:
            //      x = rktLeft
            //      y = rktTop
            // -------------------------------------

            // This code is copied and pasted from the drawPositions() function below
            int racketLeft = racketPosition.x - (racketWidth / 2);
            int racketTop = racketPosition.y - (racketHeight / 2);


            // -------------------------------------
            // 3. Get the coordinates of the ball
            // -------------------------------------
            int ballX = position.x;
            int ballY = position.y;


            // -------------------------------------
            // 4. Check if the ball's x position is inside the racket
            // -------------------------------------

            // check x-coordinate ( x <= ball.X <= x+w)
            if (ballX >= racketLeft && ballX <= (racketLeft + racketWidth) ) {
                ballInsideRacketWidth = true;
            }

            // -------------------------------------
            // 5. Check if the ball's y position is inside the racket
            // -------------------------------------

            // check if ball is inside racket's height (y < = ball.y < y+h)
            if (ballY >= racketTop && ballY <= (racketTop + racketHeight) ) {
                ballInsideRacketHeight = true;
            }

            // -------------------------------------
            // 6. If ball is inside racket, return true.
            // Else return false.
            // -------------------------------------
            if (ballInsideRacketWidth && ballInsideRacketHeight) {
                Log.d(TAG, "Ball is inside racket");
                return true;
            }
            Log.d(TAG, "Ball NOT inside racket");
            return false;
        }

        public void updatePositions() {
            //@TODO: Put your code to update the (x,y) positions of the sprites

            // Make the ball move
            // -------------------------

            if (ballIsMovingDown == true) {
                ballPosition.y = ballPosition.y + 5;


                // Check if ball is touching racket
                // If yes, then bounce the ball

                // -------------------------------------
                // 7. Update your game logic to use the function to check if ball is touching racket
                // -------------------------------------

                if (ballIsTouchingRacket(ballPosition) == true)  {
                    // change direction of ball
                    ballIsMovingDown = false;
                    ballIsMovingUp = true;

                    // increase the score
                    score = score + 1;

                    // play a "bounce" sound
                    soundPool.play(sound1, 1,1,0,0,1);

                }
            }

            if (ballIsMovingUp == true) {
                ballPosition.y = ballPosition.y - 5;
                // If the new ball position is at top of screen,
                // then switch directions again
                if (ballPosition.y <= 0) {
                    ballIsMovingDown = true;
                    ballIsMovingUp = false;

                    // play a "bounce" sound
                    soundPool.play(sound1, 1,1,0,0,1);
                }

            }

            // Make racket move
            // -------------------------

            if (racketIsMovingRight == true) {
                racketPosition.x = racketPosition.x + 10;
            }

            if (racketIsMovingLeft == true) {
                racketPosition.x = racketPosition.x - 10;
            }
        }

        public void drawPositions() {
            // @TODO:  Put code to actually draw the sprites on the screen

            // required nonsense
            if (this.holder.getSurface().isValid()) {
                // prevent other apps from modifying the canvas
                canvas = this.holder.lockCanvas();

                // background color
                canvas.drawColor(Color.BLACK);

                // color of objects
                paintbrush.setColor(Color.argb(255, 255, 255, 255));

                // size of font
                paintbrush.setTextSize(45);

                // code to draw the ball
                // -----------------------

                // the ball is a square (you could use a circle)
                // For squares/rectangles, give coordinates of all 4 corners

                int left = ballPosition.x;
                int top = ballPosition.y;
                int right = ballPosition.x + ballWidth;
                int bottom = ballPosition.y + ballWidth;
                canvas.drawRect(left, top, right, bottom, paintbrush);

                // code to draw the ball
                // -----------------------

                // you could also use a line
                int rktLeft = racketPosition.x - (racketWidth / 2);
                int rktTop = racketPosition.y - (racketHeight / 2);
                int rktRight = racketPosition.x + (racketWidth / 2);
                int rktBottom = racketPosition.y + (racketHeight / 2);

                canvas.drawRect(rktLeft, rktTop, rktRight, rktBottom, paintbrush);


                // code to draw game statistics (scores, lives, etc)
                // ----------------------------------------------

                String message = "Your score: " + score;
                canvas.drawText(message, 10, 100, paintbrush);


                // we are done drawing, so we can "unlock" the canvas.
                // other apps can access it now
                this.holder.unlockCanvasAndPost(canvas);
            }
        }

        public void setFPS() {
            //@TODO: Implement code to deal with FPS
            // Generally, you want around 60 frames per second.

            long timeOfCurrentFrame = (System.currentTimeMillis()  - timeOfLastFrame);
            long timeToSleep = 15 - timeOfCurrentFrame;
            if (timeOfCurrentFrame > 0) {
                fps = (int) (1000/ timeOfCurrentFrame);
            }
            if (timeToSleep > 0) {
                try {
                    gameThread.sleep(timeToSleep);

                }
                catch (InterruptedException e) {
                    //@TODO: Optional - put some error messaging here
                }
            }
            timeOfLastFrame = System.currentTimeMillis();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            //@TODO: Add code to deal with user input (user touching the screen)


            // I have no idea what this means.
            // Mandatory nonsense to detect when person taps the screen
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    // press finger down
                    Log.d(TAG, "The person tapped: (" + event.getX() + "," + event.getY() + ")");


                    // if left side, move racket left
                    double middleOfScreen = screenWidth / 2;
                    if (event.getX() <= middleOfScreen) {
                        racketIsMovingLeft = true;
                        racketIsMovingRight = false;
                    }
                    else {
                        // if right side, move racket right
                        racketIsMovingRight = true;
                        racketIsMovingLeft = false;
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    // person lifted their finger
                    racketIsMovingRight = false;
                    racketIsMovingLeft = false;
                    break;
            }

            return true;
        }


        public void pauseGame() {
            //@TODO: Code for when the user pauses the game
        }

        public void resumeGame() {
            //@TODO: Code for when the the game is "unpaused" (or user is playing again)

            // this is how you "start" the game loop
            gameIsRunning = true;
            this.gameThread = new Thread(this);
            this.gameThread.start();
        }

    } // --- End of PongView class
}
