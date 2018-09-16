/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.raceBoostItem.BoostItemManager;
import com.mcmiddleearth.minigames.raceCheckpoint.Checkpoint;
import com.mcmiddleearth.minigames.raceCheckpoint.CheckpointManager;
import com.mcmiddleearth.minigames.scoreboard.RaceGameScoreboard;
import com.mcmiddleearth.pluginutil.BlockUtil;
import com.mcmiddleearth.pluginutil.TitleUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class RaceGame extends AbstractGame {
    
    @Getter
    private final CheckpointManager checkpointManager = new CheckpointManager(getName());
    
    @Getter
    private final BoostItemManager boostItemManager = new BoostItemManager();
    
    @Getter
    private boolean started = false;
    
    @Getter
    private boolean steady = false;
    
    private int finished = 0;
    
    private boolean autoShow = true;
    
    private int timer = 10;
    
    private BukkitRunnable goTask; 
    
    private List<Location> cageLocations = new ArrayList<>();
    
    private final Map<UUID,Integer> nextCheckpoints = new HashMap<>();
    
    public RaceGame(Player manager, String name) {
        super(manager, name, GameType.RACE, new RaceGameScoreboard());
        setFlightAllowed(false);
        setTeleportAllowed(false);
        setGm2Forced(true);
        ((RaceGameScoreboard)getBoard()).init(this);
    }

    @Override
    public void playerMove(PlayerMoveEvent event) {
        //no super.playerMove call overrides limited game area in AbstractGame
        
        if(started) {
            if(checkpointManager.getFinish().isCheckLocation(event.getPlayer().getLocation())
                    && getNextCheckpoint(event.getPlayer())==checkpointManager.getCheckpoints().size()+1) {
                incrementCheckpoint(event.getPlayer());
                event.getPlayer().playEffect(checkpointManager.getFinish().getLocation(),Effect.CLICK2,0);
                if(autoShow) {
                    ((RaceGameScoreboard)getBoard()).showFinish();
                }
                ((RaceGameScoreboard)getBoard()).finish(event.getPlayer().getName());
                finished ++;
                TitleUtil.showTitle(event.getPlayer(),ChatColor.GOLD+"FINISH", 
                                            "You are placed "+getPlace()+".");
                if(finished==1) {
                    TitleUtil.showTitleAll(getOnlinePlayers(),event.getPlayer(),
                                             ChatColor.BLUE+event.getPlayer().getName(),"won the race.");
                }
            }
            for(Checkpoint check:checkpointManager.getCheckpoints()) {
                int checkId = checkpointManager.getId(check);
                if(check.isCheckLocation(event.getPlayer().getLocation())
                        && checkId == getNextCheckpoint(event.getPlayer())) {
                    incrementCheckpoint(event.getPlayer());
                    PluginData.getMessageUtil().sendInfoMessage(event.getPlayer(),"You reached checkpoint "+checkId+".");
                    event.getPlayer().playEffect(check.getLocation(),Effect.CLICK2,0);
                    if(autoShow) {
                        ((RaceGameScoreboard)getBoard()).showCheckpoint(checkId);
                    }
                    ((RaceGameScoreboard)getBoard()).
                                chechpointReached(event.getPlayer().getName(), checkId);
                }
            }
        }
    }
    
    private String getPlace() {
        if((finished % 10)==1) {
            return finished+"st";
        }
        if((finished % 10)==2) {
            return finished+"nd";
        }
        if((finished % 10)==3) {
            return finished+"rd";
        }
        return finished+"th";
    }
   
    @Override
    public void addPlayer(Player player) {
        super.addPlayer(player);
        forceTeleport(player,getWarp());
        ((RaceGameScoreboard) getBoard()).addPlayer(player.getName());
    }
    
    @Override
    public void end(Player player) {
        checkpointManager.deleteCheckpoints();
        if(steady) cagePlayer(false);
        super.end(player);
    }
    
    @Override
    public boolean joinAllowed() {
        return super.joinAllowed() && !started;
    }
    
    @Override
    public void playerTeleport(PlayerTeleportEvent event) {
        super.playerTeleport(event);
        // block warping of racing players to the checkpoints of the race
        // even if teleportation is allowed
        if(started && event.getCause().equals(TeleportCause_WARP)) {
            event.setCancelled(true);
        }
    }
    
    public void steady() {
        steady = true;
        started = true;
        resetNextCheckpoints();
        cageLocations = getCageLocations(checkpointManager.getStart());
        cagePlayer(true);
        //TitleUtil.setTimesAll(getOnlinePlayers(), null, 20,290,0);
        //TitleUtil.setTitleAll(getOnlinePlayers(), null, ChatColor.RED+"start in");
        final String title = ChatColor.RED+"start in";
        TitleUtil.showTitleAll(getOnlinePlayers(), null, title,"",20,300,0);
        timer = 11;
        goTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(timer>1) {
                    timer--;
                    TitleUtil.showTitleAll(getOnlinePlayers(), null, null, timer+"",0,300,0);
                }
                else {
                    cancel();
                    TitleUtil.showTitleAll(getOnlinePlayers(), null, ChatColor.GREEN+"GO","",0,30,60);
                    go();
                }
            }
        
            @Override
            public void cancel() {
                super.cancel();
            }};
        goTask.runTaskTimer(MiniGamesPlugin.getPluginInstance(), 20, 20);
    }
    
    public void go() {
        steady = false;
        cagePlayer(false);
        ((RaceGameScoreboard)getBoard()).startRace();
        placeBoostItems();
    }
    
    public void stop() {
        if(goTask!= null) {
            goTask.cancel();
        }
        if(steady) cagePlayer(false);
        started = false;
        steady = false;
        finished = 0;
        ((RaceGameScoreboard)getBoard()).stopRace();
        removeBoostItems();
    }
    
    /** 
     * Places 1-4 boost items between every two race checkpoints
     */
    private void placeBoostItems() {
        //TODO
    }
    
    /**
     * Removes all boost items of this game
     */
    private void removeBoostItems() {
        //TODO
    }
    
    private void cagePlayer(boolean cage) {
        Checkpoint start = checkpointManager.getStart();
        List<Location> checkList = start.getCheckLocList();
        if(!cageLocations.isEmpty()) {
            ListIterator<Location> listIterator = checkList.listIterator();
            for(Player player: getOnlinePlayers()) {
                if(cage) {
                    Location teleportLoc = listIterator.next();
                    teleportLoc.setX(teleportLoc.getBlockX()+0.5);
                    teleportLoc.setY(teleportLoc.getBlockY()+0.5);
                    teleportLoc.setZ(teleportLoc.getBlockZ()+0.5);
                    teleportLoc.setYaw(start.getLocation().getYaw());
                    forceTeleport(player,teleportLoc);
                }
                for(Location loc: cageLocations) {
                    Material material;
                    byte value;
                    if(cage) {
                        if(BlockUtil.isTransparent(loc)) {
                            material=Material.BARRIER;
                        }
                        else {
                            material=Material.BEDROCK;
                        }
                        value = 0;
                    }
                    else {
                        material=loc.getBlock().getType();
                        value = loc.getBlock().getData();
                    }
                    player.sendBlockChange(loc, material, value);
                }
                if(!listIterator.hasNext()) {
                    listIterator=checkList.listIterator();
                }
                listIterator.next();
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
    
    public void showStart() {
        autoShow = false;
        ((RaceGameScoreboard) getBoard()).showStart();
    }
    public void showFinish() {
        autoShow = false;
        ((RaceGameScoreboard) getBoard()).showFinish();
    }
    public void showCheckpoint(int checkId) {
        autoShow = false;
        ((RaceGameScoreboard) getBoard()).showCheckpoint(checkId);
    }
    
    public void showAuto() {
        autoShow = true;
    }
    
    private void resetNextCheckpoints() {
        nextCheckpoints.clear();
        for(UUID player: getPlayers()) {
            nextCheckpoints.put(player, 1);
        }
    }
    
    private int getNextCheckpoint(OfflinePlayer player) {
        for(UUID search: nextCheckpoints.keySet()) {
            if(player.getUniqueId().equals(search)) {
                return nextCheckpoints.get(search);
            }
        }
        nextCheckpoints.put(player.getUniqueId(), 1);
        return 1;
    }
    
    private void incrementCheckpoint(OfflinePlayer player) {
        for(UUID search: nextCheckpoints.keySet()) {
            if(player.getUniqueId().equals(search)) {
                nextCheckpoints.put(search, nextCheckpoints.get(search)+1);
                return;
            }
        }
        nextCheckpoints.put(player.getUniqueId(), 2);
    }
    
    public boolean playerRacing() {
        for(Player player: getOnlinePlayers()) {
            if(getNextCheckpoint(player)<checkpointManager.getCheckpoints().size()+2) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String getGameChatTag(Player player) {
        if(PluginData.isManager(player)) {
            return ChatColor.DARK_AQUA + "<Manager "; 
        }
        else {
            return ChatColor.BLUE + "<Racer: "; 
        }
    }
    
    public boolean hasStart() {
        return checkpointManager.getStart()!=null;
    }
    public boolean hasFinish() {
        return checkpointManager.getFinish()!=null;
    }

}
//when finished with boost items, need to impliment them here - didi