package com.pudding_mask.tilegame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.math.*;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;

import com.pudding_mask.graphics.*;
import com.pudding_mask.sound.*;
import com.pudding_mask.input.*;
import com.pudding_mask.test.GameCore;
import com.pudding_mask.tilegame.sprites.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
    GameManager manages all parts of the game.
*/
public class GameManager extends GameCore {

    public static void main(String[] args) {
        new GameManager().run();
    }

    // uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
    private static final AudioFormat PLAYBACK_FORMAT =
        new AudioFormat(44100, 16, 1, true, false);

    private static final int DRUM_TRACK = 1;

    public static final float GRAVITY = 0.002f;
    
    private int spawnTimer = 0;
    private boolean classicControl;
    private int score = 0;
    private int highscores[] = new int[8];
    private String saveFile = "highscores.txt";
    private Point pointCache = new Point();
    private TileMap map;
    private MidiPlayer midiPlayer;
    private SoundManager soundManager;
    public ResourceManager resourceManager;
    private Sound prizeSound;
    private Sound boopSound;
    private InputManager inputManager;
    private TileMapRenderer renderer;
    private MenuManager menu;
    
    private Animation anim;

    private GameAction moveLeft;
    private GameAction moveRight;
    private GameAction jump;
    private GameAction fire;
    private GameAction exit;
    private GameAction click;
    private GameAction pause;
    
    private boolean sound;

    public void init() {
        super.init();
        for(int i = 0; i<8; i++){
            highscores[i] = 0;
        }
        try {
            loadScores();
        } catch (IOException ex) {
            //do nothing
        }
        
        //init default configurations
        sound = true;
        classicControl = true;
        
        // set up input manager
        initInput();
        
        //start menu manager
        menu = new MenuManager();
        
        // start resource manager
        resourceManager = new ResourceManager(
        screen.getFullScreenWindow().getGraphicsConfiguration());

        // load resources
        renderer = new TileMapRenderer();
        renderer.setBackground(
            resourceManager.currentMap);

        // load first map
        map = resourceManager.loadNextMap();
                resourceManager.loadBoss(1, map);

        // load sounds
        soundManager = new SoundManager(PLAYBACK_FORMAT);
        prizeSound = soundManager.getSound("sounds/prize.wav");
        boopSound = soundManager.getSound("sounds/boop2.wav");

        // start music
        midiPlayer = new MidiPlayer();
        Sequence sequence =
            midiPlayer.getSequence("sounds/music.midi");
        midiPlayer.play(sequence, true);
        toggleDrumPlayback();

    }


    /**
        Closes any resurces used by the GameManager.
    */
    public void stop() {
        super.stop();
        midiPlayer.close();
        soundManager.close();
    }
    //set sound on or off
    public void toggleSound(){
        sound = !sound;
        soundManager.setPaused(sound);
        menu.sound = this.sound;
    }


