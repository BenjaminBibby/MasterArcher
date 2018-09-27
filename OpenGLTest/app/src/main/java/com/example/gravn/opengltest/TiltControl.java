package com.example.gravn.opengltest;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;

/**
 * Created by Mathiaspc on 16/05/2016.
 */
public class TiltControl extends View
{
    float x,y;
    public TiltControl(Context context)
    {
        super(context);
        this.x = 0;
        this.y = 0;
        ((SensorManager)context.getSystemService(Context.SENSOR_SERVICE)).registerListener(
                new SensorEventListener()
                {
                    @Override
                    public void onSensorChanged(SensorEvent event)
                    {
                        //based on phone tilt (ignore Z axis)
                        x = -event.values[0];
                        y = -event.values[1];
                    }
                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) //ignore this event
                    {

                    }
                },
                ((SensorManager)context.getSystemService(Context.SENSOR_SERVICE))
                        .getSensorList(Sensor.TYPE_ACCELEROMETER).get(0), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public float getX()
    {
        return this.x;
    }

    public float getY()
    {
        return this.y;
    }
}
