package com.example.p2_angelinaruiz;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;


public class Fondo {
    private FloatBuffer vertexBuffer;
    private FloatBuffer texCoordBuffer;
    private int[] textureIDs = new int[1];

    private float[] vertices = { // Vertices for a face
            -1.0f, -1.0f, 0.0f,  // 0. left-bottom-front
            1.0f, -1.0f, 0.0f,  // 1. right-bottom-front
            -1.0f,  1.0f, 0.0f,  // 2. left-top-front
            1.0f,  1.0f, 0.0f   // 3. right-top-front
    };

    float[] texCoords = { // Texture coords for the above face (NEW)
            0.0f, 1.0f,  // A. left-bottom (NEW)
            1.0f, 1.0f,  // B. right-bottom (NEW)
            0.0f, 0.0f,  // C. left-top (NEW)
            1.0f, 0.0f   // D. right-top (NEW)
    };

    public Fondo(){
        // Setup vertex-array buffer. Vertices in float. An float has 4 bytes
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder()); // Use native byte order
        vertexBuffer = vbb.asFloatBuffer(); // Convert from byte to float
        vertexBuffer.put(vertices);         // Copy data into buffer
        vertexBuffer.position(0);           // Rewind

        // Setup texture-coords-array buffer, in float. An float has 4 bytes (NEW)
        ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        texCoordBuffer = tbb.asFloatBuffer();
        texCoordBuffer.put(texCoords);
        texCoordBuffer.position(0);
    }

    public void loadTexture(GL10 gl, Context context){
        gl.glGenTextures(1, textureIDs, 0); // Generate texture-ID array

        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[0]);   // Bind to texture ID
        // Set up texture filters
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        // Construct an input stream to texture image "res\drawable\nehe.png"
        InputStream istream = context.getResources().openRawResource(R.raw.fondo1);

        Bitmap bitmap;
        try {
            // Read and decode input as bitmap
            bitmap = BitmapFactory.decodeStream(istream);
        } finally {
            try {
                istream.close();
            } catch(IOException e) { }
        }

        // Build Texture from loaded bitmap for the currently-bind texture ID
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
    }

    public void draw(GL10 gl) {
        gl.glColor4f(1,1,1,1);

        // Configurar la proyección ortográfica
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(-1, 1, -1, 1, -1, 1);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glEnable(GL10.GL_TEXTURE_2D);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordBuffer);


        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        // Deshabilitar estados
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisable(GL10.GL_TEXTURE_2D);
    }
}
