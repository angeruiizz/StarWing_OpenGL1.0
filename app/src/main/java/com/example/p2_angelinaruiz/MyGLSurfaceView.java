package com.example.p2_angelinaruiz;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {
    private final MyOpenGLRenderer renderer;  // Renderizador personalizado
    private float previousX;
    private float previousY;

    public MyGLSurfaceView(Context context) {
        super(context);

        // Configurar OpenGL ES
        setEGLContextClientVersion(1);
        renderer = new MyOpenGLRenderer(context);
        setRenderer(renderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = x - previousX;
            float dy = y - previousY;

            float scaleFactor = 0.01f; // Ajustar la sensibilidad
            renderer.getNave().move(dx * scaleFactor, -dy * scaleFactor); // Mover la nave
            requestRender(); // Redibujar la escena
        }

        previousX = x;
        previousY = y; // Asegúrate de actualizar las coordenadas previas
        return true;
    }
}
