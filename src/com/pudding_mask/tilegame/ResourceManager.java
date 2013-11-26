package com.pudding_mask.tilegame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;

import com.pudding_mask.graphics.*;
import com.pudding_mask.tilegame.sprites.*;


/**
    The ResourceManager class loads and manages tile Images and
    "host" Sprites used in the game. Game Sprites are cloned from
    "host" Sprites.
*/
public class ResourceManager {

    private ArrayList tiles;
    private int currentMap;
    private GraphicsConfiguration gc;

    // host sprites used for cloning
    private Sprite playerSprite;
    private Sprite peniSprite;
    private Sprite musicSprite;
    private Sprite coinSprite;
    private Sprite goalSprite;
    private Sprite bossSprite;
    public Sprite badassSprite;
    public Sprite chunguilloSprite;
    public Sprite bullet;
    public Sprite bulletB;
    public Sprite killerTile;
    public Animation[] peniAnim = new Animation[8];
    public boolean isPeni;

    /**
        Creates a new ResourceManager with the specified
        GraphicsConfiguration.
    */
    public ResourceManager(GraphicsConfiguration gc) {
        this.gc = gc;
        loadTileImages();
        loadCreatureSprites();
        loadPowerUpSprites();
    }


    /**
        Gets an image from the images/ directory.
    */
    public Image loadImage(String name) {
        String filename = "images/" + name;
        return new ImageIcon(filename).getImage();
    }


    public Image getMirrorImage(Image image) {
        return getScaledImage(image, -1, 1);
    }


    public Image getFlippedImage(Image image) {
        return getScaledImage(image, 1, -1);
    }


    private Image getScaledImage(Image image, float x, float y) {

        // set up the transform
        AffineTransform transform = new AffineTransform();
        transform.scale(x, y);
        transform.translate(
            (x-1) * image.getWidth(null) / 2,
            (y-1) * image.getHeight(null) / 2);

        // create a transparent (not translucent) image
        Image newImage = gc.createCompatibleImage(
            image.getWidth(null),
            image.getHeight(null),
            Transparency.BITMASK);

        // draw the transformed image
        Graphics2D g = (Graphics2D)newImage.getGraphics();
        g.drawImage(image, transform, null);
        g.dispose();

        return newImage;
    }


    public TileMap loadNextMap() {
        TileMap map = null;
        while (map == null) {
            currentMap++;
            try {
                map = loadMap(
                    "maps/map" + currentMap + ".txt");
            }
            catch (IOException ex) {
                if (currentMap == 1) {
                    // no maps to load!
                    return null;
                }
                currentMap = 0;
                map = null;
            }
        }

        return map;
    }


