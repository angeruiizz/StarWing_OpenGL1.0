package com.example.p2_angelinaruiz;

import javax.microedition.khronos.opengles.GL10;

public class Nau3D {
    private float x = 0.0f; // Posición X
    private float y = 0.0f; // Posición Y
    private float z = -3.0f; // Posición Z inicial
    private final LoadObject3D model; // Modelo de la nave
    private float rotationX = 0.0f; // Rotación en el eje X (inclinación vertical)
    private float rotationY = 0.0f; // Rotación en el eje Y (inclinación horizontal)
    private final float rotationFactor = 90.0f; // Factor de rotación para simular el vuelo
    private Light lightLanterna;
    private float scale = 0.5f;


    public Nau3D(LoadObject3D model) {
        this.model = model;
    }

    public void move(float dx, float dy) {
        float maxX = 2.3f; //limit dret
        float minX = -2.3f; //limt esquerra
        float maxY = 1.5f; //limit superior
        float minY = -1.5f; //limit inferior

        this.x = Math.max(minX, Math.min(maxX, this.x + dx));
        this.y = Math.max(minY, Math.min(maxY, this.y + dy));

        //System.out.println("Nueva posición de la nave: X=" + x + ", Y=" + y);

    }

    public void draw(GL10 gl) {
        //System.out.println("Dibujando en: X=" + x + ", Y=" + y + ", Z=" + z);

        gl.glPushMatrix();
        gl.glScalef(scale, scale, scale);
        gl.glTranslatef(x, y, z);
        gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f); //girar 180º per que quedi el cul de la nau a darrera

        model.draw(gl); //fem servir el de la clase loadObject3D
        gl.glPopMatrix();
    }

    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public float getZ() {
        return z;
    }
    public float getScale(){
        return scale;
    }
    public void setScale(float scale){
        this.scale = scale;
    }
}