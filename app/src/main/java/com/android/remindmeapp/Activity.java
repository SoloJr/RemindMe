package com.android.remindmeapp;

/**
 * Created by Bianca on 01.02.2018.
 */

public class Activity {

    private int position;
    private String name;
    private int intervalMinutes;

    public Activity(String name, int intervalMinutes)
    {
        this.name = name;
        this.intervalMinutes = intervalMinutes * 1000 * 60;
        this.position = -1;
    }

    public int getPosition() {return this.position;}

    public void setPosition(int position) {this.position = position;}

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getIntervalMinutes()
    {
        return this.intervalMinutes;
    }

    public void setIntervalMinutes(int intervalMinutes)
    {
        this.intervalMinutes = intervalMinutes  * 1000 * 60;
    }

    @Override
    public String toString() {
        return name + "-" + (intervalMinutes / 1000 / 60);
    }
}
