package com.example.gravn.opengltest;

import android.graphics.PointF;
import android.util.Log;

import java.util.Random;

/**
 * Created by Gravn on 21/05/2016.
 */
public class BackGround extends GameObject
{
    private Random rand;
    private PointF[] bgdetails;
    private int[] type;
    private float[][] spriteInfo;
    public float number = 0;
    private float[] spriteNumbers;

    public BackGround(PointF position,PointF size)
    {
        super(position, size);
        this.spriteSheetInfo = new float[]{0,0.0f,0.875f,0.125f,0.125f};
        this.spriteNumbers = new float[]{0,0.25f,0.875f,0.0625f,0.125f};
        spriteInfo = new float[][]
        {
                {0,0.13f,0.875f,0.0625f,0.0625f},
                {0,0.1925f,0.875f,0.0625f,0.0625f},
                {0,0.13f,0.9375f,0.0625f,0.0625f},
                {0,0.1925f,0.9375f,0.0625f,0.0625f}
        };
        bgdetails = new PointF[32];
        type = new int[bgdetails.length];
        rand = new Random();
        for(int i=0;i<bgdetails.length;i++)
        {
            bgdetails[i] = new PointF(rand.nextFloat()*2048-1024,rand.nextFloat()*4096-2048);
            type[i] = rand.nextInt(3);
        }
    }

    @Override
    public void Render(GLRenderer glRenderer)
    {
        super.Render(glRenderer);

        for(int i=0;i<bgdetails.length;i++)
        {
            glRenderer.RenderObject(getVertices(bgdetails[i].x,bgdetails[i].y,0,128f,128f),spriteInfo[type[i]]);
        }
    }

    @Override
    public void Update(float deltaTime)
    {
        //spriteNumbers[1] = 0.25f+(0.0625f*(int)Player.getInstance().points);
    }

    @Override
    public boolean CollisionCheck(GameObject other)
    {
        //Override;
        return true;
    }
}
