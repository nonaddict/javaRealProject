package com.example.tryhard;

public class Paddle {
    float paddleX1;
    float paddleY1;
    float width;
    float height;
    public Paddle(float paddleX1, float paddleY1,float width,float height){
       this.paddleX1=paddleX1;
       this.paddleY1=paddleY1;
       this.width=width;
       this.height=height;
    }

    public float getPaddleX1() {
        return paddleX1;
    }

    public void setPaddleX1(float paddleX1) {
        this.paddleX1 = paddleX1;
    }

    public float getPaddleY1() {
        return paddleY1;
    }

    public void setPaddleY1(float paddleY2) {
        this.paddleY1 = paddleY2;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