    public TileMap reloadMap() {
        try {
            return loadMap(
                "maps/map" + currentMap + ".txt");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private TileMap loadMap(String filename)
        throws IOException
    {
        ArrayList lines = new ArrayList();
        int width = 0;
        int height = 0;

        // read every line in the text file into the list
        BufferedReader reader = new BufferedReader(
            new FileReader(filename));
        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            // add every line except for comments
            if (!line.startsWith("#")) {
                lines.add(line);
                width = Math.max(width, line.length());
            }
        }

        // parse the lines to create a TileEngine
        height = lines.size();
        TileMap newMap = new TileMap(width, height);
        for (int y=0; y<height; y++) {
            String line = (String)lines.get(y);
            for (int x=0; x<line.length(); x++) {
                char ch = line.charAt(x);

                // check if the char represents tile A, B, C etc.
                int tile = ch - 'A';
                if (tile >= 0 && tile < tiles.size()) {
                    newMap.setTile(x, y, (Image)tiles.get(tile));
                }

                // check if the char represents a sprite
                else if (ch == 'o') {
                    addSprite(newMap, coinSprite, x, y);
                }
                else if (ch == '!') {
                    addSprite(newMap, musicSprite, x, y);
                }
                else if (ch == '*') {
                    addSprite(newMap, goalSprite, x, y);
                }
                else if (ch == '1') {
                    addSprite(newMap, chunguilloSprite, x, y);
                }
                else if (ch == '2') {
                    addSprite(newMap, badassSprite, x, y);
                }
                else if (ch == '3') {
                    Sprite boss = (Sprite)bossSprite.clone();
                    boss.setX(TileMapRenderer.tilesToPixels(x));
                    boss.setY(y);
                    newMap.addBoss(boss);
                }
                else if (ch == 'T'){
                    addSprite(newMap, killerTile, x, y);
                }
            }
        }

        // add the player to the map
        Sprite player;
        if(isPeni){
            player = (Sprite)peniSprite.clone();
        }
        else {
             player = (Sprite)playerSprite.clone();
        }
        player.setX(TileMapRenderer.tilesToPixels(3));
        player.setY(0);
        newMap.setPlayer(player);
        
        return newMap;
    }


    private void addSprite(TileMap map,
        Sprite hostSprite, int tileX, int tileY)
    {
        if (hostSprite != null) {
            // clone the sprite from the "host"
            Sprite sprite = (Sprite)hostSprite.clone();

            // center the sprite
            sprite.setX(
                TileMapRenderer.tilesToPixels(tileX) +
                (TileMapRenderer.tilesToPixels(1) -
                sprite.getWidth()) / 2);

            // bottom-justify the sprite
            sprite.setY(
                TileMapRenderer.tilesToPixels(tileY + 1) -
                sprite.getHeight());

            // add it to the map
            map.addSprite(sprite);
        }
    }


    // -----------------------------------------------------------
    // code for loading sprites and images
    // -----------------------------------------------------------


    public void loadTileImages() {
        // keep looking for tile A,B,C, etc. this makes it
        // easy to drop new tiles in the images/ directory
        tiles = new ArrayList();
        char ch = 'A';
        while (true) {
            String name = "tile_" + ch + ".png";
            File file = new File("images/" + name);
            if (!file.exists()) {
                break;
            }
            tiles.add(loadImage(name));
            ch++;
        }
    }


    public void loadCreatureSprites() {

        Image[][] images = new Image[2][];

        // load left-facing images
        images[0] = new Image[] {
            loadImage("enox1.png"), //0
            loadImage("enox2.png"),
            loadImage("enox3.png"),
            loadImage("enox4.png"),
            loadImage("enox5.png"),
            loadImage("enox6.png"),
            loadImage("enox7.png"),
            loadImage("enox8.png"), 
            loadImage("enox_jump1.png"), //8
            loadImage("enox_still1.png"), //9
            loadImage("enox_still2.png"),
            loadImage("enox_still3.png"),
            loadImage("enox_still4.png"),
            loadImage("enox_dead1.png"),//13
            loadImage("enox_dead1.png"),
            loadImage("enox_dead2.png"),
            loadImage("enox_dead2.png"),
            loadImage("enox_dead3.png"),
            loadImage("enox_dead3.png"),
            loadImage("enox_dead3.png"),
            loadImage("enox_dead3.png"),
            loadImage("peni1.png"), //21
            loadImage("peni2.png"),
            loadImage("peni3.png"),
            loadImage("peni4.png"),
            loadImage("peni5.png"),
            loadImage("peni6.png"),
            loadImage("peni7.png"),
            loadImage("peni8.png"),
            loadImage("peni_jump1.png"), //29
            loadImage("peni_still1.png"), //30
            loadImage("peni_still2.png"),
            loadImage("peni_still3.png"),
            loadImage("peni_still4.png"),
            loadImage("peni_dead1.png"), //34
            loadImage("peni_dead1.png"),
            loadImage("peni_dead2.png"),
            loadImage("peni_dead2.png"),
            loadImage("peni_dead3.png"),
            loadImage("peni_dead3.png"),
            loadImage("peni_dead3.png"),
            loadImage("peni_dead3.png"),
            loadImage("badass1.png"), //42
            loadImage("badass2.png"),
            loadImage("badass3.png"),
            loadImage("badass4.png"),
            loadImage("badass-dead1.png"), //46
            loadImage("badass-dead2.png"),
            loadImage("badass-dead3.png"),
            loadImage("badass-dead4.png"),
            loadImage("badass-dead5.png"),
            loadImage("badass-dead6.png"),
            loadImage("chunguillo1.png"), //52
            loadImage("chunguillo2.png"),
            loadImage("chunguillo3.png"),
            loadImage("chunguillo4.png"),
            loadImage("chunguillo_dead1.png"), //56
            loadImage("chunguillo_dead2.png"),
            loadImage("chunguillo_dead3.png"),
            loadImage("chunguillo_dead4.png"),
            loadImage("chunguillo_fire1.png"), //60
            loadImage("chunguillo_fire2.png"),
            loadImage("chunguillo_fire3.png"),
            loadImage("chunguillo_fire4.png"),
            loadImage("lvl1_boss1.png"), //64
            loadImage("lvl1_boss2.png"),
            loadImage("lvl1_boss3.png"),
            loadImage("lvl1_boss4.png"),
            loadImage("lvl1_boss_dead1.png"), //68
            loadImage("lvl1_boss_dead2.png"),
            loadImage("lvl1_boss_dead3.png"),
            loadImage("lvl1_boss_dead4.png"),
            loadImage("lvl1_boss_dead5.png"),
            loadImage("lvl1_boss_dead6.png"),
        };

        images[1] = new Image[images[0].length];
        
        for (int i=0; i<images[0].length; i++) {
            // right-facing images
            images[1][i] = getMirrorImage(images[0][i]);
        }

        // create creature animations
        Animation[] playerAnim = new Animation[8];
        Animation[] chunguilloAnim = new Animation[6];
        Animation[] badassAnim = new Animation[4];
        Animation[] boss1Anim = new Animation[4];
        
        //Player Animations
        playerAnim[0] = createAnim(images[0], 0, 7); //Walking Right
        playerAnim[1] = createAnim(images[1], 0, 7); //Walking Left
        playerAnim[6] = createAnim(images[0], 8, 8); //Jump Right
        playerAnim[7] = createAnim(images[1], 8, 8); //Jump left
        playerAnim[2] = createAnim(images[0], 13,20); //Dead Right
        playerAnim[3] = createAnim(images[1], 13,20); //Dead Left
        playerAnim[4] = createAnim(images[0], 9, 12); //Still Right
        playerAnim[5] = createAnim(images[1], 9, 12); //Still Left
        
        peniAnim[0] = createAnim(images[0], 21, 28); //Walking Right
        peniAnim[1] = createAnim(images[1], 21, 28); //Walking Left
        peniAnim[6] = createAnim(images[0], 29, 29); //Jump Right
        peniAnim[7] = createAnim(images[1], 29, 29); //Jump left
        peniAnim[2] = createAnim(images[0], 34,41); //Dead Right
        peniAnim[3] = createAnim(images[1], 34 ,41); //Dead Left
        peniAnim[4] = createAnim(images[0], 30, 33); //Still Right
        peniAnim[5] = createAnim(images[1], 30, 33); //Still Left
        
        //Creature Animations
        chunguilloAnim[0] = createAnim(images[0], 52, 55); //Walking Right
        chunguilloAnim[1] = createAnim(images[1], 52, 55); //Walking Left
        chunguilloAnim[2] = createAnim(images[0], 56, 59); //Dead Right
        chunguilloAnim[3] = createAnim(images[1], 56, 59); //Dead Left
        chunguilloAnim[4] = createAnim(images[0], 60, 63); //Fire Right
        chunguilloAnim[5] = createAnim(images[1], 60, 63); //Fire Left
        
        badassAnim[0] = createAnim(images[0], 42, 45); //Walking Right
        badassAnim[1] = createAnim(images[1], 42, 45); //Walking Left
        badassAnim[2] = createAnim(images[0], 46, 51); //Dead Right
        badassAnim[3] = createAnim(images[1], 46, 51); //Dead Left
        
        boss1Anim[0] = createAnim(images[0], 64, 67); //Walking Right
        boss1Anim[1] = createAnim(images[1], 64, 67); //Walking Left
        boss1Anim[2] = createAnim(images[0], 68, 73); //Dead Right
        boss1Anim[3] = createAnim(images[1], 68, 73); //Dead Left
        
        // create creature sprites
        playerSprite = new Player(playerAnim[0], playerAnim[1], playerAnim[2], playerAnim[3], 
                                  playerAnim[6], playerAnim[7],playerAnim[4],playerAnim[5], 
                                  emptyAnimation(), emptyAnimation());
        
        peniSprite = new Player(peniAnim[0], peniAnim[1], peniAnim[2], peniAnim[3], 
                                peniAnim[6], peniAnim[7],peniAnim[4],peniAnim[5], 
                                emptyAnimation(), emptyAnimation());
        
        chunguilloSprite = new Chunquillo(chunguilloAnim[0], 
                                          chunguilloAnim[1],
                                          chunguilloAnim[2], 
                                          chunguilloAnim[3], 
                                          emptyAnimation(), 
                                          emptyAnimation(), 
                                          emptyAnimation(), 
                                          emptyAnimation(),
                                          chunguilloAnim[4],
                                          chunguilloAnim[5]);
        
        badassSprite = new Badas(badassAnim[0], badassAnim[1],
                                 badassAnim[2], badassAnim[3],
                                 emptyAnimation(), 
                                 emptyAnimation(), 
                                 emptyAnimation(), 
                                 emptyAnimation(),
                                 emptyAnimation(), 
                                 emptyAnimation());
        
        bossSprite = new Boss(boss1Anim[0], boss1Anim[1],
                              boss1Anim[2], boss1Anim[3],
                              emptyAnimation(), 
                              emptyAnimation(), 
                              emptyAnimation(), 
                              emptyAnimation(),
                              emptyAnimation(), 
                              emptyAnimation());
    }


    private Animation createAnim(Image player[], int m, int n)
    {
        Animation anim = new Animation();
        for(int i = m; i<=n; i++)
            anim.addFrame(player[i], 150);

        return anim;
    }

    public Animation emptyAnimation() {
        Animation anim = new Animation();
        anim.addFrame(loadImage(""), 150);
        return anim;
    }
    private void loadPowerUpSprites() {
        // create "bullet" sprite
        Animation anim = new Animation();
        anim.addFrame(loadImage("bullet1.png"), 150);
        bullet = new Bullet(anim);
        
        // create "bullet" sprite
        anim = new Animation();
        anim.addFrame(loadImage("bulletb.png"), 150);
        bulletB = new Bullet(anim);
        
        // create killer Tile sprite
        killerTile = new PowerUp.KillerTile(emptyAnimation());
        
        // create "goal" sprite
        anim = new Animation();
        anim.addFrame(loadImage("heart1.png"), 150);
        anim.addFrame(loadImage("heart2.png"), 150);
        anim.addFrame(loadImage("heart3.png"), 150);
        anim.addFrame(loadImage("heart2.png"), 150);
        goalSprite = new PowerUp.Goal(anim);

        // create "star" sprite
        anim = new Animation();
        anim.addFrame(loadImage("star1.png"), 100);
        anim.addFrame(loadImage("star2.png"), 100);
        anim.addFrame(loadImage("star3.png"), 100);
        anim.addFrame(loadImage("star4.png"), 100);
        coinSprite = new PowerUp.Star(anim);

        // create "music" sprite
        anim = new Animation();
        anim.addFrame(loadImage("music1.png"), 150);
        anim.addFrame(loadImage("music2.png"), 150);
        anim.addFrame(loadImage("music3.png"), 150);
        anim.addFrame(loadImage("music2.png"), 150);
        musicSprite = new PowerUp.Music(anim);
    }

}
