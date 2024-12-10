package com.example.p2_angelinaruiz;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;


public class MyOpenGLRenderer implements Renderer{
    private Context context;
    private float[] vertices;
    private Fondo fondo;
    private int height;
    private int width;
    private FloatBuffer vertexBuffer;
    private FloatBuffer texCoordBuffer;
    private Starfield starfield;

    public MyOpenGLRenderer(Context context){
        this.context = context;
        this.fondo  = new Fondo();
        this.starfield = new Starfield( 100,20); //puntos por línea
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Image Background color
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);


        fondo = new Fondo();
        fondo.loadTexture(gl, context);

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
        this.width = width;
        this.height = height;

        // Configurar el viewport
        gl.glViewport(0, 0, width, height);

        // Configurar la proyección
        setPerspectiveProjection(gl);
    }


    @Override
    public void onDrawFrame(GL10 gl10) {
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl10.glEnable(GL10.GL_TEXTURE_2D);
        fondo.draw(gl10);
        gl10.glDisable(GL10.GL_TEXTURE_2D);

        starfield.update();
        starfield.draw(gl10);

    }

    private void updateVerticesForAspectRatio(int screenWidth, int screenHeight) {
        // Calcular la relación de aspecto
        float aspectRatio = (float) screenWidth / screenHeight;

        // Actualizar las coordenadas de los vértices
        vertices = new float[] {
                -aspectRatio, -1.0f, 0.0f,  // Inferior izquierdo
                aspectRatio, -1.0f, 0.0f,  // Inferior derecho
                -aspectRatio,  1.0f, 0.0f,  // Superior izquierdo
                aspectRatio,  1.0f, 0.0f   // Superior derecho
        };

        // Actualizar el buffer de vértices
        ByteBuffer vb = ByteBuffer.allocateDirect(vertices.length * 4);
        vb.order(ByteOrder.nativeOrder());
        vertexBuffer = vb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
    }



}

