package com.example.p2_angelinaruiz;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


public class MyOpenGLRenderer implements Renderer {
    private FloatBuffer vertexBuffer;
    private FloatBuffer texCoordBuffer;
    private Context context;
    private float[] vertices;

    private Fondo fondo;
    private estrellesMov estrellesMov;
    private LoadObject3D loadObject3D;
    private Nau3D nau3D;

    private int height;
    private int width;
    private int angle;


    public MyOpenGLRenderer(Context context){
        this.context = context;
        this.fondo  = new Fondo();
        this.estrellesMov = new estrellesMov( 50,20); //puntos por línea

        LoadObject3D modelNau = new LoadObject3D(context, R.raw.nau);
        this.nau3D = new Nau3D(modelNau);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); //color de fons


        gl.glEnable(GL10.GL_LIGHTING); //hablitar la llum
        gl.glEnable(GL10.GL_LIGHT0); //font de llum 0 (global)

        float[] ambientLight = { 0.1f, 0.1f, 0.3f, 1.0f }; // Color RGBA, ajusta intensitat y color, representa blau fosc
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, ambientLight, 0);

        fondo.loadTexture(gl, context); // Cargar texturas

    }

    private void setOrthographicProjection(GL10 gl){
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(-1, 1, -1, 1, -1, 1); //Coordenadas estandard de OpenGl
        gl.glDepthMask(false);  // disable writes to Z-Buffer
        gl.glDisable(GL10.GL_DEPTH_TEST);  // disable depth-testing

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    private void setPerspectiveProjection(GL10 gl) {
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float) width / height, 1.0f, 10.0f); // Configuración de perspectiva
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float) width / height, 1.0f, 10.0f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl10.glEnable(GL10.GL_TEXTURE_2D);
        fondo.draw(gl10);
        gl10.glDisable(GL10.GL_TEXTURE_2D);

        //llum puntual
        gl10.glEnable(GL10.GL_LIGHT1); // Fuente de luz 1
        float[] diffuseLight = { 1.0f, 1.0f, 1.0f, 1.0f }; // Luz difusa (color blanco)
        gl10.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, diffuseLight, 0);

        //per a que segueixi la nau
        float[] lightPosition = { nau3D.getX(), nau3D.getY(), nau3D.getZ(), 1.0f };
        gl10.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, lightPosition, 0);

        gl10.glPushMatrix();// Reset model-view matrix ( NEW )
        nau3D.draw(gl10);
        gl10.glPopMatrix();


        estrellesMov.update();
        estrellesMov.draw(gl10);
    }

    public Nau3D getNave() {
        return nau3D;
    }


}

