package com.example.gravn.opengltest;

import android.gesture.Gesture;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Bibby on 19-05-2016.
 */
public class GestureControl extends GestureDetector.SimpleOnGestureListener
{
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        float x1 = e1.getX();   // Start input x
        float y1 = e1.getY();   // Start input y

        float x2 = e2.getX();   // End input x
        float y2 = e2.getY();   // End input y

        Direction direction = getDirection(x1, y1, x2, y2); // Direction of swipe

        return onSwipe(direction);
    }

    // Call method when swiped. Meant for overriding
    public boolean onSwipe(Direction direction)
    {
        // Override this
        return false;
    }

    // Get a direction
    public Direction getDirection(float x1, float y1, float x2, float y2)
    {
        double angle = getAngle(x1, y1, x2, y2);    // Angle between coordinates
        return Direction.get(angle);                // Return direction
    }

    // Get angele between coordinates
    public double getAngle(float x1, float y1, float x2, float y2)
    {
        double rad = Math.atan2(y1-y2, x2-x1) + Math.PI;    // Get angle in radians
        return (rad*180/Math.PI + 180)%360;                 // Return as degrees
    }

    // Direction enum
    public enum Direction
    {
        up, down, left, right;  // Return values

        // Get direction based on swipe
        public static Direction get(double angle)
        {
            if(inRange(angle, 45, 135))
            {
                return Direction.up;
            }
            else if(inRange(angle, 0, 45) || inRange(angle, 315, 360))
            {
                return Direction.right;
            }
            else if(inRange(angle, 225, 315))
            {
                return Direction.down;
            }
            else
            {
                return Direction.left;
            }
        }

        // Check if swipe is between two angles
        private static boolean inRange(double angle, float init, float end)
        {
            return (angle >= init) && (angle < end);
        }
    }
}
