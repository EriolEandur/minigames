package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.HideAndSeekGame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Planetology
 */
public class HaSGameUnstuck extends AbstractGameCommand implements Listener {

    private HideAndSeekGame hasGame;
    private List<String> moved = new ArrayList<>();
    private List<String> request = new ArrayList<>();

    public HaSGameUnstuck(String... permissionNodes) {
        super(0, true, permissionNodes);

        Bukkit.getServer().getPluginManager().registerEvents(this, MiniGamesPlugin.getPluginInstance());
        cmdGroup = CmdGroup.HIDE_AND_SEEK;

        setShortDescription("Teleports you back to the game warp.");
        setUsageDescription("Teleports you back to the game warp after not moving for 10sec as a hider and 5sec as a seeker.");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isCorrectGameType((Player) cs, game, GameType.HIDE_AND_SEEK)) {
            hasGame = (HideAndSeekGame) game;
            Player player = (Player) cs;

            if (((HideAndSeekGame) game).isSeeking()) {
                if (!request.contains(player.getName())) {
                    moved.remove(player.getName());

                    if (hasGame.getHiddenPlayers().contains(player)) {
                        PluginData.getMessageUtil().sendInfoMessage(player, "You will get teleported back to the game warp in 10 seconds, don't move.");
                        request.add(player.getName());

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!moved.contains(player.getName())) {
                                    ((HideAndSeekGame) game).teleportToWarp(player);
                                    request.remove(player.getName());
                                }
                            }
                        }.runTaskLater(MiniGamesPlugin.getPluginInstance(), 20 * 10);
                    }

                    if (hasGame.getSeeker().equals(player)) {
                        PluginData.getMessageUtil().sendInfoMessage(player, "You will get teleported back to the game warp in 5 seconds, don't move.");
                        request.add(player.getName());

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!moved.contains(player.getName())) {
                                    ((HideAndSeekGame) game).teleportToWarp(player);
                                    request.remove(player.getName());
                                }
                            }
                        }.runTaskLater(MiniGamesPlugin.getPluginInstance(), 20 * 5);
                    }
                } else {
                    PluginData.getMessageUtil().sendErrorMessage(player, "You are already waiting to get teleported.");
                }
            } else {
                PluginData.getMessageUtil().sendErrorMessage(player, "Seeking has not started yet.");
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (hasGame != null && hasGame.isSeeking()) {
            if (hasGame.getHiddenPlayers().contains(player) || hasGame.getSeeker().equals(player)) {
                if (!moved.contains(player.getName()) && request.contains(player.getName())) {
                    Location from = event.getFrom();
                    Location to = event.getTo();

                    if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()) {
                        moved.add(player.getName());
                        PluginData.getMessageUtil().sendErrorMessage(player, "Don't move when waiting to get teleported to the game warp, teleport cancelled.");
                        request.remove(player.getName());
                    }
                }
            }
        }
    }
}
