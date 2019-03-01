package com.mcmiddleearth.minigames.scoreboard;

import com.mcmiddleearth.minigames.game.GolfGame;
import com.mcmiddleearth.minigames.golf.GolfPlayer;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

/**
 * @author Planetology
 */
public class GolfGameScoreboard extends GameScoreboard {

    private GolfGame game;
    private Objective holeObjective, scoresObjective, finishedObjective;

    public GolfGameScoreboard() {
        super("Golf");
    }

    public void init(GolfGame game) {
        this.game = game;

        holeObjective = scoreboard.registerNewObjective("hole", "dummy", ChatColor.GOLD + "Par " + game.getLocationManager().getGameHoles().getFirst().getPar() + ChatColor.GRAY + " | "
                + ChatColor.GOLD + "Hole " + game.getHole());
        scoresObjective = scoreboard.registerNewObjective("scores", "dummy", ChatColor.GOLD + "Scores " + ChatColor.GRAY + "| "
                + ChatColor.GOLD + "Hole " + game.getHole());
        finishedObjective = scoreboard.registerNewObjective("finished", "dummy", ChatColor.GOLD + "Scores " + ChatColor.GRAY + "| "
                + ChatColor.GOLD + "Game Over!");
    }

    public void addPlayer(String playerName) {
        Score score = holeObjective.getScore(playerName);
        score.setScore(0);
    }

    public void removePlayer(String playerName) {
        scoreboard.resetScores(playerName);
        scoreboard.resetScores(ChatColor.GOLD + playerName);
    }

    public void showHole() {
        holeObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        holeObjective.setDisplayName(ChatColor.GOLD + "Par " + game.getLocationManager().getGameHoles().get(game.getHole() - 1).getPar() + ChatColor.GRAY + " | " + ChatColor.GOLD + "Hole " + game.getHole());
    }

    public void showScores() {
        scoresObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        scoresObjective.setDisplayName(ChatColor.GOLD + "Scores " + ChatColor.GRAY + "| " + ChatColor.GOLD + "Hole " + game.getHole());
    }

    public void showFinished() {
        finishedObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void addScoresPlayer(String playerName, int shots) {
        int difference = -game.getLocationManager().getGameHoles().get(game.getHole() - 1).getPar() + shots;

        Score score = scoresObjective.getScore(playerName);
        score.setScore(difference);
    }

    public void addFinishedPlayer(String playerName, int points) {
        Score score = finishedObjective.getScore(playerName);
        score.setScore(points);
    }

    public void showShots(GolfPlayer golfPlayer) {
        holeObjective.getScore(ChatColor.YELLOW + golfPlayer.getGolfer().getName()).setScore(holeObjective.getScore(ChatColor.YELLOW + golfPlayer.getGolfer().getName()).getScore() + 1);
        holeObjective.getScore(golfPlayer.getGolfer().getName()).setScore(holeObjective.getScore(golfPlayer.getGolfer().getName()).getScore() + 1);
    }

    public void showGolfer(GolfPlayer golfPlayer) {
        scoreboard.resetScores(golfPlayer.getGolfer().getName());

        Score score = holeObjective.getScore(ChatColor.YELLOW + golfPlayer.getGolfer().getName());
        score.setScore(golfPlayer.getShots());
    }

    public void removeGolfer(GolfPlayer golfPlayer) {
        scoreboard.resetScores(ChatColor.YELLOW + golfPlayer.getGolfer().getName());

        Score score = holeObjective.getScore(golfPlayer.getGolfer().getName());
        score.setScore(golfPlayer.getShots());
    }

    public void resetGolferScore(GolfPlayer golfPlayer) {
        Score score = holeObjective.getScore(golfPlayer.getGolfer().getName());
        score.setScore(golfPlayer.getShots());
    }
}
