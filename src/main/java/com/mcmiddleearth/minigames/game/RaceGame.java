/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.raceCheckpoint.Checkpoint;
import com.mcmiddleearth.minigames.scoreboard.GameScoreboard;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 *
 * @author Eriol_Eandur
 */
public class RaceGame extends AbstractGame {

    private Checkpoint start;
    
    private Checkpoint finish;
    
    private final LinkedList<Checkpoint> checkpoints = new LinkedList<>();
    
    private String checkpointMarker, startMarker, finishMarker;
    
    public RaceGame(Player manager, String name) {
        super(manager, name, GameType.RACE, new GameScoreboard("Race Game"));
    }

    @Override
    public void playerMove(PlayerMoveEvent event) {
        //overrides limited game area in AbstractGame
    }
   
    @Override
    public void addPlayer(Player player) {
            super.addPlayer(player);
            player.teleport(getWarp());
    }
    
    public boolean setStartLocation(Location loc) {
        if(isLocationValid(loc, start)) {
            start.setLocation(loc);
        }
        return false;
    }
   
    public boolean setFinishLocation(Location loc) {
        if(isLocationValid(loc, finish)) {
            finish.setLocation(loc);
        }
        return false;
    }
    
    //not needed????
    public boolean setCheckpointLocation(Location loc, int id) {
        if(isIdValid(id) && isLocationValid(loc, checkpoints.get(indexFromId(id)))) {
            checkpoints.get(indexFromId(id)).setLocation(loc);
        }
        return false;
    }
   
    public boolean addCheckpointLocation(Location loc) {
        checkpoints.add(new Checkpoint(loc, checkpointMarker));
        return false;
    }
    
    public boolean insertCheckpointLocation(Location loc, int id) {
        checkpoints.add(indexFromId(id), new Checkpoint(loc, checkpointMarker));
        return false;
    }
    
    public boolean removeCheckpointLocation(int id) {    
        checkpoints.remove(indexFromId(id));
        return false;
    }
   
    public void setStartMarker(String name) throws FileNotFoundException {
        
    }
    
    public void setFinishMarker(String name) throws FileNotFoundException {
        
    }

    public void setCheckpointMarker(String name) throws FileNotFoundException {
        
    }
    
    public boolean isLocationValid(Location newLoc, Checkpoint toBeReplaced) {
        return false;
    }
    
    public boolean isIdValid(int id) {
        return id>1 && id<checkpoints.size()+1;
    }
    
    private static int indexFromId(int id) {
        return id-2;
    }
    
    private static int idFromindex(int index) {
        return index+2;
    }
}