    private void initInput() {
        moveLeft = new GameAction("moveLeft");
        moveRight = new GameAction("moveRight");
        fire = new GameAction("fire",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        jump = new GameAction("jump",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        pause = new GameAction("pause",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        exit = new GameAction("exit",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        click = new GameAction("click");

        inputManager = new InputManager(
            screen.getFullScreenWindow());
        inputManager.setCursor(Cursor.getDefaultCursor());

        inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(fire, KeyEvent.VK_Z);
        inputManager.mapToKey(jump, KeyEvent.VK_SPACE);
        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
        inputManager.mapToKey(pause, KeyEvent.VK_P);
        inputManager.mapToMouse(click, inputManager.MOUSE_BUTTON_1);
    }
    //set the default controls to wasd or viceversa
    private void switchControls(){
        if(classicControl){
            inputManager.clearMap(moveLeft);
            inputManager.clearMap(moveRight);
            inputManager.clearMap(fire);
            inputManager.mapToKey(moveLeft, KeyEvent.VK_A);
            inputManager.mapToKey(moveRight, KeyEvent.VK_D);
            inputManager.mapToKey(fire, KeyEvent.VK_SHIFT);
            inputManager.mapToKey(jump, KeyEvent.VK_W);
        }
        else{
            inputManager.clearMap(moveLeft);
            inputManager.clearMap(moveRight);
            inputManager.clearMap(fire);
            inputManager.clearMap(jump);
            inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
            inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
            inputManager.mapToKey(fire, KeyEvent.VK_Z);
            inputManager.mapToKey(jump, KeyEvent.VK_SPACE);
        }
        classicControl = !classicControl;
        menu.classicControl = classicControl;
    }
    //loads score from the txt file
    void loadScores() throws IOException{
        BufferedReader fileIn;
        try {
            fileIn = new BufferedReader(new FileReader(saveFile));
            
            for(int i = 0; i<8;i++){
                highscores[i] = Integer.valueOf(fileIn.readLine());
            }
        
            fileIn.close();
        } 
        catch (FileNotFoundException e) {
            //do nothing
        }
        
    }
    //Saves best 8 scores in a txt file
    void saveScores() throws IOException{
        PrintWriter fileOut = new PrintWriter(new FileWriter(saveFile));
        for(int i = 0; i<8;i++){
            fileOut.println(highscores[i]);
        }
        fileOut.close();
    }

    private void checkInput(long elapsedTime){
        if (exit.isPressed()) {
            try {
                saveScores();
            } catch (IOException ex) {
                //cry
            }
            stop();
        }
        //update menus and check for input in them (buttons clicked)
        if(!menu.isPlaying()){
            pause.reset();
            if (menu.isInMainMenu()){
                if(click.isPressed() && inputManager.getMouseX()>= 126 && inputManager.getMouseX()<= 895){
                    if(inputManager.getMouseY()>=306 && inputManager.getMouseY() <= 360){
                        click.reset();
                        menu.goToCharChoose();
                    }
                    if(inputManager.getMouseY()>=400 && inputManager.getMouseY() <= 454){
                        menu.goToConfiguration();
                    }
                    if(inputManager.getMouseY()>=488 && inputManager.getMouseY() <= 538){
                        menu.goToHighscores();
                    }
                    if(inputManager.getMouseY()>=595 && inputManager.getMouseY() <= 647){
                        menu.goToInstructions();
                    }
                }
            }
            else if(menu.isChoosingChar()){
                if(click.isPressed()){
                    if(inputManager.getMouseX()>= 790 && inputManager.getMouseX()<= 970
                        && inputManager.getMouseY()>=690 && inputManager.getMouseY() <= 740){
                        menu.goToMainMenu();
                    }
                    if(inputManager.getMouseX()>= 570 && inputManager.getMouseX()<= 880
                        && inputManager.getMouseY()>=180 && inputManager.getMouseY() <= 656){
                        Player player = (Player)map.getPlayer();
                        player.changeAnimation(resourceManager.peniAnim[0], resourceManager.peniAnim[1], resourceManager.peniAnim[2], resourceManager.peniAnim[3], 
                                  resourceManager.peniAnim[6], resourceManager.peniAnim[7],resourceManager.peniAnim[4],resourceManager.peniAnim[5], resourceManager.emptyAnimation(), resourceManager.emptyAnimation());
                        resourceManager.isPeni = true;
                        resourceManager.boss3Sprite = resourceManager.darkPeniSprite;
                        menu.goToGame();
                    }
                    if(inputManager.getMouseX()>= 170 && inputManager.getMouseX()<= 485
                        && inputManager.getMouseY()>=180 && inputManager.getMouseY() <= 656){
                        resourceManager.boss3Sprite = resourceManager.darkEnoxSprite;
                        menu.goToGame();
                    }
                }   
            }
            else if(menu.isConfiguring()){
                if(click.isPressed()){
                    if(inputManager.getMouseX()>= 810 && inputManager.getMouseX()<= 970
                        && inputManager.getMouseY()>=690 && inputManager.getMouseY() <= 750){
                        menu.goToMainMenu();
                    }
                    if(inputManager.getMouseX()>= 450 && inputManager.getMouseX()<= 510
                        && inputManager.getMouseY()>=440 && inputManager.getMouseY() <= 530){
                        switchControls();
                        click.reset();
                    }
                    if(inputManager.getMouseX()>= 450 && inputManager.getMouseX()<= 510
                        && inputManager.getMouseY()>=270 && inputManager.getMouseY() <= 350){
                        toggleSound();
                        click.reset();
                    }
                }
            }
            else if(menu.isInHighscores()){
                if(click.isPressed()){
                    if(inputManager.getMouseX()>= 840 && inputManager.getMouseX()<= 1000
                        && inputManager.getMouseY()>=685 && inputManager.getMouseY() <= 750){
                        menu.goToMainMenu();
                    }
                }
            }
            else if(menu.isLearning()){
                if(click.isPressed()){
                    if(inputManager.getMouseX()>= 810 && inputManager.getMouseX()<= 980
                        && inputManager.getMouseY()>=672 && inputManager.getMouseY() <= 737){
                        menu.goToMainMenu();
                    }
                }   
            }
        }
        else if(menu.isPlaying()) {
            if(pause.isPressed()){
                if(menu.isPaused()){
                    fire.reset();
                    moveLeft.reset();
                    moveRight.reset();
                    jump.reset();
                }
                menu.changePauseStatus();
            }
            if(!menu.isPaused()){
                Player player = (Player)map.getPlayer();
                if (player.isAlive()) {
                    float velocityX = 0;
                    if (moveLeft.isPressed()) {
                        velocityX-=player.getMaxSpeed();
                    }
                    if (fire.isPressed()) {
                        Sprite bullet = (Sprite)resourceManager.bullet.clone();
                        bullet.setX(player.getX()+50);
                        bullet.setY(player.getY()+32);
                        bullet.isBullet = true;
                        if(player.dir){
                            bullet.setVelocityX(.7f);
                        }
                        else{
                            bullet.setX(player.getX()+5);
                            bullet.setVelocityX(-.7f);
                        }
                        map.addBullet(bullet);
                    }
                    if (moveRight.isPressed()) {
                        velocityX+=player.getMaxSpeed();
                    }
                    if (jump.isPressed()) {
                        player.jump(false);
                    }
                    player.setVelocityX(velocityX);
                }
            }
        }
    }


    public void draw(Graphics2D g)
    {
        if(!menu.isPlaying()){
          //set cursos so it can be seen
            inputManager.setCursor(Cursor.getDefaultCursor());
            
        }
        else if(menu.isPlaying()){
            //set cursor to invisible
            inputManager.setCursor(inputManager.INVISIBLE_CURSOR);
            renderer.draw(g, map,
                screen.getWidth(), screen.getHeight());
            Player player = (Player)map.getPlayer();
            
           
            //update lifes
            Image hp;
            switch(player.getLife()){
                case 0:
                    hp = resourceManager.loadImage("hp0.png");
                    g.drawImage(hp, 10, 10, null);
                    break;
                case 1:
                    hp = resourceManager.loadImage("hp1.png");
                    g.drawImage(hp, 10, 10, null);
                    break;
                case 2:
                    hp = resourceManager.loadImage("hp2.png");
                    g.drawImage(hp, 10, 10, null);
                    break;
                case 3:
                    hp = resourceManager.loadImage("hp3.png");
                    g.drawImage(hp, 10, 10, null);
                    break;
                case 4:
                    hp = resourceManager.loadImage("hp4.png");
                    g.drawImage(hp, 10, 10, null);
                    break;
                case 5:
                    hp = resourceManager.loadImage("hp5.png");
                    g.drawImage(hp, 10, 10, null);
                    break;
            }
        }
        //prints menus
        menu.draw(g, map,
            screen.getWidth(), screen.getHeight());
        //prints highscores
        if(menu.isInHighscores()){
            for(int i = 0; i<8; i++){
                g.drawString(Integer.toString(highscores[i]),420, 220+(i*55)- ((i-1)*4));
            }
        }
        else if(menu.isChoosingChar()){
          URL urlImg = ResourceManager.class.getResource("/images/choosing_enox.png");
          Image enoxImg = new ImageIcon(urlImg).getImage();
          g.drawImage(enoxImg,195,335, null);
          urlImg = ResourceManager.class.getResource("/images/choosing_peni.png");
          Image peniImg = new ImageIcon(urlImg).getImage();
          g.drawImage(peniImg,590,335, null);
        }
    }


    /**
        Gets the current map.
    */
    public TileMap getMap() {
        return map;
    }


    /**
        Turns on/off drum playback in the midi music (track 1).
    */
    public void toggleDrumPlayback() {
        Sequencer sequencer = midiPlayer.getSequencer();
        if (sequencer != null) {
            sequencer.setTrackMute(DRUM_TRACK,
                !sequencer.getTrackMute(DRUM_TRACK));
        }
    }


    /**
        Gets the tile that a Sprites collides with. Only the
        Sprite's X or Y should be changed, not both. Returns null
        if no collision is detected.
    */
    public Point getTileCollision(Sprite sprite,
        float newX, float newY)
    {
        float fromX = Math.min(sprite.getX(), newX);
        float fromY = Math.min(sprite.getY(), newY);
        float toX = Math.max(sprite.getX(), newX);
        float toY = Math.max(sprite.getY(), newY);

        // get the tile locations
        int fromTileX = TileMapRenderer.pixelsToTiles(fromX);
        int fromTileY = TileMapRenderer.pixelsToTiles(fromY);
        int toTileX = TileMapRenderer.pixelsToTiles(
            toX + sprite.getWidth() - 1);
        int toTileY = TileMapRenderer.pixelsToTiles(
            toY + sprite.getHeight() - 1);

        // check each tile for a collision
        for (int x=fromTileX; x<=toTileX; x++) {
            for (int y=fromTileY; y<=toTileY; y++) {
                if (x < 0 || x >= map.getWidth() ||
                    map.getTile(x, y) != null)
                {
                    // collision found, return the tile
                    pointCache.setLocation(x, y);
                    return pointCache;
                }
            }
        }
        // no collision found
        return null;
    }


    /**
        Checks if two Sprites collide with one another. Returns
        false if the two Sprites are the same. Returns false if
        one of the Sprites is a Creature that is not alive.
    */
    public boolean isCollision(Sprite s1, Sprite s2) {
        // if the Sprites are the same, return false
        if (s1 == s2) {
            return false;
        }

        // if one of the Sprites is a dead Creature, return false
        if (s1 instanceof Creature && !((Creature)s1).isAlive()) {
            return false;
        }
        if (s2 instanceof Creature && !((Creature)s2).isAlive()) {
            return false;
        }

        // get the pixel location of the Sprites
        int s1x = Math.round(s1.getX());
        int s1y = Math.round(s1.getY());
        int s2x = Math.round(s2.getX());
        int s2y = Math.round(s2.getY());

        // check if the two sprites' boundaries intersect
        return (s1x < s2x + s2.getWidth() &&
            s2x < s1x + s1.getWidth() &&
            s1y < s2y + s2.getHeight() &&
            s2y < s1y + s1.getHeight());
    }


    /**
        Gets the Sprite that collides with the specified Sprite,
        or null if no Sprite collides with the specified Sprite.
    */
    public Sprite getSpriteCollision(Sprite sprite) {

        // run through the list of Sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite otherSprite = (Sprite)i.next();
            if (isCollision(sprite, otherSprite)) {
                // collision found, return the Sprite
                return otherSprite;
            }
        }
        i = map.getBullets();
        while (i.hasNext()) {
            Sprite otherSprite = (Sprite)i.next();
            if (isCollision(sprite, otherSprite)) {
                // collision found, return the Sprite
                return otherSprite;
            }
        }
        
        i = map.getEnBul();
        while (i.hasNext()) {
            Sprite otherSprite = (Sprite)i.next();
            if (isCollision(sprite, otherSprite)) {
                // collision found, return the Sprite
                return otherSprite;
            }
        }
        if(map.getBoss()!=null){
            if (isCollision(sprite, map.getBoss())) {
                    // collision found, return the Sprite
                return map.getBoss();
            }
        }
        // no collision found
        return null;
    }


    /**
        Updates Animation, position, and velocity of all Sprites
        in the current map.
    */
    public void update(long elapsedTime) {
        Animation newAnim = anim;
        // get keyboard/mouse input
        checkInput(elapsedTime);

        if(menu.isPlaying() && !menu.isPaused()){
            Creature player = (Creature)map.getPlayer();
            Player playr = (Player)map.getPlayer();
            Creature boss = (Creature)map.getBoss();

            // player is dead! start map over
            if (player.getState() == Creature.STATE_DEAD) {
                boolean high = tryhigh(score);
                score=0;
                map.sprites.clear();
                map.boss = null;
                map = resourceManager.reloadMap();
                resourceManager.loadBoss(resourceManager.currentMap, map);
                ((Creature)map.boss).awake = false;
                playr.restoreLife();
                return;
            }
            
            if(playr.levelUp){
                newAnim = resourceManager.lvlUpAnim;
            }
            else if(playr.powerUp){
                newAnim = resourceManager.adquireAnim;
            }
            
            if (anim != newAnim) {
                anim = newAnim;
                anim.start();
            }
            else if(anim != null) {
                anim.update(elapsedTime);
            }
            
            //Check power up and level up animations and update them
            playr.powerUpTime += elapsedTime;
            playr.levelUpTime += elapsedTime;
            if(playr.powerUp && playr.powerUpTime >= playr.POWER_ANIMATION){
                playr.powerUp = false;
            }
            if(playr.levelUp && playr.levelUpTime >= playr.LEVEL_ANIMATION){
                playr.levelUp = false;
            }
            
            

            // update player
            updateCreature(player, elapsedTime);
            player.update(elapsedTime);
            
            //get random enemies appearing
            demEnemies(elapsedTime);
            
            //if boss is dead remove it, else update him
            if(boss!=null){
                if (boss.getState() == Creature.STATE_DEAD) {
                    map.removeBoss(boss);
                    map.removeTrap();
                }
                else {
                    updateCreature(boss, elapsedTime);
                }
            boss.update(elapsedTime);
            }

            // update other sprites
            Iterator i = map.getSprites();
            while (i.hasNext()) {
                Sprite sprite = (Sprite)i.next();
                if (sprite instanceof Creature) {
                    Creature creature = (Creature)sprite;
                    if (creature.getState() == Creature.STATE_DEAD) {
                        i.remove();
                    }
                    else {
                        updateCreature(creature, elapsedTime);
                    }
                }
                // normal update
                sprite.update(elapsedTime);
            }
            //check for bullets
            i = map.getBullets();
            while (i.hasNext()) {
                Sprite sprite = (Sprite)i.next();
                sprite.update(elapsedTime);
                Point tile =
                getTileCollision(sprite, sprite.x, sprite.y);
                if (tile != null) {
                    if (sprite.isBullet){
                        i.remove();
                        map.removeBullet(sprite);
                    }           
                }
            }
            i = map.getEnBul();
            while (i.hasNext()) {
                Sprite sprite = (Sprite)i.next();
                sprite.update(elapsedTime);
                Point tile =
                getTileCollision(sprite, sprite.x, sprite.y);
                if (tile != null) {
                    if (sprite.isEnBul){
                        i.remove();
                        map.removeEnBullet(sprite);
                    }           
                }
            }
        }
    }
    //updates highscores
    boolean tryhigh(int sc){
        boolean r=false;
        for(int i=0; i<8; i++){
            if(sc>highscores[i]){
                for(int j=7; j>i; j--){
                    highscores[j]=highscores[j-1];
                }
                highscores[i]=sc;
                return true;
            }
        }
        return false;
    }

    //get random enemies on the screen depending time
    private void demEnemies(long elapsedTime){
        int rand;
        spawnTimer+=elapsedTime;
        Player player = (Player)map.getPlayer();
        if(spawnTimer>40000/((player.getLevel()*9)-5)){
            rand = (int)(2*Math.random());
            Sprite enemy;
            if(rand==1){
                enemy = (Sprite)resourceManager.chunguilloSprite.clone();
                rand = (int)(800*Math.random());
                enemy.setX(player.getX()-400+rand);
                enemy.setY(-100);
                map.addSprite(enemy);
            }
            else{
                enemy = (Sprite)resourceManager.badassSprite.clone();
                rand = (int)(800*Math.random());
                enemy.setX(player.getX()-400+rand);
                enemy.setY(-100);
                map.addSprite(enemy);
            }
            spawnTimer = spawnTimer%(40000/((player.getLevel()*9)-5));
            rand = (int)(2000*Math.random());
            spawnTimer-=rand;
        }
    }
    /**
        Updates the creature, applying gravity for creatures that
        aren't flying, and checks collisions.
    */
    private void updateCreature(Creature creature,
        long elapsedTime)
    {

        // apply gravity
        if (!creature.isFlying()) {
            creature.setVelocityY(creature.getVelocityY() +
                GRAVITY * elapsedTime);
        }

        // change x
        float dx = creature.getVelocityX();
        float oldX = creature.getX();
        float newX = oldX + dx * elapsedTime;
        Point tile =
            getTileCollision(creature, newX, creature.getY());
        if (tile == null) {
            creature.setX(newX);
        }
        else {
            // line up with the tile boundary
            if (dx > 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x) -
                    creature.getWidth());
            }
            else if (dx < 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x + 1));
            }
            creature.collideHorizontal();
        }
        if (creature instanceof Player) {
            if(((Player)creature).invul){
                if (((Player)creature).invulTimer>1000){
                    ((Player)creature).invulTimer = 0;
                    ((Player)creature).invul = false;
                }
                else{
                    ((Player)creature).invulTimer += elapsedTime;
                }
            }
            checkPlayerCollision((Player)creature, false);
        }

