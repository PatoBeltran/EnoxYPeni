package com.pudding_mask.tilegame.sprites;

import com.pudding_mask.graphics.Animation;

/**
    The Player.
*/
public class Player extends Creature {

    private static final float JUMP_SPEED = -.95f;
    
    public boolean isPeni;

    private boolean onGround;
    
    private int level;
    private int experience;


    public Player(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight, 5);
        level = 1;
        isPeni = false;
    }
    
    public int getLevel(){
        return level;
    }
    private void levelUp(){
        level++;
    }
    
    public int getExp(){
        return experience;
    }
    public void earnExp(int exp){
        experience += exp;
        if(experience>=(level*2)+5){
            int aux = experience/((level*2)+5);
            for(int i = 0;i<aux;i++){
                levelUp();
            }
            experience = 0;
        }
    }
    
    


    public void collideHorizontal() {
        setVelocityX(0);
    }
    
    public void restoreLife() {
        setHp(5);
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
