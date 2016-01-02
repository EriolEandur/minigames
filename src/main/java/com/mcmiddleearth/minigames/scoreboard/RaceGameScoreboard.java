/*
 * Copyright (C) 2015 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.minigames.scoreboard;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.game.RaceGame;
import com.mcmiddleearth.minigames.raceCheckpoint.Checkpoint;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

/**
 *
 * @author Eriol_Eandur
 */
public class RaceGameScoreboard extends GameScoreboard{
    
    private Objective startObjective;
    
    private Objective finishObjective;
    
    private RaceGame game;
    
    private final List<Objective> checkObjectives = new ArrayList<>();
    
    private BukkitRunnable timerTask;
    
    private final String TIME_SCORE = "    Race Time:";
    
    private int time = 0;
    
    public RaceGameScoreboard() {
        super("Race");
    }
    
    public void init(RaceGame game) {
        this.game = game;
        startObjective = scoreboard.registerNewObjective("START", "dummy");
        showStart();
        recreateCheckAndFinish();
    }

    private void recreateCheckAndFinish() {
        if(finishObjective!=null) {
            finishObjective.unregister();
        }
        for(Objective check : checkObjectives) {
            check.unregister();
        }
        checkObjectives.clear();
        finishObjective = scoreboard.registerNewObjective("FINISH", "dummy");
        finishObjective.setDisplayName("FINISH");
        int i=1;
        List<Checkpoint> checkpoints = game.getCheckpointManager().getCheckpoints();
        for(Checkpoint check : checkpoints) {
            Objective checkObjective = scoreboard.registerNewObjective("Check "+i
                                                   +" of "+checkpoints.size(), "dummy");
            checkObjective.setDisplayName("Checkpoint "+i+" of "+checkpoints.size());
            checkObjectives.add(checkObjective);
            i++;
        }
    }

    public void startRace() {
        if(timerTask!=null) {
            timerTask.cancel();
        }
        recreateCheckAndFinish();
        timerTask = new BukkitRunnable() {
            
            @Override
            public void run() {
                if(!game.playerRacing()) {
                    cancel();
                }
                else {
                    time++;
                    startObjective.getScore(TIME_SCORE).setScore(time);
                    finishObjective.getScore(TIME_SCORE).setScore(time);
                    for(Objective check: checkObjectives) {
                        check.getScore(TIME_SCORE).setScore(time);
                    }
                }
            }};
        timerTask.runTaskTimer(MiniGamesPlugin.getPluginInstance(), 20, 20);
    }
    
    public void stopRace() {
        if(timerTask!=null) {
            timerTask.cancel();
        }
        time = 0;
        recreateCheckAndFinish();
        startObjective.getScore(TIME_SCORE).setScore(time);
        showStart();
    }
    
    public void chechpointReached(String playerName, int checkId) {
        checkObjectives.get(checkId-1).getScore(playerName).setScore(time);
    }
    
    public void finish(String playerName) {
        finishObjective.getScore(playerName).setScore(time);
    }
    
    public void addPlayer(String playerName) {
        Score score = startObjective.getScore(playerName);
        score.setScore(0);
    }
    
    public void showStart() {
        startObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    
    public void showFinish() {
        finishObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    
    public void showCheckpoint(int checkId) {
        checkObjectives.get(checkId-1).setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    
}
