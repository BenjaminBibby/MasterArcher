package com.example.gravn.opengltest;

import android.graphics.PointF;
import android.util.Log;

import java.util.Random;

/**
 * Created by Bibby on 24-05-2016.
 */
public abstract class Particle extends GameObject
{
    protected int particles;
    protected PointF particleSize;
    protected float particleSpeed;
    protected float[] particlesRot;
    protected float particleDuration;
    protected PointF[] particlesStartPos;
    protected PointF[] particlesPos;
    protected float[] particlesTimeLeft;
    protected float[] particlesSpeed;
    protected float effectDuration;
    protected float rotationSpeed;
    protected float rotation;
    protected boolean repeat;
    protected int xSpan;
    protected int ySpan;
    protected boolean follow;

    public Particle(PointF position, int particles, PointF particleSize, float particleDuration, float effectDuration, float[] spriteSheetInfo, float particleSpeed, float rotationSpeed, boolean repeat, int xSpan, int ySpan, boolean follow)
    {
        super(position, new PointF(30, 30));
        this.particles = particles;                         // Number of particles
        this.particlesPos = new PointF[particles];          // Offset of particles
        this.particleSpeed = particleSpeed;                 // Speed of particles
        this.particlesRot = new float[particles];           // Start rotation of particles
        this.particleSize = particleSize;                   // Size of particles
        this.particleDuration = particleDuration;           // Start/Reset time for particles
        this.particlesTimeLeft = new float[particles];      // Time until particle resets/dies
        this.particlesSpeed = new float[particles];         // Speed of individual particles
        this.effectDuration = effectDuration;               // Duration of entire particle effect
        this.rotationSpeed = rotationSpeed;                 // Rotation speed of particles
        this.rotation = 0;                                  // Current rotation angle of particles
        this.spriteSheetInfo = spriteSheetInfo;             // Particle sprite sheet info
        this.repeat = repeat;                               // Reset particles until effect stops
        this.xSpan = xSpan;                                 // The y span where the particles can spawn
        this.ySpan = ySpan;                                 // The x span where the particles can spawn
        this.particlesStartPos = new PointF[particles];     // Start position for offset
        this.follow = follow;                               // Follow parent object?

        ParticleSetup();                                    // Run particle setup
    }

    @Override
    protected void Update(float deltaTime)
    {
        super.Update(deltaTime);

        ParticleBehavior(deltaTime); // Runs behavior on each particles

        // Run if duration for entire effect is timed out
        if(this.effectDuration <= 0)
        {
            repeat = false; // Disable repeat
            // Check if all particles are done
            if(ParticlesFinished() == true)
            {
                Destroy(this);  // Destroy particle effect
            }
        }

        this.effectDuration -= deltaTime;           // Count down
        this.rotation += rotationSpeed * deltaTime; // Rotate

    }

    @Override
    protected void Render(GLRenderer glRenderer)
    {
        for(int i = 0; i < particles; i++)
        {
            // If current particle is not timed out
            if(particlesTimeLeft[i] > 0)
            {
                float xOffset, yOffset;
                xOffset = particlesStartPos[i].x - position.x;  // Set offset in x
                yOffset = particlesStartPos[i].y - position.y;  // Set offset in y
                // Render all particles with, offset, rotation, size & sprite
                SpriteRender(glRenderer, i, xOffset, yOffset);
            }
        }
    }

    // Render specific particle. Meant for overriding.
    protected void SpriteRender(GLRenderer glRenderer, int i, float xOffset, float yOffset)
    {
        glRenderer.RenderObject(getVertices(particlesPos[i].x + xOffset, particlesPos[i].y + yOffset, (float)DegToRad(particlesRot[i] + rotation), particleSize.x, particleSize.y), spriteSheetInfo);
    }

    // Set all particles at the beginning
    private void ParticleSetup()
    {
        for(int i = 0; i < particles; i++)
        {
            float xPos, yPos;
            xPos = -xSpan * .5f +(xSpan / particles) * i - RandomNumber(0, 5);  // Set x position for current particle
            yPos = RandomNumber(0, ySpan);                                      // Set y position for current particle

            particlesPos[i] = new PointF(xPos, yPos);                                           // Apply position for current particle
            particlesSpeed[i] = particleSpeed + RandomNumber(0, (int)(particleSpeed * 0.3f));   // Set speed for current particle
            particlesTimeLeft[i] = particleDuration + RandomFloat(0, particleDuration * 2);     // Set time for current particle
            // If rotation speed is set
            if(rotationSpeed > 0)
            {
                particlesRot[i] = RandomNumber(0, 90);  // Rotate current particle with a random factor
            }
            particlesStartPos[i] = follow == true ? position : new PointF(position.x, position.y);  // Set start position
        }
    }

    // Particle behavior for all particles
    private void ParticleBehavior(float deltaTime)
    {
        //Random rnd = new Random();    // Instantiate Random clas
        for (int i = 0; i < particles; i++)
        {
            // If particle is still alive
            if(particlesTimeLeft[i] > 0)
            {
                particlesPos[i].y += (particlesSpeed[i]) * deltaTime;   // Move current particle
                particlesTimeLeft[i] -= deltaTime;   // Duration of current particle
            }
            else if(particlesTimeLeft[i] <= 0 && repeat == true)
            {
                ResetParticle(i);   // Reset current particle
            }
            ExtraParticleBehavior(i, deltaTime);    // Extra behavior for different particle types
        }
    }

    // Reset specific particle
    private void ResetParticle(int particleID)
    {
        particlesTimeLeft[particleID] = particleDuration + RandomNumber(0, (int)particleDuration);      // Reset particle duration
        particlesPos[particleID] = new PointF(RandomNumber(0, xSpan), RandomNumber(0, ySpan));          // Reset particle position/offset
        particlesStartPos[particleID] = follow == true ? position : new PointF(position.x, position.y); // Reset particle start position
    }

    // Check to see if all particles are alive or dead
    private boolean ParticlesFinished()
    {
        for(int i = 0; i < particles; i++)
        {
            // If particle is still alive
            if(particlesTimeLeft[i] > 0)
            {
                return false;   // Return false, if a particle is still running
            }
        }
        return true;    // Returns true if all particles are dead
    }

    // Extra behavior for different particle types
    protected void ExtraParticleBehavior(int i, float deltaTime)
    {
        // Add code for extra ability
        // i = current particle in behavior loop
    }

    // Calculate a number from degree to radian
    protected double DegToRad(double d)
    {
        return Math.PI/180 * d;
    }

    // Get a random number between minimum and maximum
    protected int RandomNumber(int minimum, int maximum)
    {
        Random rn = new Random();       // Random generator
        int n = maximum - minimum + 1;  // Get difference between max and min
        int i = rn.nextInt() % n;       // Get random number within difference
        return minimum + i;             // Return minimum + random number
    }

    protected float RandomFloat(float minimum, float maximum)
    {
        Random rn = new Random();
        float n = maximum - minimum + 1;
        float i = rn.nextFloat() % n;
        return minimum + i;
    }
}
