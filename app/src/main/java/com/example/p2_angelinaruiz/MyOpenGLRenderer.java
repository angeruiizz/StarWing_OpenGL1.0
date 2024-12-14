package com.example.p2_angelinaruiz;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;

import java.nio.FloatBuffer;

public class MyOpenGLRenderer implements Renderer {
    private FloatBuffer vertexBuffer;
    private FloatBuffer texCoordBuffer;
    private Context context;

    private Fondo fondo;
    private estrellesMov estrellesMov;
    private LoadObject3D loadObject3D;
    private Nau3D nau3D;
    private Light light;

    private int height;
    private int width;
    private float zCam = -3.0f; // Posición de la cámara en Z

    public MyOpenGLRenderer(Context context) {
        this.context = context;
        this.fondo = new Fondo();
        this.estrellesMov = new estrellesMov(50, 20); // puntos por línea

        LoadObject3D modelNau = new LoadObject3D(context, R.raw.nau);
        this.nau3D = new Nau3D(modelNau);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Color de fondo
        gl.glEnable(GL10.GL_LIGHTING); // Habilitar iluminación
        gl.glEnable(GL10.GL_NORMALIZE);

        light = new Light(gl, GL10.GL_LIGHT0);
        light.setAmbientColor(new float[]{0.6f, 0.6f, 0.6f, 1.0f}); // Más luz ambiental
        light.setDiffuseColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f}); // Color difuso

        // Posicionar la luz más arriba (por ejemplo, y=2.0f)
        light.setPosition(new float[]{0.0f, 2.0f, 1.0f, 1.0f}); // Arriba y un poco hacia adelante

        fondo.loadTexture(gl, context); // Cargar texturas
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        gl.glViewport(0, 0, width, height);
        setPerspectiveProjection(gl);

        // Actualizar los vértices del fondo para que se ajusten a la pantalla
        fondo.updateVertices(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {

        // Limpiar buffers
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // Configurar perspectiva
        setPerspectiveProjection(gl10);
        gl10.glEnable(GL10.GL_DEPTH_TEST);

        GLU.gluLookAt(gl10,
                0.0f, 0.0f, zCam, // Posición de la cámara
                0.0f, 0.0f, 0.0f, // Punto al que mira
                0.0f, 1.0f, 0.0f  // Vector "arriba"
        );


        light.setPosition(new float[]{0.0f, 2.0f, 1.0f, 1.0f}); // Luz desde arriba

        // Dibujar fondo
        gl10.glPushMatrix();
        fondo.draw(gl10);
        gl10.glPopMatrix();

        // Dibujar nave
        gl10.glPushMatrix();
        nau3D.draw(gl10);
        gl10.glPopMatrix();

        // Dibujar elementos 2D
        setOrthographicProjection(gl10);
        estrellesMov.update();
        estrellesMov.draw(gl10);
    }

    private void setPerspectiveProjection(GL10 gl) {
        gl.glClearDepthf(2.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();


        GLU.gluPerspective(gl, 45, (float) width / height, 0.1f, 5.f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    private void setOrthographicProjection(GL10 gl) {
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(-1, 1, -1, 1, -1, 1);
        gl.glDepthMask(true);
        gl.glDisable(GL10.GL_DEPTH_TEST);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }


    public Nau3D getNave() {
        return nau3D;
    }

    public void setzCam(float zCam) {
        this.zCam = zCam;
    }

    public float getzCam() {
        return zCam;
    }
}
