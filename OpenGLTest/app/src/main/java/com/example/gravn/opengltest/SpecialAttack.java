package com.example.gravn.opengltest;

import android.content.Context;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;
import android.gesture.Gesture;
import android.widget.Toast;

import java.nio.charset.Charset;

import javax.net.ssl.SSLProtocolException;

/**
 * Created by Bibby on 19-05-2016.
 */
public class SpecialAttack extends GestureControl
{
    private Context context;
    private Player player;
    private PointF enemyPosition;

    public SpecialAttack(Context context)
    {
        this.context = context;             // The activity
        this.player = GameManager.player;   // Point to player
    }

    @Override
    public boolean onSwipe(final Direction direction)
    {
        SpecialAttack(direction);           // Run special attack on swipe
        return super.onSwipe(direction);
    }

    private void SpecialAttack(Direction d)
    {
        CharSequence text = "No Target";        // Default text
        if(this.player.getSpecialTimer() <=0)
        {
            switch (d)  // Switch between actions based on swipe direction
            {
                case down:
                    MasterArrow(12);            // Call Master Arrow
                    text = "Master Arrow(Down)"; // Set feedback text to "Master Arrow"
                    this.setTimer(10);          // Set cool down timer to 10 seconds
                    break;
                case up:
                    Heal(50);                   // Heal player 50 hp
                    text = "Heal(Up)";          // Set feedback text to "Heal"
                    this.setTimer(20);           // Set cool down timer to 2 seconds
                    break;
                case right:
                    if (this.player.closestEnemy != null)
                    {
                        this.enemyPosition = this.player.closestEnemy.position;
                        Shotgun(7, 60f);                // Call Shotgun :P
                        text = "Multi Arrows(Right)";   // Set feedback text to "Multi Arrows"
                        this.setTimer(15);              // Set cool down timer to 15 seconds
                    }
                    break;
                case left:
                    ArrowStorm(36, 360f);       // Call Arrow Storm
                    text = "Arrow Storm(Left)"; // Set feedback text to "Arrow Storm"
                    this.setTimer(30);          // Set cool down timer to 30 seconds
                    break;
            }
        }
        else
        {
            text = "Cooling down";  // Set feedback text to "cooling down"
        }

        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);    // Feedback toast
        toast.show();   // Show toast
    }

    // Shoot a line of arrows in the direction the player's heading
    private void MasterArrow(int amount)
    {
        Arrow[] superArrows = new Arrow[amount];    // Set amount of arrows
        for (int i = 0; i < superArrows.length; i++)
        {
            // Get target position
            PointF targetpos = new PointF(GameManager.player.position.x+GameManager.player.speed.x,GameManager.player.position.y+GameManager.player.speed.y);
            // Instantiate current arrow
            superArrows[i] = new Arrow(new PointF(this.player.position.x, this.player.position.y), new PointF(128,128), 100+30f*i, 20, 0,targetpos);
            // Alter direction on current arrow to match target position
            superArrows[i].setDirection(targetpos);
        }

    }

    // Heal player
    private void Heal(int amount)
    {
        //new PointF(player.position.x, player.position.y)
        this.player.setHealth(amount);
        //new Particle(player.position, 7, new PointF(40, 15), 1.5f, 1f, new float[]{0, 0.751f, 0.75f, 0.01f,0.01f}, 110, 15, false, 140, 120, true);
        //new Particle(player.position, 5, new PointF(35, 10), 2f, 5f, new float[]{1, 0.75f, 0, 0.1f, 1}, 15, true, 120, 120);
        new HealParticle(player.position);
    }
    // Spray a huge amount of arrows towards closest enemy
    private void Shotgun(int amount, float span)
    {
        Arrow[] arrows = new Arrow[amount]; // Set amount of arrows
        for(int i = 0; i < amount; i++)
        {
            float x, y; // Prepare x & y
            // Set angle between closest enemy and player
            double a = this.player.GetAngleBetweenPointsDeg(this.player.position, this.enemyPosition);

            float offset = -(span * .5f) + (span / amount) * i; // Set individual direction for arrows

            x = (float)(this.player.position.x + Math.cos(RadToDeg(a + offset)));   // New X
            y = (float)(this.player.position.y + Math.sin(RadToDeg(a + offset)));   // New Y

            PointF dir = new PointF(x, y);  // Set new direction

            // Instantiate current arrow
            arrows[i] = new Arrow(new PointF(this.player.position.x,this.player.position.y),new PointF(128,256),350,40, this.player.closestEnemy);
            arrows[i].setDirection(dir);    // Alter direction of current arrow to match the new target
        }
    }

    // Spray an huge amount of arrows in a arc (mostly 360
    private void ArrowStorm(int amount, float span)
    {
        Arrow[] arrows = new Arrow[amount]; // Set amount of arrows
        for(int i = 0; i < amount; i++)
        {
            float x, y; // Prepare x & y

            float offset = -(span * .5f) + (span / amount) * i; // Set individual direction for arrows

            x = (float)(this.player.position.x + 1f * Math.cos(RadToDeg(offset)));   // New X
            y = (float)(this.player.position.y + 1f * Math.sin(RadToDeg(offset)));   // New Y

            PointF targetPoint = new PointF(x, y);  // Set new direction

            // Instantiate current arrow
            arrows[i] = new Arrow(new PointF(this.player.position.x,this.player.position.y),new PointF(128,256),350,40,0,targetPoint);
            arrows[i].setDirection(targetPoint);    // Alter direction for current arrow to match new direction
        }
    }

    // Calculate a number from Radian to Degree
    private double RadToDeg(double d)
    {
        return d * Math.PI/180; // Radian to Degree formula
    }

    // Set timer of special action cool down
    private void setTimer(int time)
    {
        this.player.setSpecialTimer(time);  // Set special timer on player
    }
}
