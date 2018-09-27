package com.example.gravn.opengltest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by Gravn on 12/05/2016.
 */
public abstract class GameObject
{
    /*
    SpriteSheetInfo:

    [0] = SpriteSheetID //Refers to boundGLtextureHandle
    [1] = Xstart;       //between 0.0f & 1.0f (6th image on a 4x4 sheet == 0.50f/3rd row)
    [2] = Ystart;       //between 0.0f & 1.0f (6th image on a 4x4 sheet == 0.25f/2nd col)
    [3] = Width;        //between 0.0f & 1.0f (4x4 sheet == 0.25f/(1/4))
    [4] = Height;       //between 0.0f & 1.0f (4x4 sheet == 0.25f/(1/4))
    */

    protected PointF position = new PointF(500,500);
    protected float[] spriteSheetInfo;
    protected PointF size = new PointF(256,1024);
    protected RectF collisionRect = new RectF(0, 0, 0, 0);
    protected float angle = 0;
    protected PointF speed = new PointF(0,0);

    public GameObject(PointF position,PointF size)
    {
        this.position = position;   // Set position
        this.size = size;           // Set size
        this.collisionRect = new RectF(position.x,position.y,position.x+size.x,position.y+size.y);  // Set size on collision box
        GameManager.objects.add(this);  // Add the object itself to the objects list
    }


    // Empty constructor
    public GameObject(){}

    // Update every frame
    protected void Update(float deltaTime)
    {
        this.position.x += speed.x * deltaTime; // Move object on x axis
        this.position.y += speed.y * deltaTime; // Move object on y axis

        // Check collision
        for(GameObject other : GameManager.tmpObjects)
        {
            // If other object is not this
            if(other != this)
            {
                // If there is no collision
                if(!CollisionCheck(other))
                {
                    return; // End method if there is no collision
                }
            }
        }
    }

    // Render sprite
    protected void Render(GLRenderer glRenderer)
    {
        glRenderer.RenderObject(getVertices(),spriteSheetInfo);
    }

    // Destroy GameObject in parameter
    public void Destroy(GameObject go)
    {
        //Log.i("GameObject","Destroying");
        GameManager.objects.remove(go);
    }

    // Get collision rectangle
    public RectF GetCollisionRect()
    {
        return new RectF(position.x, position.y, position.x+collisionRect.width(), position.y+collisionRect.height());
    }

    // Check if there is a collision with another GameObject
    public boolean CollisionCheck(GameObject other)
    {
        // If collision box intersects with other's collision box
        if(GetCollisionRect().intersect(other.GetCollisionRect()))
        {
            // If no collision was detected
            if(!OnCollision(other))
            {
                return false;   // Return false if no collision
            }
        }
        return true;    // Return true as default
    }

    public boolean OnCollision(GameObject other)
    {
        // Override
        //default return if checks should continue:
        return true;
    }

    //return vertices with rotation and movement for rendering.
    public float[] getVertices(float xOffSet,float yOffSet,float angle, float sizeX, float sizeY)
    {
        PointF[] vertices = new PointF[4];
        float sinValue = (float) Math.sin(angle);
        float cosValue = (float) Math.cos(angle);

        //Rotation:
        PointF center = new PointF(sizeX*0.5f,sizeY*0.5f);

        //(0.0,1.0)
        vertices[0] = new PointF(-center.x * cosValue - center.y * sinValue
                ,-center.x * sinValue + center.y * cosValue);
        //(0.0,0.0)
        vertices[1] = new PointF(-center.x * cosValue + center.y * sinValue
                ,-center.x * sinValue - center.y * cosValue);
        //(1.0,0.0)
        vertices[2] = new PointF(center.x * cosValue + center.y * sinValue
                ,center.x * sinValue - center.y * cosValue);
        //(1.0,1.0)
        vertices[3] = new PointF(center.x * cosValue - center.y * sinValue
                ,center.x * sinValue + center.y * cosValue);
        //Translation:
        for(int i=0;i<4;i++)
        {
            vertices[i].x += position.x+xOffSet;
            vertices[i].y += position.y+yOffSet;
        }

        return new float[]
        {
            vertices[0].x,vertices[0].y,0.0f,
            vertices[1].x,vertices[1].y,0.0f,
            vertices[2].x,vertices[2].y,0.0f,
            vertices[3].x,vertices[3].y,0.0f
        };
    }

    //Overloads for offfset and angle. Uses object size.
    public float[] getVertices(float xOffSet,float yOffSet,float angle)
    {
        return getVertices(xOffSet, yOffSet, angle, size.x, size.y);
    }

    //no Overloads, No offset, uses object angle and size.
    public float[] getVertices()
    {
        return getVertices(0,0,angle);
    }

    // Move GameObject towards a point, with a given speed
    public void MoveTowards(PointF point, float speed)
    {
//        float dx = point.x - this.position.x;
//        float dy = point.y - this.position.y;
//        float magnitude = speed/(float)Math.sqrt(dx*dx+dy*dy);
//        this.speed.x = dx / magnitude * speed;
//        this.speed.y = dx / magnitude *speed;

        this.speed.x = point.x - this.position.x;   // Difference in x coordinates
        this.speed.y = point.y - this.position.y;   // Difference in y coordinates
        double factor = speed/Math.sqrt(this.speed.x*this.speed.x+this.speed.y*this.speed.y);   // Speed factor
        this.speed.x *=factor;  // Apply speed in x
        this.speed.y *=factor;  // Apply speed in y
    }

    // Return distance between to points.
    public double GetDistance(PointF p1, PointF p2)
    {
        double dx = Math.abs(p1.x-p2.x);        // Difference in x
        double dy = Math.abs(p1.y-p2.y);        // Difference in y
        return  Math.sqrt((dx*dx)+(dy*dy));     // Distance formula
    }

    // Return angle between two points in Radians
    public double GetAngleBetweenPointsRad(PointF p1, PointF p2)
    {
        return Math.atan2(p2.y - p1.y, p2.x - p1.x);
    }

    // Return angle between two points in Degrees
    public double GetAngleBetweenPointsDeg(PointF p1, PointF p2)
    {
        return GetAngleBetweenPointsRad(p1, p2) * 180/Math.PI;
    }

    public PointF getSpeed()
    {
        return this.speed;
    }
}
