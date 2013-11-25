package com.pudding_mask.tilegame.sprites;

import com.pudding_mask.graphics.Animation;

/**
    A Grub is a Creature that moves slowly on the ground.
*/
public class Chunquillo extends Creature {
    public int timer = 0;   
    public Chunquillo(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight, 12);
        exp = 1;
        chung = true;
    }

    public float getMaxSpeed() {
        return 0.05f;
    }
}

