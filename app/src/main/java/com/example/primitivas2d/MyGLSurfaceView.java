package com.example.primitivas2d;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {
    private MyGLRenderer myGLRenderer;

    public MyGLSurfaceView(Context context){
        super(context);
        setEGLContextClientVersion(2);
        myGLRenderer = new MyGLRenderer();
        setRenderer(myGLRenderer);
    }
}
