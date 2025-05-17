package com.example.tryhard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;


public class Game extends View {
    Context context;


    Paint paint1 = new Paint();
    Paint paint2 = new Paint();
    Paint textPaint = new Paint();
    Paint life3 = new Paint();
    Paint life2 = new Paint();
    Paint life1 = new Paint();


    float x = 200;
    float y = 200;
    float radius = 20;
    float vX = 10;
    float vY = 10;
    float acceleration=1f; //    choose acceleration later
    float paddleX1 = 500;
    float paddleY1 = 1800;
    float width = 230;
    float height = 20;

    int hitCounter = 0;
    int life = 3;


    int healthBarWidth = 250;
    int healthBarHeight = 80;


    Paddle paddle = new Paddle(paddleX1, paddleY1, width, height);
    Ball ball = new Ball(x, y, radius, vX, vY);

    public Game(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Set paint colors
        paint1.setColor(Color.RED);
        paint2.setColor(Color.BLUE);
        textPaint.setColor(Color.GREEN);
        textPaint.setTextSize(60);
        textPaint.setAntiAlias(true);


        life1.setColor(Color.RED);
        life2.setColor(Color.YELLOW);
        life3.setColor(Color.GREEN);


        canvas.drawColor(Color.BLACK);


        ball.setX(ball.getX() + ball.getvX());
        ball.setY(ball.getY() + ball.getvY());


        if (ball.getX() - ball.getRadius() <= 0 || ball.getX() + ball.getRadius() >= getWidth()) {
            ball.setvX(-ball.getvX());
        }


        if (ball.getY() - ball.getRadius() <= 0) {
            ball.setvY(-ball.getvY());
        }


        if (ball.getY() + ball.getRadius() >= paddle.getPaddleY1()
                && ball.getX() - ball.getRadius() < paddle.getPaddleX1() + paddle.getWidth()
                && ball.getX() + ball.getRadius() > paddle.getPaddleX1()) {

            hitCounter++;
            ball.setvY(-ball.getvY()*acceleration);
        }
        else if (ball.getY() + ball.getRadius() >= paddle.getPaddleY1()) {
            life--;


            if (life == 0) {
                Intent intent = new Intent(context, GameOver.class);
                intent.putExtra("points", hitCounter);
                context.startActivity(intent);
                ((Activity) context).finish();
            }


            Random random = new Random();
            int number = random.nextInt(getWidth() - 10) + 10;
            ball.setX(number);
            ball.setY(50);
        }


        if (life == 3) {
            canvas.drawRect(getWidth() - healthBarWidth, 20, getWidth(), healthBarHeight + 20, life3);
        } else if (life == 2) {
            canvas.drawRect(getWidth() - (healthBarWidth * 2) / 3, 20, getWidth(), healthBarHeight + 20, life2);
        } else if (life == 1) {
            canvas.drawRect(getWidth() - healthBarWidth / 3, 20, getWidth(), healthBarHeight + 20, life1);
        }


        canvas.drawCircle(ball.getX(), ball.getY(), ball.getRadius(), paint1);


        canvas.drawRect(paddle.getPaddleX1(), paddle.getPaddleY1(),
                paddle.getPaddleX1() + paddle.getWidth(),
                paddle.getPaddleY1() + paddle.getHeight(), paint2);


        canvas.drawText("Score: " + hitCounter, 50, 100, textPaint);

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {

            paddle.setPaddleX1(event.getX() - paddle.getWidth() / 2);
        }


        if (paddle.getPaddleX1() < 0) {
            paddle.setPaddleX1(0);
        }
        if (paddle.getPaddleX1() + paddle.getWidth() > getWidth()) {
            paddle.setPaddleX1(getWidth() - paddle.getWidth());
        }

        return true;
    }
}
