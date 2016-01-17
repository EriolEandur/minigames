/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.scoreboard;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 *
 * @author Eriol_Eandur
 */
public class GameScoreboard {
  
    @Getter
    protected final Scoreboard scoreboard;
    
    private final Objective playerCountObjective;
    
    private final Score playerCountScore;
    
    public GameScoreboard(String name) {
        scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        playerCountObjective = scoreboard.registerNewObjective("PlayerCount", "dummy");
        playerCountObjective.setDisplayName(name);
        playerCountScore = playerCountObjective.getScore(ChatColor.BLUE+"players ");
        playerCountScore.setScore(0);
        playerCountObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    
    public void incrementPlayer() {
        playerCountScore.setScore(playerCountScore.getScore()+1);
    }
    
    public void decrementPlayer() {
        playerCountScore.setScore(playerCountScore.getScore()-1);
    }
    
    protected int getPlayerCount() {
        return playerCountScore.getScore();
    }
    
    protected Objective getPlayerCountObjective() {
        return playerCountObjective;
    }
    

}
