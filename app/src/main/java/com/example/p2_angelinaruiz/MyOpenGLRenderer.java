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
    private BoostHUD barraBoost;

    private int height;
    private int width;
    private float  zCam = -3.0f, xCam = 0.0f, yCam = 0.0f; //Posició camara
    private float xTarget = 0.0f, yTarget = 0.0f; // Punto hacia el que mira la cámara


    private boolean boosterActive = false;
    private long boosterEndTime = 0;
    private float originalSpeed; // Para restaurar la velocidad original
    private float originalScale = 0.5f; // Tamaño original de la nave
    private float targetSpeed; // Para la interpolación de la velocidad
    private float targetScale; // Para la interpolación del tamaño de la nave
    private boolean restoringBooster = false; // Para indicar si estamos restaurando los valores originales
    private long boosterCooldownEndTime = 0; // Momento en que el cooldown del booster termina
    private static final long BOOSTER_COOLDOWN_TIME = 5000; // 5 segundos de cooldown

    public MyOpenGLRenderer(Context context) {
        this.context = context;
        this.fondo = new Fondo();
        LoadObject3D modelNau = new LoadObject3D(context, R.raw.nau);
        this.nau3D = new Nau3D(modelNau);

        this.estrellesMov = new estrellesMov(50, 20); // puntos por línea
        barraBoost = new BoostHUD(-0.9f, 0.9f, 1.8f, 0.05f);

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
        updateBooster();
        // Limpiar buffers
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // Configurar perspectiva
        setPerspectiveProjection(gl10);
        gl10.glEnable(GL10.GL_DEPTH_TEST);

        actualizarCamara();
        shakeCam(0.025f);
        GLU.gluLookAt(gl10,
                xCam, yCam, zCam,       // Posición de la cámara
                xTarget, yTarget, 0.0f, // Punto al que mira
                0.0f, 1.0f, 0.0f        // Vector "arriba"
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
        barraBoost.draw(gl10);
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
    private void actualizarCamara() {
        float naveX = nau3D.getX();
        float naveY = nau3D.getY();

        xCam = interpolate(xCam, -naveX * 0.2f, 0.1f); // Movimiento suave
        yCam = interpolate(yCam, naveY * 0.1f, 0.1f);

        xTarget = interpolate(xTarget, naveX * 0.1f, 0.1f);
        yTarget = interpolate(yTarget, naveY * 0.1f, 0.1f);
    }

    private float interpolate(float current, float target, float alpha) {
        return current + (target - current) * alpha;
    }


    private void shakeCam(float shakeIntensity) {
        if (shakeIntensity > 0) {
            xCam += (Math.random() - 0.5f) * shakeIntensity;
            yCam += (Math.random() - 0.5f) * shakeIntensity;
            shakeIntensity *= 0.3f;
        }
    }

    public void activateBooster() {
        long currentTime = System.currentTimeMillis();

        if (!boosterActive && !restoringBooster && currentTime >= boosterCooldownEndTime) {
            boosterActive = true;
            boosterEndTime = currentTime + 3000; // 3 segundos de duración del booster

            // Guarda la velocidad original y el tamaño
            originalSpeed = estrellesMov.getSpeed();
            originalScale = nau3D.getScale();

            // Establecer velocidad y tamaño objetivo
            targetSpeed = originalSpeed * 4.0f;
            targetScale = originalScale * 0.75f;
        } else if (currentTime < boosterCooldownEndTime) {
            System.out.println("El booster está en cooldown.");
        }
    }

    // Actualiza progresivamente la velocidad y el tamaño
    private void updateBooster() {
        long currentTime = System.currentTimeMillis();

        if (boosterActive) {
            // Calcula el tiempo restante para el boost activo
            float remainingTime = boosterEndTime - currentTime;

            if (remainingTime > 0) {
                // Progreso del boost activo
                float progress = remainingTime / 3000.0f; // Escalar a [0, 1]
                barraBoost.setProgress(progress); // Actualizar la barra

                // Interpolación de velocidad y tamaño
                float speedAlpha = 1.0f - progress; // Progreso inverso
                estrellesMov.setSpeed(interpolate(originalSpeed, targetSpeed, speedAlpha));
                nau3D.setScale(interpolate(originalScale, targetScale, speedAlpha));
            } else {
                // El boost activo termina
                boosterActive = false;
                restoringBooster = true; // Inicia la restauración
                boosterEndTime = currentTime + 3000; // Tiempo para restaurar
            }
        }

        if (restoringBooster) {
            // Calcula el tiempo restante para la restauración
            float restoreTime = boosterEndTime - currentTime;

            if (restoreTime > 0) {
                // Progreso de la restauración
                float restoreAlpha = 1.0f - (restoreTime / 3000.0f); // Escalar a [0, 1]
                barraBoost.setProgress(0.0f); // La barra queda vacía mientras restaura
                estrellesMov.setSpeed(interpolate(targetSpeed, originalSpeed, restoreAlpha));
                nau3D.setScale(interpolate(targetScale, originalScale, restoreAlpha));
            } else {
                // Restauración completa
                restoringBooster = false; // Finaliza la restauración
                boosterCooldownEndTime = currentTime + BOOSTER_COOLDOWN_TIME; // Cooldown de 5 segundos
            }
        }

        if (!boosterActive && !restoringBooster && currentTime < boosterCooldownEndTime) {
            // Aquí podrías manejar el estado de cooldown si necesitas mostrarlo en el HUD
            barraBoost.setProgress(0.0f); // Asegura que la barra esté vacía en cooldown
        }
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
