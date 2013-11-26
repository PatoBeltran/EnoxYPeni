package com.pudding_mask.tilegame.sprites;

import java.lang.reflect.Constructor;
import com.pudding_mask.graphics.*;

/**
    A PowerUp class is a Sprite that the player can pick up.
*/
public abstract class PowerUp extends Sprite {

    public PowerUp(Animation anim) {
        super(anim);
    }


    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(
                new Object[] {(Animation)anim.clone()});
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }


    
    // Killer tile kills player
    public static class KillerTile extends PowerUp {
        public KillerTile(Animation anim) {
            super(anim);
        }
    }


    /**
        A Music PowerUp. Changes the game music.
    */
    public static class Heal extends PowerUp {
        public Heal(Animation anim) {
            super(anim);
        }
    }
    public static class Power extends PowerUp {
        public Power(Animation anim) {
            super(anim);
        }
    }


    /**
        A Goal PowerUp. Advances to the next map.
    */
    public static class Goal extends PowerUp {
        public Goal(Animation anim) {
            super(anim);
        }
    }
}
