package com.example.p2_angelinaruiz;

import javax.microedition.khronos.opengles.GL10;

public class Nau3D {
    private float x = 0.0f; // Posición X
    private float y = 0.0f; // Posición Y
    private float z = -1.5f; // Posición Z inicial
    private final LoadObject3D model; // Modelo de la nave
    private float rotationAngle = 0.0f; // Ángulo de rotación

    public Nau3D(LoadObject3D model) {
        this.model = model;
    }

    public void move(float dx, float dy) {
        float maxX = 1.0f; //limit dret
        float minX = -1.0f; //limt esquerra
        float maxY = 1.0f; //limit superior
        float minY = -1.0f; //limit inferior

        this.x = Math.max(minX, Math.min(maxX, this.x + dx));
        this.y = Math.max(minY, Math.min(maxY, this.y + dy));
        this.x += dx;
        this.y += dy;
        System.out.println("Nueva posición de la nave: X=" + x + ", Y=" + y);
    }

    public void draw(GL10 gl) {
        System.out.println("Dibujando en: X=" + x + ", Y=" + y + ", Z=" + z);

        gl.glPushMatrix();
        gl.glScalef(0.5f, 0.5f, 0.5f);
        gl.glTranslatef(x, y, z);
        model.draw(gl); //fem servir el de la clase loadObject3D
        gl.glPopMatrix();
    }

    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getZ() { return z; }
}