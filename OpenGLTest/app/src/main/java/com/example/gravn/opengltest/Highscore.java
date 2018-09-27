package com.example.gravn.opengltest;

/**
 * Created by Mathiaspc on 16/05/2016.
 */
public class Highscore
{
    private int _id;        // ID in database
    private String score;   // Score to be saved
    private String name;    // Name to be saved

    public Highscore()
    {

    }

    public Highscore(String name, String score)
    {
        this.name = name;   // Set name
        this.score = score; // Set score
    }

    public String getName()
    {
        return name;        // Get name
    }

    public void setName(String name)
    {
        this.name = name;   // Set or override name
    }

    public int get_id()
    {
        return _id;         // Get ID
    }

    public void set_id(int _id)
    {
        this._id = _id;     // Set or override ID
    }

    public String getScore()
    {
        return score;       // Get score
    }

    public void setScore(String score)
    {
        this.score = score; // Set or override score
    }
}
