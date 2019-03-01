package com.mcmiddleearth.minigames.scoreboard;

import com.mcmiddleearth.minigames.game.PvPGame;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

/**
 * @author Planetology
 */
public class PvPGameScoreboard extends GameScoreboard {

    private PvPGame game;
    private Objective killsObjective;

    public PvPGameScoreboard() { super("PvP"); }

    public void init(PvPGame game) {
        this.game = game;

        killsObjective = scoreboard.registerNewObjective("hole", "dummy");
        killsObjective.setDisplayName(ChatColor.GOLD + "   PvP " + ChatColor.GRAY + "▏ " + ChatColor.GOLD + game.getClock() + "   ");

        addTeams();
    }

    private void addTeams() {
        Score redScore = killsObjective.getScore(ChatColor.RED + "Red:");
        Score blueScore = killsObjective.getScore(ChatColor.BLUE + "Blue:");

        blueScore.setScore(0);
        redScore.setScore(0);
    }

    public void showKills() {
        killsObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        killsObjective.setDisplayName(ChatColor.GOLD + "   PvP " + ChatColor.GRAY + "▏ " + ChatColor.GOLD + game.getClock() + "   ");
    }

    public void showGameOver() {
        killsObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        killsObjective.setDisplayName(ChatColor.GOLD + "   Game Over!   ");
    }

    public void addKill(String team) {
        if (team.equalsIgnoreCase("red")) {
            Score score = killsObjective.getScore(ChatColor.RED + "Red:");
            score.setScore(killsObjective.getScore(ChatColor.RED + "Red:").getScore() + 1);
        } else if (team.equalsIgnoreCase("blue")) {
            Score score = killsObjective.getScore(ChatColor.BLUE + "Blue:");
            score.setScore(killsObjective.getScore(ChatColor.BLUE + "Blue:").getScore() + 1);
        }
    }
}
