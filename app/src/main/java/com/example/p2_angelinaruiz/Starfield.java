package com.example.p2_angelinaruiz;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Starfield {
    private float[] points; // Coordenadas de los puntos
    private FloatBuffer pointsBuffer;
    private int rows, cols; // Cantidad de puntos en filas y columnas
    private float speed; // Velocidad de movimiento

    public Starfield(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.speed = 0.01f; // Velocidad de desplazamiento
        points = new float[rows * cols * 3]; // Cada punto tiene (x, y, z)

        // Generar posiciones iniciales en una cuadrícula
        int index = 0;
        float startY = -0.4f; // Ajusta esto según el inicio del suelo verde
        float endY = -1.0f;   // Límite inferior de la pantalla

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                float x = (float) j / (cols - 1) * 2 - 1; // x entre -1 y 1
                float y = startY + (float) i / (rows - 1) * (endY - startY); // y entre -0.4 y -1
                float z = (float) Math.random() * 2 - 1; // z entre -1 y 1

                points[index++] = x; // Coordenada X
                points[index++] = y; // Coordenada Y
                points[index++] = z; // Coordenada Z
            }
        }

        // Crear el buffer de los puntos
        ByteBuffer bb = ByteBuffer.allocateDirect(points.length * 4);
        bb.order(ByteOrder.nativeOrder());
        pointsBuffer = bb.asFloatBuffer();
        pointsBuffer.put(points);
        pointsBuffer.position(0);
    }

    public void update() {
        float startY = -0.4f; // Inicio del suelo verde
        float endY = -1.0f;   // Límite inferior

        for (int i = 0; i < points.length; i += 3) {
            points[i + 1] -= speed; // Mover hacia abajo en Y
            points[i + 2] += speed * 0.5f; // Acercar en Z

            // Aplicar perspectiva al eje X
            points[i] *= 1.01f; // Escalar ligeramente hacia el centro

            // Si el punto sale del límite inferior en Y, reubicarlo
            if (points[i + 1] < endY) {
                points[i + 1] = startY;
                points[i] = (float) Math.random() * 2 - 1; // Nueva posición X
                points[i + 2] = (float) Math.random() * -2 - 1; // Reiniciar Z lejos
            }

            // Si el punto se acerca demasiado en Z, reubicarlo al fondo
            if (points[i + 2] > 1.0f) {
                points[i + 2] = -1.0f;
            }
        }

        // Actualizar el buffer
        pointsBuffer.clear();
        pointsBuffer.put(points);
        pointsBuffer.position(0);
    }

    public void draw(GL10 gl) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, pointsBuffer);

        for (int i = 0; i < points.length; i += 3) {
            float z = points[i + 2];
            float pointSize = 3.0f * (1.5f - Math.abs(z)); // Más grande cuando Z está cerca de 0
            gl.glPointSize(Math.max(pointSize, 1.0f)); // Tamaño mínimo de 1.0
            gl.glDrawArrays(GL10.GL_POINTS, i / 3, 1);
        }

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}
