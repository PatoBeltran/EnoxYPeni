package com.pudding_mask.tilegame.sprites;

import com.pudding_mask.graphics.Animation;

/**
    A Boss is a boss
*/
public class Boss extends Creature {
    public int timer = 0;

    public Boss(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight, 24);
        exp = 5;
        isBoss = true;
    }

    public float getMaxSpeed() {
        return 0.05f;
    }
}


