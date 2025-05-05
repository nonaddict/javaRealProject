package com.example.tryhard;

public class Ball {
    float x;
    float y;
    float radius;
    float vX;
    float vY;

    public Ball(float x,float y,float radius,float vX,float vY){
        this.x=x;
        this.y=y;
        this.radius=radius;
        this.vX=vX;
        this.vY=vY;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getvX() {
        return vX;
    }

    public void setvX(float vX) {
        this.vX = vX;
    }

    public float getvY() {
        return vY;
    }

    public void setvY(float vY) {
        this.vY = vY;
    }
}
