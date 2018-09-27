package com.example.gravn.opengltest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLSurfaceView.Renderer;
import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.util.List;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Gravn on 11/05/2016.
 * GameManager implements Renderer to get the GameLoop.
 * Renderer uses GL10, but we are using GLES20.
 * Therefore most GL10 references are unused, but params to implement the Renderer
 */
public class GameManager implements Renderer
{
    private GLRenderer glRenderer;              // Render engine
    public static List<GameObject> objects;     // Objects list
    public static List<GameObject> tmpObjects;  // Temporary Object list
    public static Player player;                // The player
    public GestureDetector gestureDetector;     // Gesture Class
    public static LevelManager levelManager;    // Level Manager
    public static Context context;              // GameActivity Context
    public static float angle =0;               // angle, used by player
    public long lastStartTime;                  // Start time for delta time

    public GameManager(Context context)
    {
        this.context = context;
        glRenderer = new GLRenderer(context,new int[]{R.drawable.image2});

        objects = new Vector<GameObject>();
        tmpObjects = new Vector<GameObject>();
        new BackGround(new PointF(1024f,2048f),new PointF(2048f,4096f));
        player = new Player(new PointF(1024-64,2048-64),new PointF(128,128),context);
        levelManager = new LevelManager(20,2f,1.05f,1.05f,1.05f,0.95f,5,1.2f);
        gestureDetector = new GestureDetector(context, new SpecialAttack(context));
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config)
    {
        glRenderer.CreateSurface();                     // Create surface to draw on
        lastStartTime = SystemClock.elapsedRealtime();  // Set start time to current time
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        glRenderer.ChangeSurface(width, height);        // Change surface
    }

    @Override
    public void onDrawFrame(GL10 unused)
    {
        long startTime = System.currentTimeMillis();    // Set start time to current time in milliseconds
        long deltaTime = startTime-lastStartTime;       // Set delta time to difference between new and old start time
        lastStartTime = startTime;                      // Set last start time to current start time

        if(tmpObjects != objects)
        {
            tmpObjects.clear(); // Clear Temporary Object list
            for(int i=0;i<objects.size();i++)
            {
                tmpObjects.add(objects.get(i)); // Add all objects from Object list to Temp Object list
            }
        }
        levelManager.Update((float)deltaTime/1000f);    // Run update on level manager

        //PLayer angle wont change.
        angle += (float)Math.sqrt((player.speed.x*player.speed.x) + (player.speed.y*player.speed.y)) * deltaTime * 0.0001f;

        if(angle>=360)
        {
            angle-=360.0f;
        }
        for(GameObject object : tmpObjects)
        {
            object.Update((float)deltaTime/1000f);  // Update all objects
        }

        glRenderer.StartRender();           // Start render
        for(GameObject object : tmpObjects)
        {
            object.Render(glRenderer);      // Render all objects
        }
    }

    public void Pause()
    {

    }

    public void Resume()
    {

    }

    //Triggered from Custom SurfaceView's OnTouchEvent()
    public void TouchEvent(MotionEvent event)
    {
        gestureDetector.onTouchEvent(event);    // Detect all gestures from GestureControl/SpecialAttack
    }

}
