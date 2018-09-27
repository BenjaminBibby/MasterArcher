package com.example.gravn.opengltest;

import android.graphics.PointF;

/**
 * Created by Mathiaspc on 20/05/2016.
 */
public class MeleeEnemy extends Enemy
{
    private float[] shieldSpriteInfo;

    public MeleeEnemy(PointF position, PointF size, int health, int damage, int range, int coolDown,float pointsToGive, GameObject target)
    {
        super(position, size, health, damage, range, coolDown,pointsToGive, target);
            this.spriteYstart = 4;                                              // Find the sprites row in sprite sheet
        this.weaponSpriteInfo = new float[]{0,0.5f,0.125f*6,0.125f,0.125f};     // Sprite coordinate for weapon
        this.shieldSpriteInfo = new float[]{0,0.625f,0.125f*6,0.125f,0.125f};   // Sprite coordinate for shield
    }

    @Override
    public void Attack()
    {
        super.Attack();

        // If cool down is ready for attack
        if (currentCoolDown <= 0)
        {
            Player p = (Player)target;      // Cast target to player
            p.TakeDamage(damage);           // Inflict damage upon target/player
            currentCoolDown = coolDown;     // Reset cool down

            float dx = target.position.x-this.position.x;   // Difference in x position between player and enemy
            float dy = target.position.y-this.position.y;   // Difference in y position between player and enemy
            weaponPosition.x = dx/(float)Math.sqrt(dx*dx+dy*dy)*size.x;     // Set weapon position x
            weaponPosition.y = dy/(float)Math.sqrt(dx*dx+dy*dy)*size.y;     // Set weapon position y
            weaponAngle = (float)GetAngleBetweenPointsRad(this.position,target.position)-(float)Math.PI/2f; // Set angle of weapon towards target
        }
    }

    @Override
    public void Update(float deltaTime)
    {
        super.Update(deltaTime);

        // Cool down with a delay after displaying attack
        if(currentCoolDown <= coolDown/1.1f)
        {
            weaponPosition.x = 0.0f;    // Reset weapon position x
            weaponPosition.y = 0.0f;    // Reset weapon position x
            weaponAngle = 0.0f;         // Reset weapon angle
        }


    }
    @Override
    public void Render(GLRenderer glRenderer)
    {
        float xScale = size.x/128f;     // Scale in x
        float yScale = size.y/128f;     // Scale in y

        // Animate weapon and shield when moving right
        if(speed.y > 0.25f)
        {
            glRenderer.RenderObject(getVertices(-40.0f*xScale, 20.0f*yScale, 0), shieldSpriteInfo);
            glRenderer.RenderObject(getVertices(weaponPosition.x+55.0f*xScale, weaponPosition.y+25f*yScale,weaponAngle), weaponSpriteInfo);
        }

        super.Render(glRenderer);

        // Animate weapon and shield when moving left
        if (speed.y < 0.25f)
        {
            glRenderer.RenderObject(getVertices(32.0f*xScale, -20.0f*yScale, 0), shieldSpriteInfo);
            glRenderer.RenderObject(getVertices(weaponPosition.x-40.0f*xScale, weaponPosition.y,weaponAngle), weaponSpriteInfo);
        }
    }
}
