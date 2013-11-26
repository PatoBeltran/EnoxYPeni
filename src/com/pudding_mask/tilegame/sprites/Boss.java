package com.pudding_mask.tilegame.sprites;

import com.pudding_mask.graphics.Animation;

/**
    A Boss is a boss
*/
public class Boss extends Creature {
    public int timer = 0;

    public Boss(Animation left, Animation right,
        Animation deadLeft, Animation deadRight, Animation leftJump,
         Animation rightJump,  Animation leftStill,  Animation rightStill,
          Animation leftFire,  Animation rightFire)
    {
        super(BOSS, left, right, deadLeft, deadRight, leftJump, rightJump, leftStill, rightStill, leftFire, rightFire, 24);

        exp = 5;
        isBoss = true;
        setDieTime(600);
    }

    public float getMaxSpeed() {
        return 0.05f;
    }
}


