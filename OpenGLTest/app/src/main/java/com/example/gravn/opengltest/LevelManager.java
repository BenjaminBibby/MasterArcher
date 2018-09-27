package com.example.gravn.opengltest;

import android.graphics.PointF;
import android.util.Log;

import java.util.Random;

/**
 * Created by Mathiaspc on 19/05/2016.
 */
public class LevelManager
{
    public int enemiesToKill;
    public float spawnTime;
    private float healthIncreaser;
    private float damageIncreaser;
    private float rangeIncreaser;
    private float cooldownIncreaser;
    private float pointsToGiveIncreaser;
    private int tmpEnemiesToKill;
    private int spawnAmount;
    private int enemiesToKillIncreaser;
    private int bossLevel;
    private float timer;
    private int enemyDmg;
    private int enemyHealth;
    private int meleeEnemyRange;
    private int rangedEnemyRange;
    private int enemyCD;
    private float pointsToGiveMelee;
    private float pointsToGiveRanged;
    private float pointsToGiveBoss;

    public LevelManager(int enemiesToKill,float spawnTime,float healthIncreaser,
                        float damageIncreaser, float rangeIncreaser,
                        float cooldownIncreaser, int enemiesToKillIncreaser,float pointsToGiveIncreaser)
    {
        this.enemiesToKill = enemiesToKill;                     // Set the amount of enemies to kill before boss fight
        this.spawnTime = spawnTime;                             // Time between spawned enemies
        this.tmpEnemiesToKill = enemiesToKill;                  // Temporarily count of enemies to kill
        this.healthIncreaser = healthIncreaser;                 // Factor for increasing enemies health each level
        this.damageIncreaser = damageIncreaser;                 // Factor for increasing enemies damage each level
        this.rangeIncreaser = rangeIncreaser;                   // Factor for increasing enemies range each level
        this.cooldownIncreaser = cooldownIncreaser;             // Factor for increasing cool down
        this.enemiesToKillIncreaser = enemiesToKillIncreaser;   // Increase the amount of enemies to kill each level before boss fight
        this.pointsToGiveIncreaser = pointsToGiveIncreaser;     // Increase the amount of points earned when killing enemies
        this.spawnAmount = 0;                                   // Amount enemies spawned
        this.enemyHealth = 100;                                 // Set base health on enemies
        this.enemyDmg = 5;                                      // Set base damage on enemies
        this.rangedEnemyRange = 800;                            // Set base range on ranged enemies
        this.meleeEnemyRange = 200;                             // Set base range on melee enemies
        this.enemyCD = 200;                                     // Set base cool down on enemies
        this.pointsToGiveMelee = 15;                            // Additional points added to melee enemy each level
        this.pointsToGiveRanged = 13;                           // Additional points added to ranged enemy each level
        this.pointsToGiveBoss = 50;                             // Additional points added to boss each level
    }

    // Increase difficulty each level
    public void IncreaseDifficulty()
    {
        enemyDmg*=damageIncreaser;                      // Apply damage increase to enemy
        enemyHealth*=healthIncreaser;                   // Apply health increase to enemy
        meleeEnemyRange*=rangeIncreaser;                // Apply range increase to melee enemy
        rangedEnemyRange*=rangeIncreaser;               // Apply range increase to ranged enemy
        enemyCD*=cooldownIncreaser;                     // Apply cool down factor to enemy
        enemiesToKill = tmpEnemiesToKill;               // Overrides enemies to kill
        enemiesToKill += enemiesToKillIncreaser;        // Add more enemies to kill
        tmpEnemiesToKill = enemiesToKill;               // Override temporary count of enemies to kill
        pointsToGiveRanged *= pointsToGiveIncreaser;    // Apply point increase on ranged enemies
        pointsToGiveMelee *= pointsToGiveIncreaser;     // Apply point increase on melee enemies
        pointsToGiveBoss *= pointsToGiveIncreaser;      // Apply point increase on boss
    }

    // Spawn new wave of enemies
    public void SpawnEnemies(float deltaTime)
    {
        // Level state
        if(bossLevel != 2)
        {
            // As long as there is enemies
            if (enemiesToKill > 0)
            {
                bossLevel = 0;  // No boss when there is still enemies
            }
            else
            {
                bossLevel = 1;  // Instantiate boss
            }
        }
        // Switch between boss state or enemy wave state
        switch (bossLevel)
        {
            case 0: // Wave
                timer+=deltaTime;   // Spawn timer

                // Spawn enemies
                if (timer > spawnTime && spawnAmount < tmpEnemiesToKill)
                {
                    Random rnd = new Random();
                    // Make random position for enemy
                    PointF randomPos = new PointF((float)Math.sin(rnd.nextDouble()*360)*1180,(float)Math.cos(rnd.nextDouble()*360)*2048);

                    // Switch between melee or ranged enemy
                    switch (rnd.nextInt(2))
                    {
                        case 0:
                            // Instantiate ranged enemy
                            new RangedEnemy(randomPos, new PointF(96, 96),enemyHealth,enemyDmg,rangedEnemyRange,enemyCD,pointsToGiveRanged, GameManager.player);
                            break;
                        case 1:
                            // Instantiate melee enemy
                            new MeleeEnemy(randomPos, new PointF(128,128),enemyHealth,enemyDmg,meleeEnemyRange,enemyCD,pointsToGiveMelee, GameManager.player);
                            break;
                        default:
                            break;
                    }
                    timer = 0;      // Reset timer
                    spawnAmount++;  // Count amount of enemies spawned
                }
                break;
            case 1: // Boss
                // Istantiate boss
                new Boss(new PointF(0,4500),new PointF(265,256),10*enemyHealth,enemyDmg,0,5,pointsToGiveBoss,GameManager.player);
                bossLevel = 2;  // Set boss level to 2, so the state is neither Wave or Boss
                break;
            default:
                break;
        }
    }

    // When killing an enenmy
    public void KillEnemy()
    {
        enemiesToKill--;    // Decrease amount of enemies to kill
    }

    // When ending the level
    public void EndLevel()
    {
        IncreaseDifficulty();   // Increase difficulty for next wave/boss
        bossLevel = 0;          // Reset level state
        spawnAmount = 0;        // Reset amount of spawned enemies
    }

    // Update every frame. Called separately from object list in GameManager
    public void Update(float deltaTime)
    {
        SpawnEnemies(deltaTime);    // Spawn enemies
    }

}
