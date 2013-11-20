package com.pudding_mask.tilegame.sprites;

import com.pudding_mask.graphics.Animation;

/**
    A Grub is a Creature that moves slowly on the ground.
*/
public class Badas extends Creature {
    
    private int hp;

    public Badas(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight, 2);
    }


    public float getMaxSpeed() {
        return 0.2f;
    }

}
