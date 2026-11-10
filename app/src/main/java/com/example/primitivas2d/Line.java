package com.example.primitivas2d;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Line {

    // Búfer para guardar las coordenadas de los puntos y que la GPU pueda leerlos (Java y la GPU no comparten memoria directamente).
    private final FloatBuffer vertexBuffer;

    // El "programa" de OpenGL: una cajita donde metemos los shaders (vertex y fragment) ya cocinados y listos para usarse.
    private final int mProgram;

    // Los "controles" o "ganchos" para encontrar las variables que creamos dentro de los shaders desde Java.
    private int positionHandle, colorHandle;

    // 1. Las coordenadas de la línea. Como es 2D, solo usa X e Y por cada punto.
    // Aquí tenemos 2 puntos: El punto inicial (X1, Y1) y el punto final (X2, Y2).
    private final float[] lineCoord = {
            -0.5f,  0.5f, // Punto 1: Izquierda y arriba
            0.5f,  0.0f  // Punto 2: Derecha y centro
    };

    // El color de la línea en formato RGBA (Rojo, Verde, Azul, Transparencia).
    // Van de 0.0f a 1.0f. Este en específico es un Amarillo brillante (Mucho rojo, mucho verde, cero azul, total opacidad).
    float[] color = { 1.0f, 1.0f, 0.0f, 1.0f };

    public Line() {
        // 1. Pedimos un bloque de memoria pura y dura en bytes (fuera de Java).
        // ¿De qué tamaño? Multiplicamos cuántos números floats hay en nuestro arreglo (lineCoord.length) por 4 bytes (lo que pesa un float).
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(lineCoord.length * 4);

        // 2. Configuramos el orden en que se leen los bytes según el celular o procesador (para que no lea al revés los números).
        byteBuffer.order(ByteOrder.nativeOrder());

        // 3. Convertimos ese bloque de bytes crudos en un búfer de números flotantes (FloatBuffer) porque nuestras coordenadas son floats.
        vertexBuffer = byteBuffer.asFloatBuffer();

        // 4. Copiamos los números de nuestro arreglo de Java (lineCoord) hacia esa memoria nativa de la GPU.
        vertexBuffer.put(lineCoord);

        // 5. ¡Súper importante! Ponemos el puntero del búfer otra vez al inicio (en la posición 0).
        // Si no hacemos esto, OpenGL empieza a leer desde el final y no dibuja nada.
        vertexBuffer.position(0);

        // 6. Creamos y compilamos los dos mini-programas que corren en la tarjeta gráfica (Shaders):
        // - El Vertex Shader (para las posiciones y formas).
        // - El Fragment Shader (para los colores).
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        // 7. Creamos un "programa" vacío en OpenGL que servirá como contenedor.
        mProgram = GLES20.glCreateProgram();

        // 8. Metemos (adjuntamos) los dos shaders ya compilados dentro de ese programa.
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        // 9. Enlazamos el programa. Aquí OpenGL revisa que los shaders se lleven bien y que las variables se conecten correctamente.
        GLES20.glLinkProgram(mProgram);
    }

    // Una funcioncita ayudante para no repetir código: crea, le inyecta el texto y compila cualquier tipo de shader.
    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type); // Crea el espacio en blanco para el shader
        GLES20.glShaderSource(shader, shaderCode); // Le pega el código escrito en GLSL
        GLES20.glCompileShader(shader); // Lo compila para que la tarjeta gráfica lo entienda
        return shader;
    }

    // 1. VERTEX SHADER (Escrito en GLSL): Es el encargado de recibir las coordenadas y decidir dónde van en la pantalla.
    // Como está vacío en tus comillas "", en este momento no hace transformaciones locas, solo pasa las coordenadas tal cual.
    private final String vertexShaderCode =
            "" ;

    // 2. FRAGMENT SHADER (Escrito en GLSL): Es el encargado de pintar los píxeles uno por uno.
    // También está vacío aquí porque el color se lo mandaremos directamente desde Java más abajo.
    private final String fragmentShaderCode =
            "" ;


    // Método que mandas a llamar cada vez que quieres que la línea aparezca dibujada en la pantalla.
    public void draw() {
        // 1. Le decimos a OpenGL: "¡Usa este programa de shaders que preparamos en el constructor!"
        GLES20.glUseProgram(mProgram);

        // 2. Buscamos la variable llamada "vPosition" dentro del Vertex Shader para saber dónde conectar los puntos.
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // 3. Activamos ese atributo de posición para que OpenGL abra la puerta y acepte los datos.
        GLES20.glEnableVertexAttribArray(positionHandle);

        // 4. Le pasamos los datos de los vértices a OpenGL:
        GLES20.glVertexAttribPointer(
                positionHandle,
                2,                  // Tamaño: Usamos 2 coordenadas por cada punto (X e Y).
                GLES20.GL_FLOAT,    // Tipo de dato: Son números decimales (Float).
                false,              // Normalización: Falso porque son coordenadas normales.
                0,                  // Stride (Zancada): 0 porque los datos están pegaditos uno tras otro sin saltos raros.
                vertexBuffer        // De dónde va a sacar los datos: De nuestro búfer en memoria nativa.
        );

        // 5. Buscamos la variable de color "vColor" en el Fragment Shader.
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // 6. Le inyectamos el color amarillo que declaramos en Java (arreglo `color`) directamente a la GPU.
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        // 7. ¡A dibujar se ha dicho!
        // Usamos GL_LINES para que OpenGL junte los puntos en parejas y dibuje una línea.
        // Empezamos desde el índice 0 y dibujamos un total de puntos igual a: (longitud del arreglo / 2 coordenadas por punto).
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, lineCoord.length / 2);

        // 8. Por mera buena educación y para limpiar la casa, apagamos el atributo de posición al terminar.
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}