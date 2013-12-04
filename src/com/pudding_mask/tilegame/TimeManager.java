/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pudding_mask.tilegame;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author PatoBeltran
 */
public class TimeManager {
    private Date initialTime;
    private long pausedTime;
    private Date startPauseTime;
    private long check;
    public boolean paused;
    
   
    public void resetTime(){
        initialTime = getCurrentTime();
        pausedTime = 0;
    }
    private Date getCurrentTime(){
        return Calendar.getInstance().getTime();
    }
    public long getGamingTime(){
        return ((getCurrentTime().getTime() - initialTime.getTime())- pausedTime)/1000;
    }
    public long getPausedTime(){
        return pausedTime;
    }
    public long getStartPauseTime(){
        return check;
    }
    private void startPause(){
        startPauseTime = getCurrentTime();
        check = getGamingTime();
        paused = true;
    }
    private void exitPause(){
        pausedTime += (getCurrentTime().getTime() - startPauseTime.getTime());
        paused = false;
    }
    public void pause(){
        if(paused){
            exitPause();
        }
        else {
            startPause();
        }
    }   
    TimeManager(){
        initialTime = this.getCurrentTime();
        pausedTime = 0;
    }
}
