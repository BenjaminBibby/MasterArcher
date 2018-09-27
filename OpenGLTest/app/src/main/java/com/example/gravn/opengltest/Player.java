package com.example.gravn.opengltest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.os.Vibrator;
import android.support.annotation.IntRange;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import android.graphics.RectF;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Mathiaspc on 16/05/2016.
 */
public class Player extends GameObject
{
    //region Singleton
    private static Player instance = null;

    // Empty constructor
    public Player()
    {
    }

    // Singleton
    public static Player getInstance()
    {
        if (instance == null)
        {
            instance = new Player();    // Create player if there is none
        }
        return instance;    // Return existing player
    }
  // endregion

    private float[] headSpriteInfo;
    private float[] weaponSpriteInfo;
    private float[] legSpriteInfo;
    private float[] legangles;
    private float z = 0;
    private PointF weaponPosition;
    private float weaponAngle = 0;
    private boolean dead = false;

    TiltControl tiltControl = null;
    private int health;
    private float damage;
    private float range;
    private PointF closestEnemyPos;
    public GameObject closestEnemy;
    public static float points;
    private Context context;
    float timer;
    private float specialTimer;
    private float currentSpecialTimer;

    public Player(PointF position, PointF size, Context context)
    {
        super(position, size);
        this.context = context;                 // Set activity
        tiltControl = new TiltControl(context); // Get TiltControl for gyroscope

        this.health = 100;                      // Set health to 100
        this.damage = 20;                       // Set damage to 20
        this.range = 1000;                      // Set range to 1000
        this.weaponPosition = new PointF(0,0);  // Position of weapon
        //this.speed = new PointF(1, 1);
        this.spriteSheetInfo = new float[]{0,0.0f,0.0f,0.125f,0.125f};          // Sprite sheet coordinates
        this.headSpriteInfo = new float[]{0, 0.375f, 0.0f, 0.125f, 0.125f};     // Sprite info for head
        this.weaponSpriteInfo = new float[]{0, 0.25f, 0.125f*6, 0.125f, 0.125f};// Sprite info for weapon
        this.legSpriteInfo = new float[]{0, 0.0f, 0.125f*6, 0.125f, 0.125f};    // Sprite info for legs
        this.legangles = new float[]{0.0f,0.0f};                                // Angles for legs

        this.points = 0;                        // Points/Score


        this.specialTimer = 0;                  // Cool down factor for special attack
        this.currentSpecialTimer = 0;           // Current cool down for special attack
    }

    @Override
    protected void Update(float deltaTime)
    {
        //Log.i("Player","z0:"+z+" Dt:"+deltaTime);
        super.Update(deltaTime);
        //TODO maximum speed?
        this.speed.x = tiltControl.getX() * 150f;           // Get x speed from gyroscope
        this.speed.y = tiltControl.getY() * 150f;           // Get y speed from gyroscope

        // If player reaches screen width
        if(this.position.x+size.x/2f >2048 && speed.x>0)
        {
            speed.x = 0;    // Stop movement in x
        }
        // If player reaches 0 in x coordinate
        if(this.position.x-size.x/2 < 0 && speed.x <0)
        {
            speed.x = 0;    // Stop movement in x
        }
        // If player reaches screen height
        if(this.position.y+size.y > 4096 && speed.y >0)
        {
            speed.y = 0;    // Stop movement in y
        }
        // If player reaches 0 in y coordinate
        if(this.position.y-size.y  <0  && speed.y <0)
        {
            speed.y = 0;    // Stop movement in y
        }

        timer+=deltaTime;                   // Update timer for attack
        closestEnemy = ClosestEnemy(range); // Get closest enemy
        // If there is a enemy in range
        if(closestEnemy != null)
        {
            float dx = closestEnemy.position.x-this.position.x;                 // Difference in x between enemy and player
            float dy = closestEnemy.position.y-this.position.y;                 // Difference in y between enemy and player
            weaponPosition.x = dx/(float)Math.sqrt(dx*dx+dy*dy)*size.x*0.25f;   // Set weapon x position
            weaponPosition.y = dy/(float)Math.sqrt(dx*dx+dy*dy)*size.y*0.25f;   // Set weapon y position
            weaponAngle = (float)GetAngleBetweenPointsRad(this.position,closestEnemy.position); // Set weapon angle
            Attack();   // Run attack method
        }
        else
        {
            weaponAngle = 0;    // Reset weapon angle
        }

        // If current special timer is not cooled down
        if(currentSpecialTimer > 0)
        {
            currentSpecialTimer-=deltaTime; // Cool down special timer
        }
        //Log.i("Player","z1:"+z+" Dt:"+deltaTime);

        //z=50f;
        PlayerAnimation(deltaTime); // Run player animation
    }

