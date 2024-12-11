package com.example.p2_angelinaruiz;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {
    private final MyOpenGLRenderer renderer;
    private float previousX;
    private float previousY;

    public MyGLSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(1);
        renderer = new MyOpenGLRenderer(context);
        setRenderer(renderer);

        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX(); //capturar x e y
        float y = event.getY();

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = x - previousX;
            float dy = y - previousY;

            float scaleFactor = 0.005f; //per la sensibilitat
            renderer.getNave().move(dx * scaleFactor, -dy * scaleFactor); // Mover la nave
            requestRender();
        }

        previousX = x; //las nuevas pasan a ser las antiguas
        previousY = y;
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        float moveDistance = 0.2f; //distancia que es mou

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP: //dalt
                renderer.getNave().move(0, moveDistance);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN: //baix
                renderer.getNave().move(0, -moveDistance);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT: //esq
                renderer.getNave().move(-moveDistance, 0);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT: //drta
                renderer.getNave().move(moveDistance, 0);
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        requestRender();
        return true;
    }
}
