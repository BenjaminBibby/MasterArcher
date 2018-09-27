package com.example.gravn.opengltest;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

/**
 * Created by Gravn on 11/05/2016.
 */
public class CustomGLSurfaceView extends GLSurfaceView
{
    private final GameManager gm;

    public CustomGLSurfaceView(Context context)
    {
        super(context);

        setEGLContextClientVersion(2);

        //GameManager extends renderer for GameLoop and sends calls through.
        gm = new GameManager(context);
        setRenderer(gm);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public void onPause()
    {
        super.onPause();

    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        gm.TouchEvent(event);
        return true;
    }

}
