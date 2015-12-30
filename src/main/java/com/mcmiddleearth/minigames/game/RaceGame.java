/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.raceCheckpoint.Checkpoint;
import com.mcmiddleearth.minigames.scoreboard.GameScoreboard;
import com.mcmiddleearth.minigames.raceCheckpoint.CheckpointManager;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class RaceGame extends AbstractGame {
    
    @Getter
    private final CheckpointManager checkpointManager = new CheckpointManager(getName());
    
    @Getter
    private boolean started = false;
    
    @Getter
    private boolean steady = false;
    
    private List<Location> cageLocations = new ArrayList<>();
    
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
    
    @Override
    public void end(Player player) {
        checkpointManager.deleteCheckpoints();
        if(steady) cagePlayer(false);
        super.end(player);
    }
    
    public void steady() {
        steady = true;
        started = true;
        cageLocations = getCageLocations(checkpointManager.getStart());
        cagePlayer(true);
        BukkitRunnable goTask = new BukkitRunnable() {
            int timer = 10;
            @Override
            public void run() {
                if(timer>0) {
                    timer--;
                    getManager().getPlayer().sendMessage(""+timer);
                }
                else {
                    cancel();
                    go();
                }
            }};
        goTask.runTaskTimer(MiniGamesPlugin.getPluginInstance(), 20, 20);
    }
    
    public void go() {
        steady = false;
        cagePlayer(false);
    }
    
    public void stop() {
        if(steady) cagePlayer(false);
        started = false;
        steady = false;
    }
    
    private void cagePlayer(boolean cage) {
        Checkpoint start = checkpointManager.getStart();
        List<Location> checkList = start.getCheckLocList();
        //List<Location> cageLocations = getCageLocations(start);
        if(!cageLocations.isEmpty()) {
            ListIterator<Location> listIterator = checkList.listIterator();
            for(Player player: getOnlinePlayers()) {
                Location teleportLoc = listIterator.next();
                teleportLoc.setX(teleportLoc.getX()+0.5);
                teleportLoc.setY(teleportLoc.getY()+0.5);
                teleportLoc.setZ(teleportLoc.getZ()+0.5);
                teleportLoc.setYaw(start.getLocation().getYaw());
                player.teleport(teleportLoc);
                for(Location loc: cageLocations) {
                    Material material;
                    if(cage) {
                        material=Material.BARRIER;
                    }
                    else {
                        material=Material.AIR;
                    }
                    player.sendBlockChange(loc, material, new Integer(0).byteValue());
                }
                if(!listIterator.hasNext()) {
                    listIterator=checkList.listIterator();
                }
            }
        }
    }

    private List<Location> getCageLocations(Checkpoint check) {
        List<Location> blockLocations = new ArrayList<>();
        List<Location> checkLocs = check.getCheckLocList();
        for(Location loc : checkLocs) {
            check.addIfNotInMarker(blockLocations, loc.getBlock().getRelative( 0,-1, 0).getLocation());
            check.addIfNotInMarker(blockLocations, loc.getBlock().getRelative( 0, 2, 0).getLocation());
            check.addIfNotInMarker(blockLocations, loc.getBlock().getRelative(-1, 0, 0).getLocation());
            check.addIfNotInMarker(blockLocations, loc.getBlock().getRelative( 1, 0, 0).getLocation());
            check.addIfNotInMarker(blockLocations, loc.getBlock().getRelative( 0, 0,-1).getLocation());
            check.addIfNotInMarker(blockLocations, loc.getBlock().getRelative( 0, 0, 1).getLocation());
            check.addIfNotInMarker(blockLocations, loc.getBlock().getRelative(-1, 1, 0).getLocation());
            check.addIfNotInMarker(blockLocations, loc.getBlock().getRelative( 1, 1, 0).getLocation());
            check.addIfNotInMarker(blockLocations, loc.getBlock().getRelative( 0, 1,-1).getLocation());
            check.addIfNotInMarker(blockLocations, loc.getBlock().getRelative( 0, 1, 1).getLocation());
        }
        return blockLocations;
    }
    
}
