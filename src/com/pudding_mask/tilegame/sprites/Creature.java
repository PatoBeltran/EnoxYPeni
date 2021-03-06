package com.pudding_mask.tilegame.sprites;

import java.lang.reflect.Constructor;
import com.pudding_mask.graphics.*;

/**
    A Creature is a Sprite that is affected by gravity and can
    die. It has four Animations: moving left, moving right,
    dying on the left, and dying on the right.
*/
public abstract class Creature extends Sprite {

    /**
        Amount of time to go from STATE_DYING to STATE_DEAD.
    */
    private int DIE_TIME = 1000;
    private static final int  FIRE_TIME = 600;

    public static final int STATE_NORMAL = 0;
    public static final int STATE_DYING = 1;
    public static final int STATE_DEAD = 2;

    private Animation left;
    private Animation right;
    private Animation deadLeft;
    private Animation deadRight;
    private Animation leftJump;
    private Animation rightJump;
    private Animation leftStill;
    private Animation rightStill;
    private Animation leftFire;
    private Animation rightFire;
    private Animation leftInv;
    private Animation rightInv;
    private Animation leftJumpInv;
    private Animation rightJumpInv;
    private Animation leftStillInv;
    private Animation rightStillInv;
    
    public boolean invul = false;
    public int invulTimer = 0 ;
    
    private int state;
    private long stateTime;
    private long fireTime = 0;
    public boolean dir = true;
    public int delay=0;
    private int hp;
    public int exp;
    public boolean chung=false;
    public boolean isBoss=false;
    public boolean stepping =true;
    public boolean shooting = false;
    public boolean awake = false;


    public int whoAmI;
    
    public static final int PLAYER = 1;
    public static final int CHUNGUILLO = 2;
    public static final int BADASS = 3;
    public static final int BOSS = 4;
    /**
        Creates a new Creature with the specified Animations.
    */
    public Creature(int whoami, Animation left, Animation right,
        Animation deadLeft, Animation deadRight, Animation leftJump,
         Animation rightJump,  Animation leftStill,  Animation rightStill,
          Animation leftFire,  Animation rightFire, Animation leftInv, Animation rightInv,
          Animation leftJumpInv, Animation rightJumpInv, Animation leftStillInv, 
          Animation rightStillInv, int hp)
    {
        super(right);
        whoAmI = whoami;
        this.left = left;
        this.right = right;
        this.deadLeft = deadLeft;
        this.deadRight = deadRight;
        this.leftJump = leftJump;
        this.rightJump = rightJump;
        this.leftStill = leftStill;
        this.rightStill = rightStill;
        this.leftFire = leftFire;
        this.rightFire = rightFire;
        this.leftInv = leftInv;
        this.rightInv = rightInv;
        this.leftJumpInv = leftJumpInv;
        this. rightJumpInv = rightJumpInv;
        this.leftStillInv = leftStillInv;
        this.rightStillInv = rightStillInv;
        state = STATE_NORMAL;
        this.hp = hp;
    }
    public void setDieTime(int dieTime){
        DIE_TIME = dieTime;
    }
    public void changeAnimation(Animation left, Animation right,
        Animation deadLeft, Animation deadRight, Animation leftJump,
         Animation rightJump,  Animation leftStill,  Animation rightStill,
          Animation leftFire,  Animation rightFire, Animation leftInv, Animation rightInv,
          Animation leftJumpInv, Animation rightJumpInv, Animation leftStillInv, 
          Animation rightStillInv){
        changeSprite(right);
        this.left = left;
        this.right = right;
        this.deadLeft = deadLeft;
        this.deadRight = deadRight;
        this.leftJump = leftJump;
        this.rightJump = rightJump;
        this.leftStill = leftStill;
        this.rightStill = rightStill;
        this.leftFire = leftFire;
        this.rightFire = rightFire;
        this.leftInv = leftInv;
        this.rightInv = rightInv;
        this.leftJumpInv = leftJumpInv;
        this. rightJumpInv = rightJumpInv;
        this.leftStillInv = leftStillInv;
        this.rightStillInv = rightStillInv;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(new Object[] {
                (Animation)left.clone(),
                (Animation)right.clone(),
                (Animation)deadLeft.clone(),
                (Animation)deadRight.clone(),
                (Animation)leftJump.clone(),
                (Animation)rightJump.clone(),
                (Animation)leftStill.clone(),
                (Animation)rightStill.clone(),
                (Animation)leftFire.clone(),
                (Animation)rightFire.clone(),
                (Animation)leftInv.clone(),
                (Animation)rightInv.clone(),
                (Animation)leftJumpInv.clone(),
                (Animation)rightJumpInv.clone(),
                (Animation)leftStillInv.clone(),
                (Animation)rightStillInv.clone()
            });
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }
    public void decreaseLife() {
        hp--;
    }
    public void decreaseLife(int playerLevel) {
        hp-=playerLevel;
    }
    
    public int getLife(){
        return hp;
    }


    /**
        Gets the maximum speed of this Creature.
    */
    public float getMaxSpeed() {
        return 0;
    }


    /**
        Wakes up the creature when the Creature first appears
        on screen. Normally, the creature starts moving left.
    */
    public void wakeUp() {
        if (getState() == STATE_NORMAL && !awake) {
            setVelocityX(-getMaxSpeed());
            awake = true;
        }
    }


