package com.example.p2_angelinaruiz;

import javax.microedition.khronos.opengles.GL10;

public class Nau3D {
    private float x = 0.0f; // Posición X
    private float y = 0.0f; // Posición Y
    private float z = -1.5f; // Posición Z inicial
    private final LoadObject3D model; // Modelo de la nave

    public Nau3D(LoadObject3D model) {
        this.model = model;
    }

    public void move(float dx, float dy) {
        this.x += dx;
        this.y += dy;
        System.out.println("Nueva posición de la nave: X=" + x + ", Y=" + y);
    }

    public void draw(GL10 gl) {
        System.out.println("Dibujando en: X=" + x + ", Y=" + y + ", Z=" + z);
        gl.glPushMatrix(); // Guardar la matriz actual
        gl.glScalef(0.5f, 0.5f, 0.5f); // Ajusta el tamaño del modelo si es necesario
        gl.glTranslatef(x, y, z); // Mover a la posición de la nave
        model.draw(gl); //fem servir el de la clase loadObject3D
        gl.glPopMatrix(); // Restaurar la matriz original
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