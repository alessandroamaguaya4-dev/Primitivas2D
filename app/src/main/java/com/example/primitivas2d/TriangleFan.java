package com.example.primitivas2d;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class TriangleFan {
    private FloatBuffer vertexBuffer;
    private int program;
    private int positionHandle;
    private final float[] coords={
            0.0f,0.0f,
            0.f,0.4f,
            -0.2f, 0.3f,
            -0.4f,0.1f,
            -0.5f,0.0f
           /* -0.2f,-0.3f,
            0.0f,-0.4f,
            0.2f,-0.3f,*/

    };
    public TriangleFan(){
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(coords.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(coords);
        vertexBuffer.position(0);

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
                    "  gl_FragColor = vec4(1.0,1.0,0.0,1.0);" +
                    "}";


    public void draw(){
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



        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, coords.length/2);

        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