    /**
        Gets the state of this Creature. The state is either
        STATE_NORMAL, STATE_DYING, or STATE_DEAD.
    */
    public int getState() {
        return state;
    }
    public void fire(){
       fireTime = 0;
       shooting = true;
       delay=0;
    }

    /**
        Sets the state of this Creature to STATE_NORMAL,
        STATE_DYING, or STATE_DEAD.
    */
    public void setState(int state) {
        if (this.state != state) {
            this.state = state;
            stateTime = 0;
            if (state == STATE_DYING) {
                setVelocityX(0);
                setVelocityY(0);
            }
        }
    }


    /**
        Checks if this creature is alive.
    */
    public boolean isAlive() {
        return (state == STATE_NORMAL);
    }


    /**
        Checks if this creature is flying.
    */
    public boolean isFlying() {
        return false;
    }


    /**
        Called before update() if the creature collided with a
        tile horizontally.
    */
    public void collideHorizontal() {
        setVelocityX(-getVelocityX());
    }


    /**
        Called before update() if the creature collided with a
        tile vertically.
    */
    public void collideVertical() {
        setVelocityY(0);
    }
    public void hasJump(){
          if(anim == rightStill){
            anim = rightJump;
            anim.start();
        }
        else if (anim == leftStill){
            anim = leftJump;
            anim.start();
        }
    }
    public void hasStep(){
        if(anim == rightJump){
            anim = rightStill;
            anim.start();
        }
        else if (anim == leftJump){
            anim = leftStill;
            anim.start();
        }
    }
    /**
        Updates the animaton for this creature.
    */
    public void update(long elapsedTime) {
        // select the correct Animation
        Animation newAnim = anim;
        if (getVelocityX() < 0) {
            if(invul && whoAmI == PLAYER){
                newAnim = leftInv;
            }
            else {
                newAnim = left;
            }
             dir = false;
        }
        else if (getVelocityX() > 0) {
            if(invul && whoAmI == PLAYER){
                newAnim = rightInv;
            }
            else {
                newAnim = right;
            }
            dir = true;
        }
        if (state == STATE_DYING && (newAnim == left || newAnim == leftStill || newAnim == leftFire)) {
            newAnim = deadLeft;
        }
        else if (state == STATE_DYING && (newAnim == right || newAnim == rightStill || newAnim == rightFire)) {
            newAnim = deadRight;
        }
        if(shooting && newAnim == left){
            newAnim = leftFire;
        }
        else if(shooting && newAnim == right){
            newAnim = rightFire;
        }
        switch(whoAmI){
            case PLAYER:
                if (invul){
                    if(!stepping && (newAnim == left || newAnim == leftInv || newAnim == leftJumpInv || newAnim == leftStillInv || newAnim == leftStill)){
                        newAnim = leftJumpInv;
                    }
                    else if (!stepping && (newAnim == right || newAnim == rightInv || newAnim == rightJumpInv || newAnim == rightStillInv || newAnim == rightStill)){
                        newAnim = rightJumpInv;
                    }
                    if (getVelocityX()==0 && (newAnim == left || newAnim == leftInv || newAnim == leftJumpInv || newAnim == leftStillInv || newAnim == leftStill)){
                        if(stepping){
                            newAnim = leftStillInv;
                        }
                        else {
                            newAnim = leftJumpInv;
                        }
                    }
                    else if(getVelocityX()==0 && (newAnim == right || newAnim == rightInv || newAnim == rightJumpInv || newAnim == rightStillInv || newAnim == rightStill)){
                        if(stepping){
                            newAnim = rightStillInv;  
                        }
                        else {
                            newAnim = rightJumpInv;
                        }
                    }
                }
                else {
                    if(!stepping && (newAnim == left || newAnim == leftInv || newAnim == leftJumpInv || newAnim == leftStillInv || newAnim == leftStill)){
                        newAnim = leftJump;
                    }
                    else if (!stepping && (newAnim == right || newAnim == rightInv || newAnim == rightJumpInv || newAnim == rightStillInv || newAnim == rightStill)){
                        newAnim = rightJump;
                    }
                    if (getVelocityX()==0 &&  (newAnim == left || newAnim == leftInv || newAnim == leftJumpInv || newAnim == leftStillInv || newAnim == leftStill)){
                        if(stepping){
                            newAnim = leftStill;
                        }
                        else {
                            newAnim = leftJump;
                        }
                    }
                    else if(getVelocityX()==0 && (newAnim == right || newAnim == rightInv || newAnim == rightJumpInv || newAnim == rightStillInv || newAnim == rightStill)){
                        if(stepping){
                            newAnim = rightStill;  
                        }
                        else {
                            newAnim = rightJump;
                        }
                    }
                }
                break;
            case CHUNGUILLO:
                fireTime += elapsedTime;
                 if(fireTime >= FIRE_TIME){
                     //shooting = false;
                 }                  
                break;
        }

        // update the Animation
        if (anim != newAnim) {
            anim = newAnim;
            anim.start();
        }
        else {
            anim.update(elapsedTime);
        }

        // update to "dead" state
        stateTime += elapsedTime;
        if (state == STATE_DYING && stateTime >= DIE_TIME) {
            setState(STATE_DEAD);
        }
    }

}
