package com.example.gravn.opengltest;

import android.graphics.PointF;

/**
 * Created by Bibby on 25-05-2016.
 */
public class BloodParticle extends Particle
{
    private float gravity;
    private float[] particlesGravity;
    private float[] particlesSpeed_x;

    public BloodParticle(PointF position, int particles)
    {
        super(position, particles, new PointF(50, 50), 0.7f, 1f, new float[]{0, 0.875f, 0.750f, 0.125f, 0.125f}, 400f, 0, false, 1, 1, false);
        this.gravity = 3.0f;                            // Set gravity factor
        this.particlesGravity = new float[particles];   // Add individual gravity to particles
        this.particlesSpeed_x = new float[particles];   // Add individual x speed to particles
        for(int i = 0; i < particles; i++)
        {
            particlesSpeed_x[i] = -100 + (200 / particles) * i; // Apply x speed to particles
        }
    }

    @Override
    protected void ExtraParticleBehavior(int i, float deltaTime)
    {
        particlesGravity[i] -= Math.pow(gravity, 2);            // Decrease gravity
        particlesPos[i].x += particlesSpeed_x[i] * deltaTime;   // Apply x speed to particle position
        particlesPos[i].y += particlesGravity[i] * deltaTime;   // Apply gravity to particle y speed
        particlesRot[i] = (float)GetAngleBetweenPointsDeg(new PointF(0, 0), particlesPos[i]) - 90;  // Rotate particle to match direction
    }
}
