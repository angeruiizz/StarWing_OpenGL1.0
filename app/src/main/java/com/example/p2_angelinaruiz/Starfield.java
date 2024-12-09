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
        this.speed = 0.1f; // Velocidad de desplazamiento
        points = new float[rows * cols * 3]; // Cada punto tiene (x, y, z)

        int index = 0;
        float startZ = -2.0f; // Punto más lejano
        float endZ = 0.0f;    // Punto más cercano
        float zStep = (endZ - startZ) / (rows - 1); // Distancia entre líneas en Z

        for (int i = 0; i < rows; i++) {
            float z = startZ + i * zStep; // Coordenada Z para esta línea
            for (int j = 0; j < cols; j++) {
                points[index++] = (float) j / (cols - 1) * 2 - 1; // x entre -1 y 1
                points[index++] = 0.0f;                           // y constante (puedes variar si quieres)
                points[index++] = z;                              // z calculada
            }
        }

        // Crear el buffer de los puntos
        ByteBuffer bb = ByteBuffer.allocateDirect(points.length * 4);
        bb.order(ByteOrder.nativeOrder());
        pointsBuffer = bb.asFloatBuffer();
        pointsBuffer.put(points);
        pointsBuffer.position(0);
    }

    // Actualizar posiciones de los puntos para simular movimiento
    public void update() {
        for (int i = 2; i < points.length; i += 3) { // Solo modificar la coordenada Z
            points[i] += speed; // Mover hacia la cámara

            // Si el punto se acerca demasiado, reiniciarlo al fondo
            if (points[i] > 1.0f) {
                points[i] = -2.0f; // Reiniciar al fondo de la escena
            }
        }

        // Actualizar el buffer
        pointsBuffer.clear();
        pointsBuffer.put(points);
        pointsBuffer.position(0);
    }

    // Dibujar los puntos en pantalla
    public void draw(GL10 gl) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, pointsBuffer);

        for (int i = 0; i < points.length; i += 3) { // Iterar sobre cada punto
            float z = points[i + 2]; // Coordenada Z
            float size = Math.max(1.0f, 10.0f * (1.0f - z / -2.0f)); // Tamaño inverso a la distancia, ajusta los factores según convenga

            gl.glPointSize(size); // Ajustar el tamaño del punto
            gl.glDrawArrays(GL10.GL_POINTS, i / 3, 1); // Dibujar un solo punto
        }

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }
}