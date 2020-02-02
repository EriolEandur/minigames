/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.scoreboard.HideAndSeekGameScoreboard;
import com.mcmiddleearth.pluginutil.PlayerUtil;
import com.mcmiddleearth.pluginutil.DynmapUtil;
import com.mcmiddleearth.minigames.utils.GameChatUtil;
import com.mcmiddleearth.pluginutil.TitleUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Eriol_Eandur
 */
public class HideAndSeekGame extends AbstractGame implements Listener {

    private final int seekerCageRadius = 5; 
    
    private final int defaultRadius = 10;
    
    private final int defaultHideTimeSeconds = 60;
    
    private final int defaultSeekTimeSeconds = 300;
    
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
    
    @Getter public OfflinePlayer seeker;
    
    @Getter public final List<Player> hiddenPlayers = new ArrayList<>();
    
    private BukkitRunnable seekTask, stopTask;
    
    public HideAndSeekGame(Player manager, String name) {
        super(manager, name, GameType.HIDE_AND_SEEK, new HideAndSeekGameScoreboard());

        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGamesPlugin.getPluginInstance());

        setTeleportAllowed(false);
        setFlightAllowed(false);
        setGm2Forced(true);
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
        if(seeker == null || PlayerUtil.getOnlinePlayer(seeker)==null) {
            seeker = getOnlinePlayers().get(new Double(Math.floor(Math.random()*(getPlayers().size()))).intValue());
        }
        final PotionEffect effect = new PotionEffect(PotionEffectType.SPEED, 
                                                    (hideTime+seekTime)*20, 3, false, true);
        ((Player)seeker).addPotionEffect(effect);
        for(Player player : getOnlinePlayers()) {
            if(!PlayerUtil.isSame(player,seeker)) {
                hidePlayer(player);
            }
        }
        ((HideAndSeekGameScoreboard)this.getBoard()).startHiding(seeker.getName(), hideTime);
        sendStartHideMessage();
        Location loc = getWarp().clone();
        loc.setPitch(80);
        forceTeleport((Player) seeker,loc);
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
        for(Player player:hiddenPlayers) {
            player.setSneaking(true);
        }
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
            forceTeleport(player,getWarp());
        }
        this.seeking = false;
        this.hiding = false;
        Player onlinePlayer = Bukkit.getPlayer(seeker.getUniqueId());
        if(onlinePlayer!=null) {
            onlinePlayer.removePotionEffect(PotionEffectType.SPEED);
        }
        seeker = null;
        ((HideAndSeekGameScoreboard)this.getBoard()).stop();
    }
    
    private void hidePlayer(Player player) {
        hiddenPlayers.add(player);
        DynmapUtil.hide(player);
    }
    
    private void unhidePlayer(Player player) {
        hiddenPlayers.remove(player);
        player.setSneaking(false);
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
        forceTeleport(player,getWarp());
    }
    
    @Override 
    public void removePlayer(OfflinePlayer player) {
        super.removePlayer(player);
        if(seeker != null && PlayerUtil.isSame(player,seeker)) {
            stop();
        }
    }
    
    public void setSeeker(OfflinePlayer player) {
        seeker = player;
        ((HideAndSeekGameScoreboard)getBoard()).setSeeker(seeker.getName());
    }
    
    private boolean isHidden(Player player) {
        for(Player search : hiddenPlayers) {
            if(PlayerUtil.isSame(search,player)) {
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
        if(PlayerUtil.isSame(player,seeker)) {
            sendSeekerLeavingMessage(player);
            stop();
        }
    }
    
    @Override
    public int allowedRadius(Player player) {
        if(seeking) {
            return Math.abs(radius);
        }
        if(hiding && seeker != null && PlayerUtil.isSame(seeker,player)) {
            return Math.abs(seekerCageRadius);
        }
        if(hiding) {
            return Math.abs(radius);
        }
        return Math.abs(defaultRadius);
    }

    @Override
    public void playerMove(PlayerMoveEvent event) {
        super.playerMove(event);
        if(hiding && PlayerUtil.isSame(event.getPlayer(), seeker)) {
            if(event.getTo().getPitch()<70) {
                Location to = event.getTo().clone();
                to.setPitch(80);
                event.setCancelled(true);
                event.getPlayer().teleport(to, TeleportCause_FORCE);
            }
        }
        if(seeking) {
            if(!PlayerUtil.isSame(event.getPlayer(),seeker)) {
                event.getPlayer().setSneaking(true);
            } else {
                Player[] myList = hiddenPlayers.toArray(new Player[0]);
                for(Player hidden : myList) {
                    if(event.getTo().distance(hidden.getLocation())<revealDistance) {
                        sendPlayerFoundMessage(hidden);
                        revealPlayer(hidden);
                   }
                }
            }
        }
    }
    
    @Override
    public void playerDamaged(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player)) {
            return;
        }
        if(!PlayerUtil.isSame(seeker, (Player) event.getDamager())) {
            return;
        }
        Player player = (Player) event.getEntity();
        if(seeking && hiddenPlayers.contains(player)) {
            this.revealPlayer(player);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (!hiddenPlayers.isEmpty() && seeker != null) {
            if (hiddenPlayers.contains(event.getPlayer()) || seeker.equals(event.getPlayer())) {
                if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK)) {
                    if (itemInHand.getType().equals(Material.GHAST_TEAR)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        List<Block> blocks = new ArrayList<>();

        if (!hiddenPlayers.isEmpty() && seeker != null) {
            if (hiddenPlayers.contains(event.getPlayer()) || seeker.equals(event.getPlayer())) {
                for (int x = getWarp().getBlockX() - radius; x <= getWarp().getBlockX() + radius; x++) {
                    for (int y = getWarp().getBlockY() - radius; y <= getWarp().getBlockY() + radius; y++) {
                        for (int z = getWarp().getBlockZ() - radius; z <= getWarp().getBlockZ() + radius; z++) {
                            blocks.add(getWarp().getWorld().getBlockAt(x, y, z));
                        }
                    }
                }

                if (blocks.contains(event.getBlock())) {
                    if (!getPlayers().contains(event.getPlayer().getUniqueId())) {
                        PluginData.getMessageUtil().sendErrorMessage(event.getPlayer(), "You can't edit terrain where a minigame is active.");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        List<Location> locations = new ArrayList<>();

        if (!hiddenPlayers.isEmpty() && seeker != null) {
            if (hiddenPlayers.contains(event.getPlayer()) || seeker.equals(event.getPlayer())) {
                for (int x = getWarp().getBlockX() - radius; x <= getWarp().getBlockX() + radius; x++) {
                    for (int y = getWarp().getBlockY() - radius; y <= getWarp().getBlockY() + radius; y++) {
                        for (int z = getWarp().getBlockZ() - radius; z <= getWarp().getBlockZ() + radius; z++) {
                            locations.add(new Location(event.getPlayer().getWorld(), x, y, z));
                        }
                    }
                }

                if (locations.contains(event.getBlock().getLocation())) {
                    if (!getPlayers().contains(event.getPlayer().getUniqueId())) {
                        PluginData.getMessageUtil().sendErrorMessage(event.getPlayer(), "You can't edit terrain where a minigame is active.");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        if (!hiddenPlayers.isEmpty() && seeker != null) {
            if (hiddenPlayers.contains(event.getPlayer()) || seeker.equals(event.getPlayer())) {
                if (event.getMessage().startsWith("/up") || event.getMessage().startsWith("//up")) {
                    event.setCancelled(true);
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
        if(PlayerUtil.isSame(player,seeker)) {
            return ChatColor.GOLD + "<Seeker ";
        }
        else {
            return super.getGameChatTag(player);
        }
    }

    public void teleportToWarp(Player player) {
        player.teleport(getWarp(), TeleportCause_FORCE);
    }

    private void sendStartHideMessage() {
        TitleUtil.showTitle((Player) seeker, ChatColor.BLUE+" FREEZE!!!"," Wait for other players to hide.");
        for(Player player : getOnlinePlayers()) {
            if(!PlayerUtil.isSame(player,seeker)) {
                TitleUtil.showTitle(player, ChatColor.YELLOW+" HIDE!!!"," ");
            }
        }
    }

    private void sendStartSeekingMessage() {
        TitleUtil.showTitle((Player) seeker, ChatColor.YELLOW+" SEEK!!!"," Try to find the other players.");
        for(Player player : getOnlinePlayers()) {
            if(!PlayerUtil.isSame(player, seeker)) {
                TitleUtil.showTitle(player, ChatColor.BLUE+" FREEZE!!!",seeker.getName() + " is seeking you.");
                if(PlayerUtil.getOnlinePlayer(player)!=null) {
                    PluginData.getMessageUtil().sendInfoMessage(PlayerUtil.getOnlinePlayer(player), "Hold SHIFT to hide your name tag.");
                }
            }
        }
    }

    private void sendStopSeekingMessage() {
        if(hiddenPlayers.isEmpty()) {
                TitleUtil.showTitle((Player) seeker, ChatColor.GOLD+"YOU WON", "You found all players.");
            }
            else {
               TitleUtil.showTitle((Player) seeker, ChatColor.BLUE+"GAME OVER", "You found not all players.");
            }
        for(Player player : getOnlinePlayers()) {
            if(!PlayerUtil.isSame(player,seeker)) {
                if(hiddenPlayers.isEmpty()) {
                    TitleUtil.showTitle(player, ChatColor.BLUE+"GAME OVER", seeker.getName()+" found all players.");
                }
                else if (isHidden(player)) {
                    TitleUtil.showTitle(player, ChatColor.GOLD+"YOU WON", seeker.getName()+" found you not.");
                }
                else {
                    TitleUtil.showTitle(player, ChatColor.BLUE+"GAME OVER", seeker.getName()+" found you but not all players.");
                }
            }
        }
    }

    private void sendSeekerLeavingMessage(Player player) {
        GameChatUtil.sendAllInfoMessage(player, this, "The seeker left.");
    }

    private void sendPlayerFoundMessage(Player hidden) {
        PluginData.getMessageUtil().sendInfoMessage(hidden, seeker.getName() +" found you.");
        PluginData.getMessageUtil().sendInfoMessage((Player) seeker, "You found "+ hidden.getName() + ".");
    }
}
