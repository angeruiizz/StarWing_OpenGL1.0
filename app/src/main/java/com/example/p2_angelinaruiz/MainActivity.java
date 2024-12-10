package com.example.p2_angelinaruiz;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private MyGLSurfaceView glSurfaceView;  // Superficie de renderizado personalizada

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Crear el GLSurfaceView personalizado
        glSurfaceView = new MyGLSurfaceView(this);

        setContentView(glSurfaceView);  // Configurar MyGLSurfaceView como contenido principal
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
