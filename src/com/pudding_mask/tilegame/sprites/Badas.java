package com.pudding_mask.tilegame.sprites;

import com.pudding_mask.graphics.Animation;

/**
    A Badass is a Creature that moves fastly on the ground.
*/
public class Badas extends Creature {
    public Badas(Animation left, Animation right,
        Animation deadLeft, Animation deadRight, Animation leftJump,
         Animation rightJump,  Animation leftStill,  Animation rightStill,
          Animation leftFire,  Animation rightFire, Animation leftInv, Animation rightInv,
          Animation leftJumpInv, Animation rightJumpInv, Animation leftStillInv, Animation rightStillInv)
    {
        super(BADASS, left, right, deadLeft, deadRight, leftJump, rightJump, leftStill, rightStill, leftFire, rightFire, leftInv, rightInv, leftJumpInv, rightJumpInv, leftStillInv, rightStillInv, 17);

        exp = 2;
        setDieTime(900);
    }


    public float getMaxSpeed() {
        return 0.2f;
    }

}
