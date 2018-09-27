package com.example.gravn.opengltest;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by Bibby on 16-05-2016.
 */
public class RangedEnemy extends Enemy
{


    public RangedEnemy(PointF position, PointF size, int health, int damage, int range, int coolDown,float pointsToGive, GameObject target)
    {
        super(position, size, health, damage, range, coolDown,pointsToGive, target);
        this.spriteYstart = 2;  // Find the sprites row in sprite sheet
        this.weaponSpriteInfo = new float[]{0,0.25f,0.125f*6,0.125f,0.125f};    // Sprite coordinates for weapon
    }

    @Override
    public void Attack()
    {
        float dx = target.position.x-this.position.x;   // Difference in x position between target and enemy
        float dy = target.position.y-this.position.y;   // Difference in y position between target and enemy
        weaponPosition.x = dx/(float)Math.sqrt(dx*dx+dy*dy)*size.x*0.5f;    // Set weapon position x
        weaponPosition.y = dy/(float)Math.sqrt(dx*dx+dy*dy)*size.y*0.5f;    // Set weapon position y
        weaponAngle = (float)GetAngleBetweenPointsRad(this.position,target.position);   // Set weapon angle

        // If cool down is ready to attack
        if(currentCoolDown <= 0)
        {
            // Creates arrow and reset cool down
            new Arrow(new PointF(this.position.x,this.position.y),new PointF(128,128),350,1,target);
            currentCoolDown = coolDown;
        }
    }

    @Override
    public void Render(GLRenderer glRenderer)
    {
        float xScale = size.x/128f; // Scale in x
        float yScale = size.y/128f; // Scale in y

        super.Render(glRenderer);

        // Render weapon
        glRenderer.RenderObject(getVertices(weaponPosition.x*xScale, weaponPosition.y*yScale,weaponAngle), weaponSpriteInfo);

    }
}
