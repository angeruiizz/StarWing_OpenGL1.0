package com.example.p2_angelinaruiz;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private GLSurfaceView glSurfaceView;  // Superficie de renderizado OpenGL
    private MyOpenGLRenderer myGLRenderer;  // Renderizador personalizado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Crear un GLSurfaceView y configurarlo
        glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setEGLContextClientVersion(1);  // Usar OpenGL ES 1.0
        myGLRenderer = new MyOpenGLRenderer(this);  // Instanciar el renderizador
        glSurfaceView.setRenderer(myGLRenderer);  // Asignar renderizador al GLSurfaceView

        setContentView(glSurfaceView);  // Configurar GLSurfaceView como contenido principal
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();  // Reanudar renderizado OpenGL
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();  // Pausar renderizado OpenGL
    }
}
