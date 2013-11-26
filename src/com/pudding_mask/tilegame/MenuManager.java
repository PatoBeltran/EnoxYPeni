/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pudding_mask.tilegame;

import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

/**
 *
 * @author PatoBeltran
 */

/*
 * Manages time in the game, from the begining and it doesnt take in account
 * time spent in the pause menu.
 * 
 * */
public class MenuManager {
    private boolean main;
    private boolean configuration;
    private boolean instructions;
    private boolean charchoose;
    private boolean play;
    private boolean highscores;
    private boolean pause;
    public boolean classicControl;
    public boolean sound;
    
    //gets and sets
    public boolean isPlaying(){
        return play;
    }
    public boolean isPaused(){
        return pause;
    }
    public boolean isInHighscores(){
        return highscores;
    }
    public boolean isConfiguring(){
        return configuration;
    }
    public boolean isLearning(){
        return instructions;
    }
    public boolean isChoosingChar(){
        return charchoose;
    }
    public boolean isInMainMenu(){
        return main;
    }
    public void changePauseStatus(){
        main = false;
        configuration = false;
        highscores = false;
        instructions = false;
        charchoose = false;
        play = true;
        pause = !pause;
    }
    public void goToCharChoose(){
        main = false;
        configuration = false;
        highscores = false;
        instructions = false;
        charchoose = true;
        play = false;
        pause = false;
    }
    public void goToMainMenu(){
        main = true;
        highscores = false;
        configuration = false;
        instructions = false;
        charchoose = false;
        play = false;
        pause = false;
    }
    public void goToConfiguration(){
        main = false;
        highscores = false;
        configuration = true;
        instructions = false;
        charchoose = false;
        play = false;
        pause = false;
    }
    public void goToInstructions(){
        main = false;
        highscores = false;
        configuration = false;
        instructions = true;
        charchoose = false;
        play = false;
        pause = false;
    }
    public void goToGame(){
        main = false;
        highscores = false;
        configuration = false;
        instructions = false;
        charchoose = false;
        play = true;
        pause = false;
    }
    public void goToHighscores(){
        main = false;
        highscores = true;
        configuration = false;
        instructions = false;
        charchoose = false;
        play = false;
        pause = false;
    }
    //checks which to draw, loads it and draws it
    public void draw(Graphics2D g, TileMap map,
        int screenWidth, int screenHeight)
    {
        String filename = "images/";
        
        if(main){
            filename += "menu.png";
        }
        else if(highscores){
            filename += "highscores.png";
        }
        else if(configuration){
            filename += "configurar.png";
        }
        else if(instructions){
            filename += "instrucciones.png";
        }
        else if(charchoose){
            filename += "elegir.png";
        }
        else if(pause){
            filename += "pause.png";
        }
        
        Image background = new ImageIcon(filename).getImage();
        g.drawImage(background, 0, 0, null);
        if(!classicControl && configuration){
            g.drawImage(new ImageIcon("images/configurar_wasd.png").getImage(), 0,0,null);
        }
        if(!sound && configuration){
            g.drawImage(new ImageIcon("images/configurar_no.png").getImage(), 0,0,null);
        }
         if(!classicControl && instructions){
            g.drawImage(new ImageIcon("images/instrucciones_wasd.png").getImage(), 0,0,null);
        }
        
    }
    
    //init
    public MenuManager(){
        main = true;
        highscores = false;
        configuration = false;
        instructions = false;
        charchoose = false;
        play = false;
        pause = false;
        sound = true;
        classicControl = true;
    }
}
