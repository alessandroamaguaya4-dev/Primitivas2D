package com.example.primitivas2d;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {

    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int positionHandle, colorHandle;
    static final int coords_per_vertex = 3;

    // Coordenadas corregidas para formar dos triángulos simétricos (un rombo o reloj de arena)
    static final float[] triangleCoords = {
            // --- TRIÁNGULO 1 (Inferior) ---
            -0.3f,  0.0f, 0.0f,  // Vértice 0: Izquierda
            0.3f,  0.0f, 0.0f,  // Vértice 1: Derecha
            0.0f, 0.6f, 0.0f,  // Vértice 2: Abajo (Punta)

            // --- TRIÁNGULO 2 (Superior) ---
            -0.5f,  -0.25f, 0.0f,  // Vértice 0: Izquierda
            0.5f,  -0.25f, 0.0f,  // Vértice 1: Derecha
            0.0f, -0.4f, 0.0f,  // Vértice 5: Arriba (Punta)
    };

    // Calcula automáticamente los vértices: 18 elementos / 3 = 6 vértices en total.
    private final int vertexCount = triangleCoords.length / coords_per_vertex;

    // Color rojo oscuro
    float[] color = { 0.8f, 0.1f, 0.1f, 1.0f };

    public Triangle(){
        // 1. Reservamos memoria nativa (Multiplicado por 4 porque un float pesa 4 bytes)
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);

        // 2. Carga de Shaders
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
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
                coords_per_vertex,                  // 3 coordenadas por vértice (X, Y, Z)
                GLES20.GL_FLOAT,
                false,
                0,
                vertexBuffer
        );

        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // ¡CORRECCIÓN AQUÍ!
        // Como todos tus triángulos están ordenados secuencialmente en el mismo búfer (los 6 vértices),
        // puedes dibujarlos de un solo tirón indicando que comience en el índice 0 y dibuje los 6 vértices.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}