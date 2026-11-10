package com.example.primitivas2d;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Line {

    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int positionHandle, colorHandle;

    // 1. Coordenadas de los vértices que forman la línea (2 puntos: inicio y fin en 2D: X, Y).
    private final float[] lineCoord = {
            -0.5f,  0.5f, // Punto inicial (X1, Y1)
            0.5f,  0.0f  // Punto final (X2, Y2)
    };

    // Color asignado a la línea en formato RGBA (Rojo, Verde, Azul, Alfa/Opacidad).
    // Valores entre 0.0f y 1.0f (En este caso, Amarillo brillante).
    float[] color = { 1.0f, 1.0f, 0.0f, 1.0f };

    public Line() {
        // 1. Asigna memoria nativa en bytes: (número de floats en lineCoord) * 4 bytes/float.
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(lineCoord.length * 4);

        // 2. Configura el orden de lectura de los bytes según el hardware del dispositivo (Little-Endian o Big-Endian).
        byteBuffer.order(ByteOrder.nativeOrder());

        // 3. Modifica la interpretación del búfer de bytes crudos a un búfer de floats (FloatBuffer).
        vertexBuffer = byteBuffer.asFloatBuffer();

        // 4. Copia los datos del arreglo de Java (lineCoord) a la memoria nativa accesible por la GPU.
        vertexBuffer.put(lineCoord);

        // 5. Restablece el puntero de lectura del búfer a la posición inicial (índice 0).
        vertexBuffer.position(0);

        // 6. Compila los Shaders de vértices y fragmentos para el procesamiento gráfico.
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // 7. Crea el programa de OpenGL donde se enlazarán los shaders.
        mProgram = GLES20.glCreateProgram();

        // 8. Adjunta los dos shaders compilados al programa recién creado.
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        // 9. Enlaza los shaders dentro del programa para comprobar compatibilidad entre entradas y salidas.
        GLES20.glLinkProgram(mProgram);
    }

    // Método auxiliar para compilar individualmente cada tipo de Shader.
    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    // 1. VERTEX SHADER (Lenguaje GLSL): Define la posición espacial de los vértices de la línea.
    private final String vertexShaderCode =
            "" ;

    // 2. FRAGMENT SHADER (Lenguaje GLSL): Define la apariencia visual y el color de los píxeles de la línea.
    private final String fragmentShaderCode =
            "" ;


    // Método encargado de renderizar o dibujar la línea en pantalla.
    public void draw() {
        // Selecciona el programa de shaders actual.
        GLES20.glUseProgram(mProgram);

        // Obtiene la referencia del atributo "vPosition" declarado en el Vertex Shader.
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Habilita el arreglo de atributos de vértices.
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Especifica los datos de los vértices (asociando el búfer de coordenadas).
        GLES20.glVertexAttribPointer(
                positionHandle,
                2,                  // Tamaño: 2 coordenadas por vértice (X e Y).
                GLES20.GL_FLOAT,    // Tipo de dato de los vértices.
                false,              // Normalización de datos (falso para coordenadas estándar).
                0,                  // Stride: 0 porque los vértices están almacenados consecutivamente sin saltos.
                vertexBuffer        // Búfer con los datos de los puntos de la línea.
        );

        // Obtiene la referencia de la variable uniforme "vColor" en el Fragment Shader.
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Envía el color asignado desde Java hacia la GPU.
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // Dibuja la línea utilizando primitivas de OpenGL (GL_LINES conecta pares de puntos).
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, lineCoord.length / 2);

        // Deshabilita el arreglo de atributos tras terminar el dibujo por buenas prácticas.
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}