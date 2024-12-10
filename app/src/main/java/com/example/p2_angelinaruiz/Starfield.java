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

        Random random = new Random(); // Generador de números aleatorios

        int index = 0;
        float startY = 1.0f; // Ajusta esto según el inicio del suelo verde
        float endY = -1.0f;   // Límite inferior de la pantalla

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                points[index++] = random.nextFloat() * 2 - 1; // x entre -1 y 1 (aleatorio)
                points[index++] = startY + random.nextFloat() * (endY - startY); // y aleatorio entre -0.4 y -1
                points[index++] = random.nextFloat() * -2 - 1; // z entre -1 y -3 (aleatorio)
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
        float startY = 1.0f; // Inicio del suelo verde
        float endY = -1.0f;   // Límite inferior

        Random random = new Random();

        for (int i = 0; i < points.length; i += 3) {
            points[i + 1] -= speed; // Mover hacia abajo en Y
            points[i + 2] += speed * 0.5f; // Acercar en Z

            // Aplicar perspectiva al eje X
            points[i] *= 1.01f; // Escalar ligeramente hacia el centro

            // Si el punto sale del límite inferior en Y, reubicarlo
            if (points[i + 1] < endY) {
                points[i + 1] = startY;
                points[i] = random.nextFloat() * 2 - 1; // Nueva posición X aleatoria
                points[i + 2] = random.nextFloat() * -2 - 1; // Nueva posición Z aleatoria
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
