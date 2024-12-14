package com.example.p2_angelinaruiz;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class estrellesMov {
    private float[] coord; //Coordenades dels punts
    private FloatBuffer coordBuffer; //buffer de coordenades
    private int rows, cols; //Quantitat de punts
    private float speed;


    /**
     * Constructor
     * Genera les coordenades incials
     * @param rows quantitat de files
     * @param cols quantitat de columnes
     */
    public estrellesMov(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.speed = 0.01f;

        coord = new float[rows * cols * 3]; // Cada punto tiene (x, y, z)

        Random random = new Random(); //generador de punts aleatoris

        int index = 0;
        float startY = 1.0f; //inici de la pantalla
        float endY = -1.0f; //final pantalla

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                coord[index++] = random.nextFloat() * 2 - 1; // x entre -1 y 1 
                coord[index++] = random.nextFloat() * 2 -1; // y aleatorio
                coord[index++] = random.nextFloat() * -2.5f - 0.5f; // z entre -0.5 y -3.0

            }
        }

        //buffer de punts
        ByteBuffer bb = ByteBuffer.allocateDirect(coord.length * 4); //reservar memoria
        bb.order(ByteOrder.nativeOrder());
        coordBuffer = bb.asFloatBuffer();
        coordBuffer.put(coord);
        coordBuffer.position(0); //resetear pos buffer per OpneGL llegir desde inici
    }

    /**
     * Métode per actualitzar les posicions de la coordenada segons la velocitat especificada
     */
    public void update() {
        float startY = 1.0f; //posició on apareixen les estrelles
        float endY = -1.0f;  //limit de la pantalla

        Random random = new Random();

        for (int i = 0; i < coord.length; i += 3) { //+3 pq per cada punt hi ha 3 coordenades

            if (coord[i + 1] > 0) {
                coord[i + 1] += speed; //Si es sobre la nau, les estrelles cap amunt
            } else {
                coord[i + 1] -= speed; //sino cap abaix
            }

            coord[i + 2] += speed; //aproparse a camara coordenada Z (i +2)
            coord[i] *= 1.01f; //coordenada x, al multiplicar per 1,01 conseguim l'efecte d'anar-se a la vorera

            //En cas de que el punt surti de la camara (-1 en y)
            if (coord[i + 1] < endY || coord[i + 1] > startY) {
                coord[i + 1] = (random.nextFloat() * 2) - 1;; //torna al inici de y
                //coord[i+1] = random.nextFloat * 2 - 1 //pos de y aleatoria
                coord[i] = random.nextFloat() * 2 - 1; //pos X aleatoria
                coord[i + 2] = random.nextFloat() * -2.5f - 0.5f; //nova pos aleatoria a Z de -1 a -3
            }
        }


        //Actualizar el buffer
        coordBuffer.clear();
        coordBuffer.put(coord);
        coordBuffer.position(0);//reiniciar punter buffer
    }

    /**
     * S'encarrega de dibuixar els punts.
     * S'ha de tenir en compte cada punt té 3 coordenades
     * @param gl
     */
    public void draw(GL10 gl) {
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, coordBuffer); //3 components, tipo float, no espai, buffer amb les coordenades

        for (int i = 0; i < coord.length; i += 3) { //+3 pq son les 3 coordenades
            float z = coord[i + 2];
            float coordize = 3.0f / Math.abs(z); // quant + aprop - z
            gl.glPointSize(Math.min(Math.max(coordize, 1.0f), 5.0f)); //mínim 1, max 5
            gl.glDrawArrays(GL10.GL_POINTS, i / 3, 1); //i/3 pq al buffer son 3 coordenades
        }

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    public void setSpeed(Float speed){
        this.speed = speed;
    }

    public Float getSpeed(){
        return speed;
    }
}
