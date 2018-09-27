package com.example.gravn.opengltest;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by Bibby on 16-05-2016.
 */
public class Enemy extends GameObject
{
    protected int health;
    protected int startHealth;
    public int damage;
    public int range;
    public float coolDown;
    protected float currentCoolDown;
    public float pointsToGive;
    protected GameObject target = null;
    protected int spriteYstart = 2;

    protected float[] headSpriteInfo;
    protected float[] weaponSpriteInfo;
    protected float[] legSpriteInfo;
    protected float[] legangles;
    protected float z = 0;
    protected boolean dead = false;

    protected PointF weaponPosition;
    protected float weaponAngle;

    public Enemy(PointF position, PointF size, int health, int damage, int range, int coolDown, float pointsToGive, GameObject target)
    {
        super(position, size);
        this.health = health;               // Set current health
        this.startHealth = health;          // Set start health, this will not change when taking damage
        this.damage = damage;               // Set damage for when attacking player
        this.range = range;                 // Set range within the enemy can attack player
        this.coolDown = coolDown;           // Set cool down time for when it may attack
        this.currentCoolDown = 0;           // Set current cool down to 0. Will be reset to coolDown every time attacks
        this.target = target;               // Set target. Will always be player for this build
        this.pointsToGive = pointsToGive;   // Set the amount of points the player will recieve for killing this enemy
        Setup();                            // Run setup method for adjusting sprites
    }

    // Sprite setup
    public void Setup()
    {
        this.spriteSheetInfo = new float[]{0,0.25f,0.25f*spriteYstart,0.125f,0.125f};   // Set sprite coordinates for body
        this.headSpriteInfo = new float[]{0,0.375f,0.125f*spriteYstart,0.125f,0.125f};  // Set sprite coordinates for head
        this.legSpriteInfo = new float[]{0,0.0f,0.125f*6,0.125f,0.125f};                // Set sprite coordinates for legs
        this.legangles = new float[]{0.0f,0.0f};    // Set start angle for leg rotation
        weaponPosition = new PointF(0,0);           // Set start relative position weapon
        weaponAngle =0;                             // Set start angle for weapon rotation
    }

    @Override
    public void Update(float deltaTime)
    {
        super.Update(deltaTime);

        // While not dead, to avoid enemy actions if killed in same frame
        if(!dead)
        {
            // Iterate cool down negative towards 0
            if (currentCoolDown > 0)
            {
                currentCoolDown -= deltaTime * 50f;   // Subtract current cool down every frame
            }

            // Switch between attack and move
            if (target != null && InRange(target))
            {
                speed.x = 0;    // Stop moving in x
                speed.y = 0;    // Stop moving in y
                Attack();       // Start attacking!
            }
            else
            {
                MoveTowards(target.position, 150f);     // If not in range, the move toward target/player
                Animate(deltaTime);
            }
        }
    }

    // ALL ANIMATION FOR ALL BODY PARTS
    protected void Animate(float deltaTime)
    {
        z+=(float)Math.sqrt((speed.x*speed.x)+(speed.y*speed.y))*deltaTime*0.1f;

        if(z>=360)
        {
            z-=360.0f;
        }

        if(speed.x >0)
        {
            legSpriteInfo[1] = 0.0f;
        }
        else
        {
            legSpriteInfo[1] = 0.125f;
        }

        if(speed.x <= -0.25f || speed.x >= 0.25f)
        {
            legangles[0] = (float) Math.sin(z);
            legangles[1] = (float) Math.sin(z + 90);
        }

        if(legangles[0] >= 0.05f)
        {
            legangles[0] -=0.05f;
        }
        else
        {
            legangles[0] +=0.05f;
        }

        if(legangles[1] >= 0.05f)
        {
            legangles[1] -=0.05f;
        }
        else
        {
            legangles[1] +=0.05f;
        }

        if (speed.y < 0.25f)
        {
            this.spriteSheetInfo[2] = 0.125f*spriteYstart;
            this.headSpriteInfo[2] = 0.125f*spriteYstart;
        }
        else
        {
            this.spriteSheetInfo[2] = 0.125f*(spriteYstart+1);
            this.headSpriteInfo[2] = 0.125f*(spriteYstart+1);
        }
        //to -2.0
        if(speed.x < -2.0f)
        {
            this.spriteSheetInfo[1] = 0.0f;
            this.spriteSheetInfo[2] = 0.125f*spriteYstart;

            this.headSpriteInfo[1] = 0.5f;
            this.headSpriteInfo[2] = 0.125f*spriteYstart;
        }
        //-2.0 to -1.0
        if(speed.x >= -2.0f && speed.x < -1.0)
        {
            this.spriteSheetInfo[1] = 0.125f;
            this.headSpriteInfo[1] = 0.625f;
        }
        //-1.0 to 1.0
        if(speed.x >= -1.0f && speed.x <= 1.0f)
        {
            this.spriteSheetInfo[1] = 0.25f;
            this.headSpriteInfo[1] = 0.75f;
        }
        //1.0 to 2.0
        if(speed.x >1.0f && speed.x <2.0f)
        {
            this.spriteSheetInfo[1] = 0.375f;
            this.headSpriteInfo[1] = 0.875f;
        }
        //from 2.0
        if(speed.x >=2.0f)
        {
            this.spriteSheetInfo[1] = 0.0f;
            this.spriteSheetInfo[2] = 0.125f*(spriteYstart+1);

            this.headSpriteInfo[1] = 0.5f;
            this.headSpriteInfo[2] = 0.125f*(spriteYstart+1);
        }
    }

    @Override
    protected void Render(GLRenderer glRenderer)
    {
        float xScale = size.x/128f; // Scale in x
        float yScale = size.y/128f; // Scale in y

        glRenderer.RenderObject(getVertices(25.0f*xScale,-50.0f*yScale,legangles[0]),legSpriteInfo);    // Render 1'st leg
        glRenderer.RenderObject(getVertices(-25.0f*xScale,-50.0f*yScale,legangles[1]),legSpriteInfo);   // Render 2'nd leg

        super.Render(glRenderer);

        glRenderer.RenderObject(getVertices(0.0f,60.0f*yScale,0.0f),headSpriteInfo);    // Render head

        // Render health bar. (size * currentHealth/startHealth)
        glRenderer.RenderObject(getVertices(0*xScale, 128*yScale, 0, (256 * health /startHealth), 20), new float[]{0, 0.751f, 0.75f, 0.01f,0.01f});
    }

    // Attack methods
    public void Attack()
    {
        // Override
    }
    // When receiving damage
    public void TakeDamage(int damage)
    {
        this.health -= damage;  // Subtract damage from health
        new BloodParticle(position, 3);  // Create blood particle when hit

        // Destroy Object if health is below 0
        if(this.health <= 0 && !dead)
        {
            new BloodParticle(position, 7); // Add more blood when dead
            Die();                          // Kill enemy through Die method
        }
    }

    // When killed
    protected void Die()
    {
        GameManager.player.points += pointsToGive;  // Player receive points
        GameManager.levelManager.KillEnemy();       // Removes enemy from level manager
        this.Destroy(this);                         // Removes itself from object list
        dead = true;                                // Set dead to true, to avoid doing more this frame
    }

    // Check if other object is in range
    public boolean InRange(GameObject other)
    {
        //Check if outside Screen
        if(this.position.x+size.x >2048 || this.position.x < 0 || this.position.y+size.y > 4096 || this.position.y <0)
        {
            return false;   // Not in range if outside screen
        }

        return GetDistance(this.position, other.position) < range;  // Return true if within range, false if not
    }
}
