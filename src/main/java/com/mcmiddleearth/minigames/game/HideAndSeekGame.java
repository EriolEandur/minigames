/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.scoreboard.HideAndSeekGameScoreboard;
import com.mcmiddleearth.minigames.utils.BukkitUtil;
import com.mcmiddleearth.minigames.utils.DynmapUtil;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class HideAndSeekGame extends AbstractGame {

    private final int seekerCageRadius = 5; 
    
    private final int defaultRadius = 10;
    
    private final int defaultHideTimeSeconds = 10;
    
    private final int defaultSeekTimeSeconds = 20;
    
    private final int revealDistance = 1;
    
    @Setter
    private int seekTime = defaultSeekTimeSeconds;
    
    @Setter
    private int hideTime = defaultHideTimeSeconds;
    
    private int radius;
    
    @Getter
    private boolean seeking = false;
    
    @Getter
    private boolean hiding = false;
    
    private OfflinePlayer seeker;
    
    private final List<Player> hiddenPlayers = new ArrayList<>();
    
    private BukkitRunnable seekTask, stopTask;
    
    public HideAndSeekGame(Player manager, String name) {
        super(manager, name, GameType.HIDE_AND_SEEK, new HideAndSeekGameScoreboard());
        announceGame();
    }
    
    public void hiding(int radius) {
        if(radius>0) {
            this.radius = radius;
        }
        else {
            this.radius = defaultRadius;
        }
        this.hiding = true;
        this.seeking = false;
        if(seeker == null || BukkitUtil.getOnlinePlayer(seeker)==null) {
            seeker = getOnlinePlayers().get(new Double(Math.floor(Math.random()*(getPlayers().size()))).intValue());
        }
        for(Player player : getOnlinePlayers()) {
            if(!BukkitUtil.isSame(player,seeker)) {
                hidePlayer(player);
            }
        }
        ((HideAndSeekGameScoreboard)this.getBoard()).startHiding(seeker.getName(), hideTime);
        sendStartHideMessage();
        Location loc = getWarp().clone();
        loc.setPitch(80);
        ((Player) seeker).teleport(loc);
        seekTask = new BukkitRunnable() {
            @Override
            public void run() {
                seeking();
            }};
        seekTask.runTaskLater(MiniGamesPlugin.getPluginInstance(), hideTime*20);
    }
    
    public void seeking() {
        this.hiding = false;
        this.seeking = true;
        ((HideAndSeekGameScoreboard)this.getBoard()).startSeeking(seekTime);
        sendStartSeekingMessage();
        stopTask = new BukkitRunnable() {
            @Override
            public void run() {
                stop();
            }};
        stopTask.runTaskLater(MiniGamesPlugin.getPluginInstance(),seekTime*20);
    }
    
    public void stop() {
        if(seekTask!=null) {
            seekTask.cancel();
        }
        if(stopTask!=null) {
            stopTask.cancel();
        }
        sendStopSeekingMessage();
        for(Player player : getOnlinePlayers()) {
            if(hiddenPlayers.contains(player)) {
                unhidePlayer(player);
            }
            player.teleport(getWarp());
        }
        this.seeking = false;
        this.hiding = false;
        seeker = null;
        ((HideAndSeekGameScoreboard)this.getBoard()).stop();
    }
    
    private void hidePlayer(Player player) {
        hiddenPlayers.add(player);
        DynmapUtil.hide(player);
    }
    
    private void unhidePlayer(Player player) {
        hiddenPlayers.remove(player);
        DynmapUtil.show(player);
    }
    
    private void revealPlayer(Player player) {
        unhidePlayer(player);
        ((HideAndSeekGameScoreboard)this.getBoard()).locatePlayer();
        if(hiddenPlayers.isEmpty()) {
            stop();
        }
    }
    
    @Override
    public void addPlayer(Player player) {
            super.addPlayer(player);
            player.teleport(getWarp());
    }
    
    @Override 
    public void removePlayer(OfflinePlayer player) {
        super.removePlayer(player);
        if(seeker != null && BukkitUtil.isSame(player,seeker)) {
            stop();
        }
    }
    
    public void setSeeker(OfflinePlayer player) {
        seeker = player;
        ((HideAndSeekGameScoreboard)getBoard()).setSeeker(seeker.getName());
    }
    
    private boolean isHidden(Player player) {
        for(Player search : hiddenPlayers) {
            if(BukkitUtil.isSame(search,player)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void playerLeaveServer(PlayerQuitEvent event) {
        super.playerLeaveServer(event);
        Player player = event.getPlayer();
        if(isHidden(player)) {
            revealPlayer(player);
        }
        if(BukkitUtil.isSame(player,seeker)) {
            sendSeekerLeavingMessage(player);
            stop();
        }
    }
    
    @Override
    public int allowedRadius(Player player) {
        if(seeking) {
            return Math.abs(radius);
        }
        if(hiding && seeker != null && BukkitUtil.isSame(seeker,player)) {
            return Math.abs(seekerCageRadius);
        }
        if(hiding) {
            return Math.abs(radius);
        }
        return Math.abs(seekerCageRadius);
    }

    @Override
    public void playerMove(PlayerMoveEvent event) {
        super.playerMove(event);
        if(hiding && BukkitUtil.isSame(event.getPlayer(), seeker)) {
            if(event.getTo().getPitch()<70) {
                Location to = event.getTo().clone();
                to.setPitch(80);
                event.setTo(to);
            }
        }
        if(seeking && BukkitUtil.isSame(event.getPlayer(),seeker)) {
            Player[] myList = hiddenPlayers.toArray(new Player[0]);
            for(Player hidden : myList) {
                if(event.getTo().distance(hidden.getLocation())<revealDistance) {
                    sendPlayerFoundMessage(hidden);
                    revealPlayer(hidden);
               }
            }
        }
    }

    @Override
    public boolean joinAllowed() {
        return isAnnounced() && !(hiding || seeking);
    }
    
    @Override
    public String getGameChatTag(Player player) {
        if(BukkitUtil.isSame(player,seeker)) {
            return ChatColor.GOLD + "<Seeker ";
        }
        else {
            return super.getGameChatTag(player);
        }
    }

    private void sendStartHideMessage() {
        MessageUtil.showTitle((Player) seeker, "blue"," FREEZE!!!"," Wait for other players to hide.");
        for(Player player : getOnlinePlayers()) {
            if(!BukkitUtil.isSame(player,seeker)) {
                MessageUtil.showTitle(player, "yellow"," HIDE!!!"," ");
            }
        }
    }

    private void sendStartSeekingMessage() {
        MessageUtil.showTitle((Player) seeker, "yellow"," SEEK!!!"," Try to find the other players.");
        for(Player player : getOnlinePlayers()) {
            if(!BukkitUtil.isSame(player, seeker)) {
                MessageUtil.showTitle(player, "blue"," FREEZE!!!",seeker.getName() + " is seeking you.");
                if(BukkitUtil.getOnlinePlayer(player)!=null) {
                    MessageUtil.sendInfoMessage(BukkitUtil.getOnlinePlayer(player), "Hold SHIFT to hide your name tag.");
                }
            }
        }
    }

    private void sendStopSeekingMessage() {
        if(hiddenPlayers.isEmpty()) {
                MessageUtil.showTitle((Player) seeker, "green","YOU WON", "You found all players.");
            }
            else {
               MessageUtil.showTitle((Player) seeker, "red", "GAME OVER", "You found not all players.");
            }
        for(Player player : getOnlinePlayers()) {
            if(!BukkitUtil.isSame(player,seeker)) {
                if(hiddenPlayers.isEmpty()) {
                    MessageUtil.showTitle(player, "red","GAME OVER", seeker.getName()+" found all players.");
                }
                else if (isHidden(player)) {
                    MessageUtil.showTitle(player, "green","YOU WON", seeker.getName()+" found you not.");
                }
                else {
                    MessageUtil.showTitle(player, "red","GAME OVER", seeker.getName()+" found you but not all players.");
                }
            }
        }
    }

    private void sendSeekerLeavingMessage(Player player) {
        MessageUtil.sendAllInfoMessage(player, this, "The seeker left.");
    }

    private void sendPlayerFoundMessage(Player hidden) {
        MessageUtil.sendInfoMessage(hidden, seeker.getName() +" found you.");
        MessageUtil.sendInfoMessage((Player) seeker, "You found "+ hidden.getName() + ".");
    }
    
    
}
