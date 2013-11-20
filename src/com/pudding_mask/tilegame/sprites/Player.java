package com.pudding_mask.tilegame.sprites;

import com.pudding_mask.graphics.Animation;

/**
    The Player.
*/
public class Player extends Creature {

    private static final float JUMP_SPEED = -.95f;

    private boolean onGround;
    
    private int hp;

    public Player(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
        hp = 5;
    }


    public void collideHorizontal() {
        setVelocityX(0);
    }
    
    public void decreaseLife() {
        hp--;
    }
    public void restoreLife() {
        hp = 5;
    }
    
    public int getLife(){
        return hp;
    }


    public void collideVertical() {
        // check if collided with ground
        if (getVelocityY() > 0) {
            onGround = true;
        }
        setVelocityY(0);
    }


    public void setY(float y) {
        // check if falling
        if (Math.round(y) > Math.round(getY())) {
            onGround = false;
        }
        super.setY(y);
    }


    public void wakeUp() {
        // do nothing
    }


    /**
        Makes the player jump if the player is on the ground or
        if forceJump is true.
    */
    public void jump(boolean forceJump) {
        if (onGround || forceJump) {
            onGround = false;
            setVelocityY(JUMP_SPEED);
        }
    }
    public void fire(){
        
    }


    public float getMaxSpeed() {
        return 0.5f;
    }

}
