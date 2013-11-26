package com.pudding_mask.tilegame;

import java.awt.Image;
import java.util.LinkedList;
import java.util.Iterator;

import com.pudding_mask.graphics.Sprite;

/**
    The TileMap class contains the data for a tile-based
    map, including Sprites. Each tile is a reference to an
    Image. Of course, Images are used multiple times in the tile
    map.
*/
public class TileMap {

    private Image[][] tiles;
    private LinkedList sprites;
    private LinkedList bullets;
    private LinkedList enemyBullets;
    private Sprite boss;
    private Sprite player;

    /**
        Creates a new TileMap with the specified width and
        height (in number of tiles) of the map.
    */
    public TileMap(int width, int height) {
        tiles = new Image[width][height];
        sprites = new LinkedList();
        bullets = new LinkedList();
        enemyBullets = new LinkedList();
    }


    /**
        Gets the width of this TileMap (number of tiles across).
    */
    public int getWidth() {
        return tiles.length;
    }


    /**
        Gets the height of this TileMap (number of tiles down).
    */
    public int getHeight() {
        return tiles[0].length;
    }


    /**
        Gets the tile at the specified location. Returns null if
        no tile is at the location or if the location is out of
        bounds.
    */
    public Image getTile(int x, int y) {
        if (x < 0 || x >= getWidth() ||
            y < 0 || y >= getHeight())
        {
            return null;
        }
        else {
            return tiles[x][y];
        }
    }

    /**
        Sets the tile at the specified location.
    */
    public void setTile(int x, int y, Image tile) {
        tiles[x][y] = tile;
    }
     public void removeTrap() {
        tiles[ResourceManager.trap_x][ResourceManager.trap_y] = null;
    }


    /**
        Gets the player Sprite.
    */
    public Sprite getPlayer() {
        return player;
    }
    public Sprite getBoss() {
        return boss;
    }

    /**
        Sets the player Sprite.
    */
    public void setPlayer(Sprite player) {
        this.player = player;
    }


    /**
        Adds a Sprite object to this map.
    */
    public void addSprite(Sprite sprite) {
        sprites.add(sprite);
    }
    public void addBullet(Sprite bullet) {
        bullets.add(bullet);
    }
    public void addEnBullet(Sprite bullet) {
        enemyBullets.add(bullet);
    }
    public void addBoss(Sprite boss) {
        this.boss=boss;
    }


    /**
        Removes a Sprite object from this map.
    */
    public void removeSprite(Sprite sprite) {
        sprites.remove(sprite);
    }
    public void removeBullet(Sprite sprite) {
        bullets.remove(sprite);
    }
    public void removeEnBullet(Sprite sprite) {
        enemyBullets.remove(sprite);
    }
    public void removeBoss(Sprite sprite) {
        this.boss=null;
    }

    /**
        Gets an Iterator of all the Sprites in this map,
        excluding the player Sprite.
    */
    public Iterator getSprites() {
        return sprites.iterator();
    }
    public Iterator getBullets() {
        return bullets.iterator();
    }
    public Iterator getEnBul(){
        return enemyBullets.iterator();
    }
}
