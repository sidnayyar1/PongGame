package com.example.ponggame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainActivity extends AppCompatActivity {

    Canvas canvas;
   // PongView gameView;
    Display display;
    Point screenSize;
    int screenWidth;
    int screenHeight;


    long timeOfLastFrame;
    long fps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      //  this.gameView = new PongView(this);
       // setContentView(this.gameView);
        this.display = this.getWindowManager().getDefaultDisplay();
        this.screenSize = new Point();
        this.display.getSize(screenSize);
        this.screenHeight = screenSize.y;
        this.screenWidth = screenSize.x;

    }
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

        public void updatePositions() {
            //@TODO: Put your code to update the (x,y) positions of the sprites
        }

        public void drawPositions() {
            // @TODO:  Put code to actually draw the sprites on the screen
        }

        public void setFPS() {
            //@TODO: Implement code to deal with FPS
            // Generally, you want around 60 frames per second.

            long timeOfCurrentFrame = (System.currentTimeMillis() - timeOfLastFrame);
            long timeToSleep = 15 - timeOfCurrentFrame;
            if (timeOfCurrentFrame > 0) {
                fps = (int) (1000 / timeOfCurrentFrame);
            }
            if (timeToSleep > 0) {
                try {
                    gameThread.sleep(timeToSleep);

                } catch (InterruptedException e) {
                    //@TODO: Optional - put some error messaging here
                }
            }
            timeOfLastFrame = System.currentTimeMillis();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            //@TODO: Add code to deal with user input (user touching the screen)
            return super.onTouchEvent(event);
        }


        public void pauseGame() {
            //@TODO: Code for when the user pauses the game
        }

        public void resumeGame() {
            //@TODO: Code for when the the game is "unpaused" (or user is playing again)
        }
    }
}