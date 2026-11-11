package com.example.primitivas2d;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Line {

    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int positionHandle, colorHandle;

    // Arreglo con todas las coordenadas de las figuras ordenadas por secciones:
    private final float[] lineCoord = {

         /*   -0.6f,  -0.3f,
            -0.4f,   0.3f,
            -0.2f,  -0.3f,
            0.0f,   0.3f,
            0.2f,  -0.3f,
            0.4f,   0.3f,
            0.6f,  -0.3f,

            -0.25f, -0.4f,
            0.25f, -0.4f,
            0.5f,  -0.6f,
            0.25f, -0.8f,
            -0.25f, -0.8f,
            -0.5f,  -0.6f,



            0.0f,   0.5f,
            0.0f,   0.9f,

            -0.5f,   0.7f,
            0.5f,   0.7f*/
        //K
            -0.4f, 0.2f,
            -0.4f, -0.2f,

            -0.4f, 0.0f,
            -0.1f, 0.2f,

            -0.4f, 0.0f,
            -0.1f, -0.2f,

          //corazon
            -0.8f,0.0f,
            -0.4f,0.6f,
            0.0f,0.3f,
            0.4f, 0.6f,
            0.8f, 0.0f,
            0.0f, -0.8f,

            //a
            0.1f,-0.2f,
            0.3f,0.2f,
            0.5f,-0.2f,

            //
            0.2f, 0.0f,
            0.4f,0.0f






    };

    // Color verde brillante en formato RGBA
    float[] color = { 0.1f, 0.0f, 0.0f, 1.0f };

    public Line() {
        // CORRECCIÓN: Se agregó el asterisco (*) faltante en la multiplicación del búfer
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(lineCoord.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(lineCoord);
        vertexBuffer.position(0);

        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";

    public void draw() {
        GLES20.glUseProgram(mProgram);

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(
                positionHandle,
                2,                  // 2 coordenadas por vértice (X, Y)
                GLES20.GL_FLOAT,
                false,
                0,
                vertexBuffer
        );

        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // Establecer el grosor de las líneas
        GLES20.glLineWidth(5.0f);

        // 1. Dibuja el ZIGZAG: Inicia en el índice 0 y toma 7 vértices consecutivos con líneas conectadas.
        //GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, 7);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 6);
        // 2. Dibuja el HEXÁGONO: Inicia en el índice 7, toma 6 vértices y cierra el ciclo automáticamente con LINE_LOOP.
       GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 6, 6);
       GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 12, 3);
        GLES20.glDrawArrays(GLES20.GL_LINES, 15, 2);
        // 3. Dibuja la CRUZ: Inicia en el índice 13, toma 4 vértices para formar 2 líneas independientes con LINES.


        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}