/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.scoreboard.GameScoreboard;
import com.mcmiddleearth.minigames.utils.BukkitUtil;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 *
 * @author Eriol_Eandur
 */
public abstract class AbstractGame {
    
    protected final static TeleportCause TeleportCause_WARP = TeleportCause.NETHER_PORTAL;
   
    protected final static TeleportCause TeleportCause_FORCE = TeleportCause.END_PORTAL;
    
    @Getter
    private final String name;
    
    @Getter
    private boolean announced = false;
    
    @Getter
    private OfflinePlayer manager;

    @Getter
    private final GameType type;
    
    @Getter
    private final List<OfflinePlayer> players = new ArrayList<>();
    
    private final List<OfflinePlayer> bannedPlayers = new ArrayList<>();

    private final List<OfflinePlayer> spectators = new ArrayList<>();
    
    private final List<OfflinePlayer> invitedPlayers = new ArrayList<>();
    
    @Getter
    private final Location warp;
    
    @Getter
    @Setter
    private boolean warpAllowed = true;
    
    @Getter
    @Setter
    private boolean spectateAllowed = true;
    
    @Getter
    @Setter
    private boolean privat = false;
    
    @Getter
    private boolean flightAllowed = true;
    
    @Getter
    @Setter
    private boolean teleportAllowed = true;
   
    @Getter
    private final GameScoreboard board;
    
    private boolean managerOnlineLastTime = true; //for cleanup task
    
