package com.pudding_mask.tilegame.sprites;

import com.pudding_mask.graphics.Animation;

/**
    The Player.
*/
public class Player extends Creature {

    private static final float JUMP_SPEED = -.95f;
    public static final int POWER_ANIMATION = 800;
    public static final int LEVEL_ANIMATION = 2300;
    
    public boolean isPeni;

    private boolean onGround;
    
    private int level;
    private int experience;
    private float max_speed;
    public boolean powerUp = false;
    public long powerUpTime = 0;
    
    public boolean levelUp = false;
    public long levelUpTime = 0;
    
    


    public Player(Animation left, Animation right,
        Animation deadLeft, Animation deadRight, Animation leftJump,
         Animation rightJump,  Animation leftStill,  Animation rightStill,
          Animation leftFire,  Animation rightFire)
    {
        super(PLAYER, left, right, deadLeft, deadRight, leftJump, rightJump, leftStill, rightStill, leftFire, rightFire, 5);
        level = 1;
        isPeni = false;
        setDieTime(1100);
        max_speed = 0.5f;
    }
    
    public int getLevel(){
        return level;
    }
    private void levelUp(){
        level++;
        levelUp = true;
        levelUpTime = 0;
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
    

    public void gotPowerUp(){
        powerUp = true;
        powerUpTime = 0;
    }
    public void collideVertical() {
        // check if collided with ground
        if (getVelocityY() > 0) {
            onGround = true;
            stepping = true;
            hasStep();
        }
        setVelocityY(0);
    }


    public void setY(float y) {
        // check if falling
        if (Math.round(y) > Math.round(getY())) {
            onGround = false;
            stepping = false;
            hasJump();
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
            stepping = false;
            hasJump();
            setVelocityY(JUMP_SPEED);
        }
    }

    public void moreSpeed(){
        max_speed += 0.1f;
    }
    public float getMaxSpeed() {
        return max_speed;
    }

}