        // change y
        float dy = creature.getVelocityY();
        float oldY = creature.getY();
        float newY = oldY + dy * elapsedTime;
        tile = getTileCollision(creature, creature.getX(), newY);
        if (tile == null) {
            creature.setY(newY);
        }
        else {
            // line up with the tile boundary
            if (dy > 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y) -
                    creature.getHeight());
            }
            else if (dy < 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y + 1));
            }
            creature.collideVertical();
        }
        if (creature instanceof Player) {
            boolean canKill = (oldY < creature.getY());
            checkPlayerCollision((Player)creature, canKill);
        }
        else if(creature.getState()== Creature.STATE_NORMAL){
            checkBulletCollision(creature);
        }
        if (creature instanceof Creature){
            if(creature.chung&&creature.awake){
                if( (((Chunquillo)creature).timer>500) && !(((Chunquillo)creature).shooting) ){
                    ((Chunquillo)creature).fire();
                }
                else{                    
                    if(((Chunquillo)creature).delay>450 && ((Chunquillo)creature).timer>500){
                        Sprite bullet = (Sprite)resourceManager.bulletB.clone();
                        bullet.setX(creature.getX()+64);
                        bullet.setY(creature.getY()+16);
                        bullet.isEnBul = true;
                        if(creature.dir){
                            bullet.setVelocityX(.5f);
                        }
                        else{
                            bullet.setX(creature.getX());
                            bullet.setVelocityX(-.5f);
                        }
                        map.addEnBullet(bullet);
                        ((Chunquillo)creature).delay %= 450;
                        ((Chunquillo)creature).timer =((Chunquillo)creature).timer%500;
                        int rand = (int)(2000*Math.random());
                        ((Chunquillo)creature).timer-=rand;
                        ((Chunquillo)creature).shooting=false;
                    }
                    else{
                        ((Chunquillo)creature).delay += elapsedTime;
                        ((Chunquillo)creature).timer += elapsedTime;
                    }
                }
            }
            if(creature.isBoss){
                if((((Boss)creature).timer>2000)&&creature.awake) {
                    Sprite enemy = (Sprite)resourceManager.chunguilloSprite.clone();
                    enemy.setX(creature.getX()+100);
                    enemy.setY(creature.getY());
                    map.addSprite(enemy);
                    enemy = (Sprite)resourceManager.badassSprite.clone();
                    enemy.setX(creature.getX()-100);
                    enemy.setY(creature.getY());
                    map.addSprite(enemy);
                    ((Boss)creature).timer =((Boss)creature).timer%200;
                    int rand = (int)(2000*Math.random());
                    ((Boss)creature).timer-=rand;
                }
                else{
                    ((Boss)creature).timer += elapsedTime;
                }
            }
        }
    }


    /**
        Checks for Player collision with other Sprites. If
        canKill is true, collisions with Creatures will kill
        them.
    */
    public void checkPlayerCollision(Player player, boolean canKill)
    {        
        if (!player.isAlive()) {
            return;
        }
        // check for player collision with other sprites
        Sprite collisionSprite = getSpriteCollision(player);
        if (collisionSprite instanceof PowerUp) {
            acquirePowerUp((PowerUp)collisionSprite);
        }
        else if(collisionSprite == null){}
        else if (collisionSprite.isEnBul) {
            if(player.invul==false){
                player.invul = true;
                player.invulTimer = 0;
                map.removeEnBullet(collisionSprite);
                if(player.getLife()==1){
                    // player dies!
                    player.decreaseLife();
                    player.setState(Creature.STATE_DYING);

                }
                else { 
                    player.decreaseLife();
                }
            }
        }
        else if (collisionSprite instanceof Creature) {
            Creature badguy = (Creature)collisionSprite;
            if (canKill&&(player.getY()<badguy.getY() - player.getHeight()+20)&&!(badguy.isBoss)) {
                // kill the badguy and make player bounce
                soundManager.play(boopSound);
                if(badguy.getLife()<=3*player.getLevel()){
                    player.earnExp(badguy.exp);
                    badguy.setState(Creature.STATE_DYING);
                    score+=badguy.exp;
                }
                else {
                    badguy.decreaseLife(3*player.getLevel());
                }
                    player.jump(true);
            }
            else if(!canKill&&(player.getY()>badguy.getY() - player.getHeight()+20)){
                if(player.invul==false){
                    player.invul = true;
                    player.invulTimer = 0;
                    if(player.getLife()==1){
                        // player dies!
                        player.decreaseLife();
                        player.setState(Creature.STATE_DYING);

                    }
                    else { 
                        player.decreaseLife();
                    }
                }
            }
        }
    }

    //checks bullet collision with creature
    public void checkBulletCollision(Creature creature){
        Sprite collisionSprite = getSpriteCollision(creature);
        Player player = (Player)map.getPlayer();
        if(collisionSprite != null){
            if(collisionSprite.isBullet) {
                creature.decreaseLife(player.getLevel());
                 if(creature.getLife()<=0){
                    player.earnExp(creature.exp);
                    creature.setState(Creature.STATE_DYING);
                    score+=creature.exp;
                }
                map.removeBullet(collisionSprite);
            }
        }
    }

    /**
        Gives the player the speicifed power up and removes it
        from the map.
    */
     
    public void acquirePowerUp(PowerUp powerUp) {
        // remove it from the map
        map.removeSprite(powerUp);
        Player player = (Player)map.getPlayer();
        if (powerUp instanceof PowerUp.Heal) {
            player.gotPowerUp();
            player.restoreLife();
            soundManager.play(prizeSound);
        }
        else if (powerUp instanceof PowerUp.Power) {
            // change the music
            player.gotPowerUp();
            player.moreSpeed();
            soundManager.play(prizeSound);
        }
        else if (powerUp instanceof PowerUp.Goal) {
            // advance to next map
            soundManager.play(prizeSound,
                new EchoFilter(2000, .7f), false);
            map = resourceManager.loadNextMap();
            renderer.setBackground(
            resourceManager.currentMap);
            resourceManager.loadBoss(resourceManager.currentMap, map);
        }
         else if (powerUp instanceof PowerUp.KillerTile) {
            // kill player
            player.decreaseLife(player.getLife());
            player.setState(player.STATE_DYING);
        }
    }

}
