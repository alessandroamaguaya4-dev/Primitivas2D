package com.example.primitivas2d;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Point {
    private final FloatBuffer vertexBuffer;//sirve para recibir las coordenadas de los vertices, para que se procesen eficientemente
    private final int mProgram; //sirve para combinar el vertx y fragment shaders
    private int positionHandle, colorHandle;
    static final int coords_per_vertex = 3; // 3 coordenadas espaciales (X, Y, Z)
    float[] pointCoords = { 0.25f, -0.25f, 0.0f }; // Coordenadas cartesianas del punto
    private final int vertexCount = pointCoords.length / coords_per_vertex; // Total de vértices (1 punto)
    private final int vertexStride = coords_per_vertex * 4; // Zancada: 3 coordenadas * 4 bytes = 12 bytes por vértice
    float[] color = { 1.0f, 0.75f, 0.25f, 1.0f }; // Color RGBA (naranja/amarillo opaco)
    public Point() {
        // 1. Reserva memoria nativa directa fuera del recolector de basura de la JVM (3 floats * 4 bytes = 12 bytes).
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(pointCoords.length * 4);

        // 2. Configura el orden de bytes nativo del procesador (Little-Endian o Big-Endian).
        byteBuffer.order(ByteOrder.nativeOrder());

        // 3. Convierte el búfer de bytes en un búfer de tipo float.
        vertexBuffer = byteBuffer.asFloatBuffer();

        // 4. Copia las coordenadas de Java a la memoria nativa de la GPU.
        vertexBuffer.put(pointCoords);

        // 5. Reinicia el puntero del búfer a la posición inicial (índice 0) para que OpenGL pueda leerlo correctamente.
        vertexBuffer.position(0);

        // Carga, compilación y vinculación de los Shaders
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram(); // Crea un contenedor de programa OpenGL vacío
        GLES20.glAttachShader(mProgram, vertexShader); // Asocia el Vertex Shader al programa
        GLES20.glAttachShader(mProgram, fragmentShader); // Asocia el Fragment Shader al programa
        GLES20.glLinkProgram(mProgram); // Enlaza y valida la estructura de los shaders en la GPU
    }
    public void draw() {
        GLES20.glUseProgram(mProgram); // Activa el programa OpenGL con sus shaders vinculados

        // Obtiene la referencia de la variable 'vPosition' dentro del Vertex Shader
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Habilita el arreglo de atributos de vértices para que OpenGL use los datos
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Asocia los datos del búfer de vértices con el atributo de posición en la GPU
        GLES20.glVertexAttribPointer(
                positionHandle, coords_per_vertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        // Obtiene la referencia de la variable de color y asigna los valores RGBA
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // Ordena a OpenGL dibujar las primitivas geométricas (en este caso, un punto: GL_POINTS)
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertexCount);

        // Deshabilita el atributo de posición al terminar para liberar el estado
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
    // Código del Vertex Shader: Asigna las coordenadas del mundo directamente a la pantalla
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main(){" +
                    "  gl_Position = vPosition;" +
                    "}";

    // Código del Fragment Shader (actualmente vacío en este fragmento base)
    private final String fragmentShaderCode =
            "precision medium float;" +
                    "uniform vec4 vColor;" +
                    "";

    // Método auxiliar para crear, asociar código fuente y compilar un shader en la GPU
    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type); // Crea el espacio para el shader
        GLES20.glShaderSource(shader, shaderCode); // Inyecta el código fuente en GLSL
        GLES20.glCompileShader(shader); // Compila el shader para que la GPU lo entienda
        return shader;
    }
}


