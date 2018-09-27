package com.example.gravn.opengltest;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by Bibby on 16-05-2016.
 */
public class Arrow extends GameObject
{
    protected float movespeed;
    protected int damage;
    protected GameObject target;
    protected int OwnerType;
    protected PointF targetposition;

    // Main Constructor(Taget)
    public Arrow(PointF position, PointF size, float speed, int damage, GameObject target)
    {
        super(position, size);
        this.movespeed = speed;     // Set movement speed
        this.damage = damage;       // Set damage
        this.target = target;       // Set target
        this.targetposition = new PointF(target.position.x,target.position.y);  // Set target position to targets x & y
        angle = (float)GetAngleBetweenPointsRad(position,targetposition);       // Get angle between arrow and target
        setDirection(target);       // Apply direction to arrow
        this.spriteSheetInfo = new float[]{0,0.375f,0.125f*6,0.125f,0.125f};    // Get sprite from sprite sheet
    }

    // Secondary Constructor(Position)
    public Arrow(PointF position, PointF size, float speed, int damage,int ownerType,PointF targetposition)
    {
        super(position, size);
        this.movespeed = speed;     // Set movement speed
        this.damage = damage;       // Set damage
        this.target = null;         // Set target
        this.OwnerType = ownerType; // Specify owner of arrow
        this.targetposition = targetposition;   // Get targets position
        angle = (float)GetAngleBetweenPointsRad(position,targetposition);   // Get angle between target position and arrow
        setDirection(targetposition);   // Apply direction to arrow
        this.spriteSheetInfo = new float[]{0,0.375f,0.125f*6,0.125f,0.125f};    // Get sprite from sprite sheet
    }

    @Override
    public void Update(float deltaTime)
    {
        super.Update(deltaTime);

        //Check if outside Screen, if so, Destroy.
        if(this.position.x >2048 || this.position.x < 0 || this.position.y > 4096 || this.position.y <0)
        {
            this.Destroy(this); // Destroy self if outside screen bounds
        }
    }

    @Override
    public boolean OnCollision(GameObject other)
    {
        // Run only if target is not null
        if(target != null)
        {
            // If arrow target is player aka fired by enemy.
            if (target.getClass() == Player.class)
            {
                // If other class is player
                if (other.getClass() == Player.class)
                {
                    GameManager.player.TakeDamage(this.damage); // Damage on player
                    this.Destroy(this);                         // Destroy arrow on collision
                    return false;                               // Return false and end method
                }
            }

            // If arrow target is enemy aka fired by player
            if (target.getClass().equals(Enemy.class) || target.getClass().equals(RangedEnemy.class) || target.getClass().equals(MeleeEnemy.class) || target.getClass().equals(Boss.class))
            {
                // If other class is enemy of any kind
                if (other.getClass().equals(Enemy.class) || other.getClass().equals(RangedEnemy.class) || other.getClass().equals(MeleeEnemy.class) || other.getClass().equals(Boss.class))
                {
                    Enemy e = (Enemy) other;        // Cast other as enemy
                    // As long enemy is not dead
                    if(!e.dead)
                    {
                        e.TakeDamage(this.damage);  // Apply damage on enemy
                    }
                    this.Destroy(this);             // Destroy arrow on collision
                    return false;                   // Return false and end method
                }
            }
        }
        else
        {
            // If owner type is 1 aka Enemy owner
            if(OwnerType == 1)
            {
                // If other class is player
                if (other.getClass() == Player.class)
                {
                    GameManager.player.TakeDamage(this.damage); // Apply damage on player
                    this.Destroy(this);                         // Destroy arrow on collision
                    return false;                               // Return false and end method
                }
            }

            // If arrow target is enemy aka fired by player
            if (OwnerType == 0)
            {
                // If other class is enemy of any kind
                if (other.getClass().equals(Enemy.class) || other.getClass().equals(RangedEnemy.class) || other.getClass().equals(MeleeEnemy.class) || other.getClass().equals(Boss.class))
                {
                    Enemy e = (Enemy) other;        // Cast other as enemy
                    // As long enemy is not dead
                    if(!e.dead)
                    {
                        e.TakeDamage(this.damage);  // Apply damage on enemy
                    }
                    this.Destroy(this);             // Destroy arrow on collision
                    return false;                   // Return false and end method

                }
            }
        }

        return true;    // Ends method by default if no collision was detected
    }

    // Change direction towards specific target
    private void setDirection(GameObject target)
    {
        this.speed.x = target.position.x - this.position.x; // Set speed.x relative to distance
        this.speed.y = target.position.y - this.position.y; // Set speed.y relative to distance
        double factor = movespeed/Math.sqrt(this.speed.x*this.speed.x+this.speed.y*this.speed.y);   // Movement speed factor
        this.speed.x *=factor;  // Apply speed.x
        this.speed.y *=factor;  // Apply speed.y
    }

    // Change direction towards specific point
    public void setDirection(PointF point)
    {
        angle = (float)GetAngleBetweenPointsRad(position, point);   // Get angle between position and point
        this.speed.x = point.x - this.position.x;   // Set speed.x relative to distance
        this.speed.y = point.y - this.position.y;   // Set speed.y relative to distance
        double factor = movespeed/Math.sqrt(this.speed.x*this.speed.x+this.speed.y*this.speed.y);   // Movement speed factor
        this.speed.x *=factor;  // Apply speed.x
        this.speed.y *=factor;  // Apply speed.y
    }
}
