package com.example.touchmenot_deltatask2;

import static com.example.touchmenot_deltatask2.MainThread.canvas;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView<context> extends SurfaceView implements SurfaceHolder.Callback {
    public context mContext;
    private MainThread thread;
    private CharacterSprite characterSprite;
    public PipeSprite pipe1, pipe2, pipe3;
    public static int gapHeight = 500;
    public static int velocity = 10;
    private TextView hello;
    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
    private int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    public String text="";
    public String text1="";
    public String text2="";
    public String text4="";
    private int highScore;
    public int score = 0;
    public int highscore = 0;
    private int state=1;


    public GameView(Context context) {
        super(context);

        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        setFocusable(true);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }




    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        makeLevel();


        thread.setRunning(true);
        thread.start();

    }



    private void makeLevel() {
        characterSprite = new CharacterSprite
                (getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ball), 300, 240));
        Bitmap bmp;
        Bitmap bmp2;
        int y;
        int x;
        bmp = getResizedBitmap(BitmapFactory.decodeResource
                (getResources(), R.drawable.spike_down), 500, Resources.getSystem().getDisplayMetrics().heightPixels / 2);
        bmp2 = getResizedBitmap
                (BitmapFactory.decodeResource(getResources(), R.drawable.spike_up), 500, Resources.getSystem().getDisplayMetrics().heightPixels / 2);

        pipe1 = new PipeSprite(bmp, bmp2, 2000, 100);
        pipe2 = new PipeSprite(bmp, bmp2, 4500, 100);
        pipe3 = new PipeSprite(bmp, bmp2, 3200, 100);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    public void update() {
        logic();
        characterSprite.update();
        pipe1.update();
        pipe2.update();
        pipe3.update();
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (state == 0) {

            canvas.drawColor(Color.WHITE);
            characterSprite.draw(canvas);
            pipe1.draw(canvas);
            pipe2.draw(canvas);
            pipe3.draw(canvas);


            Paint paint = new Paint();
            canvas.drawText(text, 100, 100, paint);
            paint.setTextSize(200);
            canvas.drawText(text1, 100, 420, paint);
            paint.setTextSize(50);
            canvas.drawText(text2, 750, 520, paint);
            paint.setTextSize(100);
            canvas.drawText(text4, 300, 700, paint);
            paint.setTextSize(100);

        } else if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            text = "GAME OVER";
            text1 = "Score=" + String.valueOf(score);
            text2 = "Play Again";
            text4 = String.valueOf(highScore);

            Paint paint = new Paint();
            canvas.drawText(text, 600, 420, paint);
            paint.setTextSize(200);
            canvas.drawText(text1, 600, 420, paint);
            paint.setTextSize(50);
            canvas.drawText(text2, 750, 520, paint);
            paint.setTextSize(100);
            canvas.drawText(text4, 1450, 100, paint);

            characterSprite.draw(canvas);
            pipe1.draw(canvas);
            pipe2.draw(canvas);
            pipe3.draw(canvas);

        }
    }

    public void logic() {

        List<PipeSprite> pipes = new ArrayList<>();
        pipes.add(pipe1);
        pipes.add(pipe2);
        pipes.add(pipe3);

        for (int i = 0; i < pipes.size(); i++) {
            //Detect if the character is touching one of the pipes
            if (characterSprite.y < pipes.get(i).yY + (screenHeight / 2) - (gapHeight / 2) && characterSprite.x + 300 > pipes.get(i).xX && characterSprite.x < pipes.get(i).xX + 500) {
                gameover();
                thread.interrupt();


                // resetLevel();
            } else if (characterSprite.y + 240 > (screenHeight / 2) + (gapHeight / 2) + pipes.get(i).yY && characterSprite.x + 300 > pipes.get(i).xX && characterSprite.x < pipes.get(i).xX + 500) {
                gameover();
                thread.interrupt();
                // OnFalse1();
            } else {
                score = score + 1;
            }

            //Detect if the pipe has gone off the left of the screen and regenerate further ahead
            if (pipes.get(i).xX + 500 < 0) {
                Random r = new Random();
                int value1 = r.nextInt(500);
                int value2 = r.nextInt(500);
                pipes.get(i).xX = screenWidth + value1 + 1000;
                pipes.get(i).yY = value2 - 250;
            }
        }

        //Detect if the character has gone off the bottom or top of the screen
        if (characterSprite.y + 240 < 0) {
            gameover();
            thread.interrupt();
            //resetLevel();
        }
        if (characterSprite.y > screenHeight) {
            gameover();
            thread.interrupt();
            //resetLevel();
        }
    }


    public void resetLevel() {

        characterSprite.y = 100;
        pipe1.xX = 2000;
        pipe1.yY = 0;
        pipe2.xX = 4500;
        pipe2.yY = 200;
        pipe3.xX = 3200;
        pipe3.yY = 250;
        thread.start();
        Log.i("resetlevel","working");
    }

    public void gameover() {
        Log.i("game over execution", "hello2");
        state = 0;
        if (score > highScore) {
            highScore = score;
        }
       // thread.setRunning(false);
        text = "GAME OVER";
        text1 = "Score= " + String.valueOf(score);
        text2 = "Play Again";
        text4 = "highScore=" + String.valueOf(highScore);
        Object event = null;
        onTouchEvent((MotionEvent) event);

    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (state == 0 && (event.getX() > 600 && event.getX() < 1000) && (event.getY() > 400 && event.getY() < 850) ){
            Log.i("hello",String.valueOf(event.getX()));
            Log.i("hello1",String.valueOf(event.getY()));
            Log.i("state1", String.valueOf(state));
            resetLevel();
            Log.i("alive",String.valueOf(thread.isAlive()));

            thread.start();
            Log.i("alive",String.valueOf(thread.isAlive()));

        }
        characterSprite.y = characterSprite.y - (characterSprite.yVelocity * 10);
        return super.onTouchEvent(event);
    }
    public boolean onTouchEvent1(MotionEvent event) {
        Log.i("ola",String.valueOf(event.getX()));
        Log.i("olla",String.valueOf(event.getY()));
        if ((event.getX() > 700 && event.getX() < 1500) && (event.getY() > 700 && event.getY() < 850)) {
            resetLevel();
            thread.start();

        }
        return super.onTouchEvent(event);
    }



}

