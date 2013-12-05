package com.pudding_mask.tilegame.sprites;

import com.pudding_mask.graphics.Animation;

/**
    A Chunquillo is a Creature that moves slowly on the ground and shoots.
*/
public class Chunquillo extends Creature {
    public int timer = 0;   
    public Chunquillo(Animation left, Animation right,
        Animation deadLeft, Animation deadRight, Animation leftJump,
         Animation rightJump,  Animation leftStill,  Animation rightStill,
          Animation leftFire,  Animation rightFire, Animation leftInv, Animation rightInv,
          Animation leftJumpInv, Animation rightJumpInv, Animation leftStillInv, Animation rightStillInv)
    {
        super(CHUNGUILLO, left, right, deadLeft, deadRight, leftJump, rightJump, leftStill, rightStill, leftFire, rightFire, leftInv, rightInv, leftJumpInv, rightJumpInv, leftStillInv, rightStillInv, 12);

        exp = 1;
        chung = true;
        setDieTime(600);
    }

    public float getMaxSpeed() {
        return 0.05f;
    }
}

