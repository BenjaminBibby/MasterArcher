package com.example.gravn.opengltest;

import android.graphics.PointF;

/**
 * Created by Bibby on 25-05-2016.
 */
public class HealParticle extends Particle
{

    public HealParticle(PointF position)
    {
        super(position, 7, new PointF(40, 15), 1.5f, 1f, new float[]{0, 0.751f, 0.75f, 0.01f,0.01f}, 110, 15, false, 140, 120, true);
    }

    @Override
    protected void SpriteRender(GLRenderer glRenderer, int i, float xOffset, float yOffset)
    {
        // Render two green rectangles crossing each other(Cross/Plus)
        glRenderer.RenderObject(getVertices(particlesPos[i].x + xOffset, particlesPos[i].y + yOffset, (float)DegToRad(particlesRot[i] + rotation), particleSize.x, particleSize.y), spriteSheetInfo);
        glRenderer.RenderObject(getVertices(particlesPos[i].x + xOffset, particlesPos[i].y + yOffset, (float)DegToRad(particlesRot[i] + rotation + 90), particleSize.x, particleSize.y), spriteSheetInfo);
    }
}