    public AbstractGame(Player manager, String name, GameType type, GameScoreboard board) {
        this.name = name;
        this.manager = manager;
        warp = manager.getLocation();
        this.board = board;
        manager.setScoreboard(getBoard().getScoreboard());
        this.type = type;
        BukkitRunnable cleanupTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(!getManager().isOnline()) {
                    if(!managerOnlineLastTime) {
                        end(null);
                        cancel();
                    }
                    else {
                        managerOnlineLastTime = false;
                    }
                }
                else {
                    managerOnlineLastTime = true;
                }
            }};
        cleanupTask.runTaskTimer(MiniGamesPlugin.getPluginInstance(), 3000, 3000);
    }
    
    public void end(Player sender) {
        sendGameEndMessage(sender);    
        for(Player player: getOnlinePlayers()) {
                removePlayer(player); 
            }
            PluginData.removeGame(this);
    }
    
    public int countOnlinePlayer() {
        return getOnlinePlayers().size();
    }
    
    public void setManager(OfflinePlayer manager) {
        if(manager != null && BukkitUtil.getOnlinePlayer(manager)!=null) {
            Player oldManager = BukkitUtil.getOnlinePlayer(manager);
            if(!PluginData.isInGame(oldManager)) {
                oldManager.setScoreboard(Bukkit.getServer().getScoreboardManager().getMainScoreboard());
            }
        }
        this.manager = manager;
    }
    
    public OfflinePlayer getPlayer(String name) {
        for(OfflinePlayer player : players) {
            if(player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }
    
    public OfflinePlayer getBannedPlayer(String name) {
        for(OfflinePlayer player : bannedPlayers) {
            if(player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }
    
    public List<Player> getOnlinePlayers() {
        List<Player> online = new ArrayList<>();
        for(OfflinePlayer player : players) {
            Player onlinePlayer = BukkitUtil.getOnlinePlayer(player);
            if(onlinePlayer!=null) {
                online.add(onlinePlayer);
            }
        }
        return online;
    }
    
    public void addPlayer(Player player) {
        if(!flightAllowed) {
            player.setFlying(false);
        }
        players.add(player);
        getBoard().incrementPlayer();
        player.setScoreboard(board.getScoreboard());
    }
    
    public void addSpectator(Player player) {
        spectators.add(player);
        player.setScoreboard(getBoard().getScoreboard());
    }
    
    public void removeSpectator(Player player) {
        for(OfflinePlayer search: spectators) {
            if(BukkitUtil.isSame(search, player)) {
                spectators.remove(search);
                player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
                return;
            }
        }
    }
    
    public boolean isSpectating(Player player) {
        for(OfflinePlayer search: spectators) {
            if(BukkitUtil.isSame(search, player)) {
                return true;
            }
        }
        return false;
    }
    
    public void removePlayer(OfflinePlayer player) {
        for(OfflinePlayer search : players) {
            if(BukkitUtil.isSame(search,player)) {
                players.remove(search);
                break;
            }
        }
        getBoard().decrementPlayer();
        Player onlinePlayer = BukkitUtil.getOnlinePlayer(player);
        if(onlinePlayer!=null && !BukkitUtil.isSame(onlinePlayer,manager)) {
            onlinePlayer.setScoreboard(Bukkit.getServer().getScoreboardManager().getMainScoreboard());
        }
    }
    
    public boolean isBanned(OfflinePlayer player) {
        for(OfflinePlayer search : bannedPlayers) {
            if(BukkitUtil.isSame(search,player)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isInGame(OfflinePlayer player) {
        for(OfflinePlayer search: players) {
            if(BukkitUtil.isSame(search,player)) {
                return true;
            }
        }
        return false;
    }

    public void setBanned(OfflinePlayer player) {
        bannedPlayers.add(player);
    }
        
    public void setUnbanned(OfflinePlayer player) {
        bannedPlayers.remove(player);
    }
        
    public void playerJoinServer(PlayerJoinEvent event) {
        event.getPlayer().setScoreboard(board.getScoreboard());
        getBoard().incrementPlayer();
        if(!flightAllowed) {
            event.getPlayer().setFlying(false);
        }
    }
    
    public void playerLeaveServer(PlayerQuitEvent event) {
        event.getPlayer().setScoreboard(Bukkit.getServer().getScoreboardManager().getMainScoreboard());
        getBoard().decrementPlayer();
    }
    
    public int allowedRadius(Player player) {
        return Integer.MAX_VALUE;
    }
    
    public void playerMove(PlayerMoveEvent event) {
        Location to = event.getTo();
        Location from = event.getFrom();
        if(to.distance(getWarp())>allowedRadius(event.getPlayer())) {
            Vector vel = to.toVector().subtract(event.getFrom().toVector());
            Vector radial = event.getPlayer().getLocation().toVector().subtract(getWarp().toVector());
            Vector tangential = new Vector(radial.getZ(),radial.getY(), -radial.getX());
            tangential = tangential.multiply(1/tangential.length());
            double dot = tangential.dot(vel);
            tangential = tangential.multiply(tangential.dot(vel));
            Location newTo = new Location(from.getWorld(), 
                                          from.getX()+tangential.getX(),
                                          from.getY()+tangential.getY(),
                                          from.getZ()+tangential.getZ(),
                                          to.getYaw(), to.getPitch());
            if(newTo.distance(getWarp())>allowedRadius(event.getPlayer())) {
                Vector radialOld = radial.clone();
                Vector radialNorm = radial.multiply(1/radial.length()).clone();
                radial = radial.multiply(allowedRadius(event.getPlayer()));
                radial = radial.subtract(radialNorm.multiply(0.01));
                radial = radial.subtract(radialOld);
                newTo = newTo.add(radial);
            }
            event.setTo(newTo);
        }
    }
    
    public void playerTeleport(PlayerTeleportEvent event) {
        if((!teleportAllowed && !event.getCause().equals(TeleportCause_FORCE))) {
            event.setCancelled(true);
        }
    }
    
    public void playerToggleFlight(PlayerToggleFlightEvent event) {
        if(!flightAllowed) {
            event.getPlayer().setFlying(false);
            event.setCancelled(true);
        }
    }
    
    public void forceTeleport(Player player, Location loc) {
        //boolean teleport = teleportAllowed;
        player.teleport(loc, TeleportCause_FORCE);
        //teleportAllowed = teleport;
    }
    
    public void warp(Player player, Location loc) {
        player.teleport(loc, TeleportCause_WARP);
    }

    
    public boolean joinAllowed() {
        return announced;
    }
    
    public void announceGame() {
        announced = true;
        sendAnnounceGameMessage();
    }
    
    public boolean isInvited(OfflinePlayer player) {
        return BukkitUtil.getOfflinePlayer(invitedPlayers, player)!=null 
                || BukkitUtil.isSame(player, manager);
    }
    
    public void invite(OfflinePlayer player) {
        invitedPlayers.add(player);
    }
    
    public void setFlightAllowed(boolean allowed) {
        if(this.flightAllowed && !allowed)  {
            for(Player player : getOnlinePlayers()) {
                player.setFlying(false);
            }
        }
        flightAllowed = allowed;
    }
    
    public String getGameChatTag(Player player) {
        if(PluginData.isManager(player)) {
            return ChatColor.DARK_AQUA + "<Manager "; 
        }
        else {
            return ChatColor.BLUE + "<Participant "; 
        }
    }
    
    protected void sendAnnounceGameMessage() {
        MessageUtil.sendBroadcastMessage("§2"+manager.getName() + "§b started a new §2"+ getType().toString()+"§b game. "
                                        + "To play that game, type in chat: /game join §2"+getName());
    }
    
    public void sendGameEndMessage(Player sender) {
        MessageUtil.sendAllInfoMessage(sender, this, "The game "+ getName()+" ended.");
    }
}
