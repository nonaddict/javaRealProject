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

// Custom View class for the main game logic
public class Game extends View {
    Context context;

    // Paint objects for different elements
    Paint paint1 = new Paint();        // Ball paint
    Paint paint2 = new Paint();        // Paddle paint
    Paint textPaint = new Paint();     // Score text paint
    Paint life3 = new Paint();         // Health bar (3 lives)
    Paint life2 = new Paint();         // Health bar (2 lives)
    Paint life1 = new Paint();         // Health bar (1 life)

    // Ball position and velocity
    float x = 200;
    float y = 200;
    float radius = 20;
    float vX = 6;
    float vY = 6;

    // Paddle position and dimensions
    float paddleX1 = 500;
    float paddleY1 = 1800;
    float width = 230;
    float height = 20;

    int hitCounter = 0;  // Score counter
    int life = 3;        // Remaining lives

    // Health bar dimensions
    int healthBarWidth = 250;
    int healthBarHeight = 80;

    // Game objects
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
        paint1.setColor(Color.RED);       // Ball
        paint2.setColor(Color.BLUE);      // Paddle
        textPaint.setColor(Color.GREEN);  // Score text
        textPaint.setTextSize(60);
        textPaint.setAntiAlias(true);

        // Health bar colors
        life1.setColor(Color.RED);
        life2.setColor(Color.YELLOW);
        life3.setColor(Color.GREEN);

        // Clear screen with black background
        canvas.drawColor(Color.BLACK);

        // Update ball position
        ball.setX(ball.getX() + ball.getvX());
        ball.setY(ball.getY() + ball.getvY());

        // Ball collision with left and right walls
        if (ball.getX() - ball.getRadius() <= 0 || ball.getX() + ball.getRadius() >= getWidth()) {
            ball.setvX(-ball.getvX());
        }

        // Ball collision with top wall
        if (ball.getY() - ball.getRadius() <= 0) {
            ball.setvY(-ball.getvY());
        }

        // Ball collision with paddle
        if (ball.getY() + ball.getRadius() >= paddle.getPaddleY1()
                && ball.getX() - ball.getRadius() < paddle.getPaddleX1() + paddle.getWidth()
                && ball.getX() + ball.getRadius() > paddle.getPaddleX1()) {

            hitCounter++; // Increase score
            ball.setvY(-ball.getvY()); // Bounce the ball
        }

        // Ball missed the paddle
        else if (ball.getY() + ball.getRadius() >= paddle.getPaddleY1()) {
            life--; // Lose a life

            // Game over if no lives left
            if (life == 0) {
                // Save high score
                SharedPreferences prefs = context.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
                int highScore = prefs.getInt("highScore", 0);
                if (hitCounter > highScore) {
                    prefs.edit().putInt("highScore", hitCounter).apply();
                }

                // Navigate to GameOver activity
                Intent intent = new Intent(context, GameOver.class);
                intent.putExtra("points", hitCounter);  // Pass final score
                context.startActivity(intent);
                ((Activity) context).finish();  // Close current activity
            }

            // Reset ball position at top
            Random random = new Random();
            int number = random.nextInt(getWidth() - 10) + 10;
            ball.setX(number);
            ball.setY(50);
        }

        // Draw health bar based on remaining lives
        if (life == 3) {
            canvas.drawRect(getWidth() - healthBarWidth, 20, getWidth(), healthBarHeight + 20, life3);
        } else if (life == 2) {
            canvas.drawRect(getWidth() - (healthBarWidth * 2) / 3, 20, getWidth(), healthBarHeight + 20, life2);
        } else if (life == 1) {
            canvas.drawRect(getWidth() - healthBarWidth / 3, 20, getWidth(), healthBarHeight + 20, life1);
        }

        // Draw ball
        canvas.drawCircle(ball.getX(), ball.getY(), ball.getRadius(), paint1);

        // Draw paddle
        canvas.drawRect(paddle.getPaddleX1(), paddle.getPaddleY1(),
                paddle.getPaddleX1() + paddle.getWidth(),
                paddle.getPaddleY1() + paddle.getHeight(), paint2);

        // Draw score
        canvas.drawText("Score: " + hitCounter, 50, 100, textPaint);

        invalidate(); // Redraw the view continuously (acts as game loop)
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            // Update paddle X position based on touch
            paddle.setPaddleX1(event.getX() - paddle.getWidth() / 2);
        }

        // Clamp paddle within screen bounds
        if (paddle.getPaddleX1() < 0) {
            paddle.setPaddleX1(0);
        }
        if (paddle.getPaddleX1() + paddle.getWidth() > getWidth()) {
            paddle.setPaddleX1(getWidth() - paddle.getWidth());
        }

        return true; // Event handled
    }
}
