/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.scoreboard;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

/**
 *
 * @author Eriol_Eandur
 */
public class HideAndSeekGameScoreboard extends GameScoreboard {
    
    private final Objective hidingObjective;
    private final Objective seekingObjective;
    
    private final Score hidingTimeScore;
    private final Score seekingTimeScore;
    private final Score hiddenPlayerScore;
    private final Score locatedPlayerScore;
    
    private final static String title = "Seeker: ";
    private final static String playerCountTitle = "Next Seeker: ";
    
    private BukkitRunnable timerTask;
    
    public HideAndSeekGameScoreboard() {
        super(playerCountTitle+"?");
        hidingObjective = scoreboard.registerNewObjective("HidingTime", "dummy");
        hidingObjective.setDisplayName(title+"?");
        hidingTimeScore = hidingObjective.getScore(ChatColor.YELLOW+"hiding time remaining: ");
        
        seekingObjective = scoreboard.registerNewObjective("SeekingTime", "dummy");
        seekingObjective.setDisplayName(title+"?");
        seekingTimeScore = seekingObjective.getScore(ChatColor.YELLOW+"seeking time remaining: ");
        hiddenPlayerScore = seekingObjective.getScore(ChatColor.RED+"hidden Players: ");
        locatedPlayerScore = seekingObjective.getScore(ChatColor.GREEN+"located Players: ");
    }
    
    public void startHiding(String seeker, int hidingTime) {
        hidingObjective.setDisplayName(title+seeker);
        seekingObjective.setDisplayName(title+seeker);
        hidingTimeScore.setScore(hidingTime);
        hidingObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        if(timerTask!=null) {
            timerTask.cancel();
        }
        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                hidingTimeScore.setScore(hidingTimeScore.getScore()-1);
                if(hidingTimeScore.getScore()<1) {
                    cancel();
                }
            }};
        timerTask.runTaskTimer(MiniGamesPlugin.getPluginInstance(), 20, 20);
    }
    
    public void startSeeking(int seekingTime) {
        seekingTimeScore.setScore(seekingTime);
        hiddenPlayerScore.setScore(getPlayerCount()-1);
        locatedPlayerScore.setScore(0);
        seekingObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        if(timerTask!=null) {
            timerTask.cancel();
        }
        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                seekingTimeScore.setScore(seekingTimeScore.getScore()-1);
                if(seekingTimeScore.getScore()<1) {
                    cancel();
                }
            }};
        timerTask.runTaskTimer(MiniGamesPlugin.getPluginInstance(), 20, 20);
    }
    
    public void stop() {
        setSeeker("?");
        getPlayerCountObjective().setDisplaySlot(DisplaySlot.SIDEBAR);
        if(timerTask!=null) {
            timerTask.cancel();
        }
        timerTask = null;
    }
    
    public void locatePlayer() {
        hiddenPlayerScore.setScore(hiddenPlayerScore.getScore()-1);
        locatedPlayerScore.setScore(locatedPlayerScore.getScore()+1);
    }

    public void setSeeker(String name) {
        hidingObjective.setDisplayName(title+name);
        seekingObjective.setDisplayName(title+name);
        getPlayerCountObjective().setDisplayName(playerCountTitle+name);
    }
    

}
