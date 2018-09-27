package com.example.gravn.opengltest;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;

import java.util.Random;

/**
 * Created by Mathiaspc on 19/05/2016.
 */
public class Boss extends Enemy
{
    public Boss(PointF position, PointF size, int health, int damage, int range, int coolDown,float pointsToGive, GameObject target)
    {
        super(position, size, health, damage, range, coolDown,pointsToGive, target);

        this.spriteYstart = 4;                                                      // Row on sprite sheet
        this.spriteSheetInfo = new float[]{0, 0.25f, 0.5f, 0.125f, 0.125f};         // Sprite coordinates for body
        this.weaponSpriteInfo = new float[]{0, 0.25f, 0.125f * 6, 0.125f, 0.125f};  // Sprite coordinates for weapon
        this.speed.x = 300f;                                                        // X speed
    }

    @Override
    public void Update(float deltaTime)
    {
        // When reaching screen bounds
        if (this.position.x + size.x / 2f > 2048 && speed.x > 0)
        {
            speed.x = -300f;    // Move left
        }

        if (this.position.x - size.x / 2 < 0 && speed.x < 0)
        {
            speed.x = 300f;     // Move right
        }

        this.currentCoolDown -= deltaTime;  // Cool down attack

        // If ready to attack
        if (currentCoolDown < 0)
        {
            Attack(1);                  // Start attacking
            currentCoolDown = coolDown; // Reset cool down
        }

        // Start fleeing when low on health
        if (health > (float) startHealth / 2f)
        {
            if (this.position.y > 4096 - size.y-128)
            {
                this.speed.y = -100f;   // Run to the bottom of screen
            } else
            {
                this.speed.y = 0;       // Stop when reached the bottom
            }
        } else
        {
            if (this.position.y > size.y)
            {
                this.speed.y = -300f;   // Run from outside screen to top of screen
            } else
            {
                this.speed.y = 0f;      // Stop when inside the level
            }
        }

        this.position.x += speed.x * deltaTime;     // Move in x direction
        this.position.y += speed.y * deltaTime;     // Move in y direction

        Animate(deltaTime);
    }

    @Override
    public void Die()
    {
        GameManager.levelManager.EndLevel();    // End current level
        super.Die();
    }

    // When attacking
    private void Attack(int type)
    {
        Random r = new Random();

        // Switch between two special attack
        switch (r.nextInt(2))
        {
            case 0:
                // Arrow storm
                Arrow[] superArrows = new Arrow[12];    // Make 12 arrows
                for (int i = 0; i < superArrows.length; i++)
                {
                    // Get position of target and add arrow in that direction
                    PointF targetpos = new PointF(GameManager.player.position.x,GameManager.player.position.y);
                    superArrows[i] = new Arrow(new PointF(this.position.x, this.position.y), new PointF(128,128), 200+30f*i,damage, 1,targetpos);
                    superArrows[i].setDirection(targetpos);
                }
                break;

            case 1:
                // Multi Arrow
                float span = 15f;                       // Span in angle
                Arrow[] arrows = new Arrow[12];         // Make 12 arrows
                for(int i = 0; i < arrows.length; i++)
                {
                    float x, y;
                    double a = GetAngleBetweenPointsDeg(this.position,target.position); // Angle between boss and target

                    double offset = -(span * .5f) + (span /arrows.length) * i;          // Angular offset between arrows

                    x = this.position.x + 1f * (float)Math.cos((a + offset)*Math.PI/180);   // New X
                    y = this.position.y + 1f * (float)Math.sin((a + offset)*Math.PI/180);   // New Y

                    PointF dir = new PointF(x, y);  // Target direction

                    // Instantiate arrow and change it's angle to match the new direction
                    arrows[i] = new Arrow(new PointF(this.position.x,this.position.y),new PointF(128,256),350,damage,this.target);
                    arrows[i].setDirection(dir);
                }
                break;
        }
    }

    @Override
    public void Render(GLRenderer glRenderer)
    {
        super.Render(glRenderer);
    }
}
