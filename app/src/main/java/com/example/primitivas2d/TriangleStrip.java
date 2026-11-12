package com.example.primitivas2d;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class TriangleStrip {
    private FloatBuffer vertexBuffer;
    private int program;
    private int positionHandle;

    private final float[] coordsTriangleStrip={
            -0.8f,-0.2f,
            -0.4f, -0.2f,
            -0.8f,0.2f,
            -0.4f,0.2f,
            -0.4f,0.6f
    };
    public TriangleStrip(){
        // 1. Reservamos memoria nativa (Multiplicado por 4 porque un float pesa 4 bytes)
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(coordsTriangleStrip.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(coordsTriangleStrip);
        vertexBuffer.position(0);

        // 2. Carga de Shaders
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
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
                    "  gl_FragColor = vec4(1.0,0.0,0.0,1.0);" +
                    "}";
    public void draw() {
        GLES20.glUseProgram(program);

        positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);

        GLES20.glVertexAttribPointer(
                positionHandle,
                2,                  // 3 coordenadas por vértice (X, Y, Z)
                GLES20.GL_FLOAT,
                false,
                0,
                vertexBuffer
        );


        // ¡CORRECCIÓN AQUÍ!
        // Como todos tus triángulos están ordenados secuencialmente en el mismo búfer (los 6 vértices),
        // puedes dibujarlos de un solo tirón indicando que comience en el índice 0 y dibuje los 6 vértices.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 5);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}