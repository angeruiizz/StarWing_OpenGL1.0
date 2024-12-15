package com.example.p2_angelinaruiz;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class BoostHUD {
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private float[] vertices;
    private float[] colors;

    private float progress;
    private long restoreStartTime; // Almacena el tiempo de inicio para restaurar
    private boolean restoring;     // Bandera para activar la restauración
    private boolean canRestore;    // Nueva bandera para evitar ciclos de restauración

    public BoostHUD(float x, float y, float width, float height) {
        this.progress = 1.0f;
        this.restoring = false;
        this.canRestore = true; // La barra puede restaurarse al inicio

        // Definir los vértices de la barra
        vertices = new float[]{
                x, y, 0.0f,
                x + width, y, 0.0f,
                x, y - height, 0.0f,
                x + width, y - height, 0.0f,
        };

        // Asignar el buffer con orden nativo
        ByteBuffer bb = ByteBuffer.allocateDirect(vertices.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // Colores (fondo gris claro)
        colors = new float[]{
                0.5f, 0.5f, 0.5f, 1.0f,
                0.5f, 0.5f, 0.5f, 1.0f,
                0.5f, 0.5f, 0.5f, 1.0f,
                0.5f, 0.5f, 0.5f, 1.0f,
        };

        ByteBuffer cb = ByteBuffer.allocateDirect(colors.length * 4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(colors);
        colorBuffer.position(0);
    }

    public void setProgress(float progress) {
        if (canRestore) { // Solo se activa si es posible restaurar
            this.progress = Math.max(0.0f, Math.min(progress, 1.0f));

            if (this.progress == 0.0f && !restoring) {
                restoring = true;
                restoreStartTime = System.currentTimeMillis();
                canRestore = false; // Evita reiniciar repetidamente
            }
        }
    }

    private void updateProgress() {
        if (restoring) {
            long currentTime = System.currentTimeMillis();
            float elapsedTime = (currentTime - restoreStartTime) / 1000.0f; // Segundos

            if (elapsedTime >= 5.0f) {
                // Restauración completa
                progress = 1.0f;
                restoring = false;
                canRestore = true; // Permitir futuros boosts
            } else {
                // Restaurar progresivamente
                progress = elapsedTime / 5.0f;
            }
        }
    }

    // Método opcional para permitir la restauración manual de la barra
    public void resetRestoration() {
        canRestore = true;
    }

    public void draw(GL10 gl) {
        updateProgress(); // Actualizar el progreso si está restaurando

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        // Dibujar la barra de progreso (sección roja)
        if (progress > 0) {
            float[] progressVertices = new float[]{
                    vertices[0], vertices[1], vertices[2],
                    vertices[0] + (vertices[3] - vertices[0]) * progress, vertices[1], vertices[2],
                    vertices[6], vertices[7], vertices[8],
                    vertices[6] + (vertices[3] - vertices[0]) * progress, vertices[7], vertices[8],
            };

            // Actualizar el buffer de progreso
            ByteBuffer pb = ByteBuffer.allocateDirect(progressVertices.length * 4);
            pb.order(ByteOrder.nativeOrder());
            FloatBuffer progressBuffer = pb.asFloatBuffer();
            progressBuffer.put(progressVertices);
            progressBuffer.position(0);

            gl.glColor4f(1.0f, 0.0f, 0.0f, 1.0f); // Rojo
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, progressBuffer);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, progressVertices.length / 3);
        }

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}