package com.example.gravn.opengltest;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends Activity
{
    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        glSurfaceView = new CustomGLSurfaceView(this);

        setContentView(glSurfaceView);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        glSurfaceView.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //System.runFinalizersOnExit(true); //wait for threads to exit before clearing app
        //android.os.Process.killProcess(android.os.Process.myPid());  //remove app from memory
    }
}