    // Return closest enemy
    public GameObject ClosestEnemy(float range)
    {
        float shortestDistance = range; // Set shortest distance as range
        GameObject closest = null;
        for(GameObject object : GameManager.tmpObjects)
        {
            // If current object is enemy
            if(object.getClass().equals(Enemy.class)
                    || object.getClass().equals(RangedEnemy.class)
                    || object.getClass().equals(MeleeEnemy.class)
                    || object.getClass().equals(Boss.class))
            {
                // If distance between current enemy is less than range
                if(GetDistance(this.position,object.position) <= shortestDistance)
                {
                    shortestDistance = (float)GetDistance(this.position,object.position);   // Reset shortest distance, if distance is shorter
                    closest = object;                                                       // Set closest enemy to current
                }
            }
        }
        return closest; // Return closest enemy
    }

    public void PlayerAnimation(float deltaTime)
    {
//        z+=5000f;
//        z = 50f;
//        if(z>=360)
//        {
//            z-=360.0f;
//        }
//        float a = (float)Math.sqrt((speed.x*speed.x)+(speed.y*speed.y))*deltaTime*0.1f;
//        Log.i("Player","z2:"+z+" Dt:"+deltaTime+":"+a);

        //Animate various parts according to speed/direction.
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

            legangles[0] = (float) Math.sin(GameManager.angle);
            legangles[1] = (float) Math.sin(GameManager.angle+90f);
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
            this.spriteSheetInfo[2] = 0.0f;
            this.headSpriteInfo[2] = 0.0f;
        }
        else
        {
            this.spriteSheetInfo[2] = 0.125f;
            this.headSpriteInfo[2] = 0.125f;
        }
        //to -2.0
        if(speed.x < -2.0f)
        {
            this.spriteSheetInfo[1] = 0.0f;
            this.spriteSheetInfo[2] = 0.0f;

            this.headSpriteInfo[1] = 0.5f;
            this.headSpriteInfo[2] = 0.0f;
        }
        //-1.0 to -1.0
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
            this.spriteSheetInfo[2] = 0.125f;

            this.headSpriteInfo[1] = 0.5f;
            this.headSpriteInfo[2] = 0.125f;
        }

    }
    // Receive damage
    public void TakeDamage(float dmgAmount)
    {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE); // Get vibrator unit
        // If health - damage is not below 0
        if(this.health-dmgAmount > 0)
        {
            this.health-=dmgAmount; // Subtract damage from health
            v.vibrate(50);          // Vibrate in 50 milliseconds
        }
        else
        {
            //GAMEOVER
            if(!dead)
            {
                MenuActivity.editor.putFloat("Points",Player.getInstance().points); // Set points in highscore
                MenuActivity.editor.commit();                                       // Commit changes, is read by highscore later
                Player.getInstance().points = 0;                                    // Reset points
                dead = true;                                                        // Player is dead(What is dead may never die!)
                Intent openGame = new Intent(context,HighscoreActivity.class);      // Create intent to open highscore
                context.startActivity(openGame);                                    // Open highscore
                v.vibrate(500);                                                     // Vibrate for 500 milliseconds
                ((GameActivity)context).finish();                                   // Close current activity
            }
        }
        new BloodParticle(position, 3);  // Create blood particle when hit
    }

    // When attacking
    public void Attack()
    {
        // If there is a target and attack timer is not timed out
        if (timer > 1f && closestEnemy!=null)
        {
            new Arrow(new PointF(this.position.x,this.position.y),new PointF(128,256),350,(int)damage,closestEnemy);    // Make new arrow
            timer =0;   // Reset attack timer
        }

    }
    @Override
    protected void Render(GLRenderer glRenderer)
    {
        float xScale = size.x/128f;     // Scale x in size
        float yScale = size.y/128f;     // Scale y in size
        glRenderer.RenderObject(getVertices(25.0f*xScale,-50.0f*yScale,legangles[0]),legSpriteInfo);    // Render sprite for leg 1
        glRenderer.RenderObject(getVertices(-25.0f*xScale,-50.0f*yScale,legangles[1]),legSpriteInfo);   // Render sprite for leg 2
        glRenderer.RenderObject(getVertices(0, 128, 0, (256 * health /100), 20), new float[]{0, 0.751f, 0.75f, 0.01f,0.01f}); //Healthbar

        super.Render(glRenderer);

        glRenderer.RenderObject(getVertices(speed.x*0.01f,60.0f+speed.y*0.01f,0.0f),headSpriteInfo);            // Render head
        glRenderer.RenderObject(getVertices(weaponPosition.x, weaponPosition.y,weaponAngle), weaponSpriteInfo); // Render weapon

        // If special cool down is cooling down
        if(this.currentSpecialTimer > 0)
        {
            // Render cool down bar above player
            glRenderer.RenderObject(getVertices(0, 108, 0, (256 * currentSpecialTimer / specialTimer), 20),new float[]{0, 0.77f, 0.75f, 0.01f,0.01f});
        }
    }

    // Set players health
    public void setHealth(int amount)
    {
        // If current health + amount is no more than 100
        if((this.health + amount) < 100)
        {
            this.health += amount;  // Add amount to health
        }
        else
        {
            this.health = 100;      // Set health to max(100)
        }
    }

    // Get current time from special timer
    public float getSpecialTimer()
    {
        return this.currentSpecialTimer;
    }

    // Set time on special timer
    public void setSpecialTimer(int time)
    {
        this.specialTimer = time;       // Set special time
        this.currentSpecialTimer = time;// Set current special time
    }
}
