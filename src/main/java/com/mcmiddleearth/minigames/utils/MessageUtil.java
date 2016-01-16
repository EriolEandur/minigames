/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.utils;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivanpl
 */

public class MessageUtil {
    
    @Getter
    private static final String PREFIX   = "[MiniGames] ";
    private static final String NOPREFIX = "    ";
    private static final String CHATPREFIX = "[GameChat] ";
    
    public static void sendErrorMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.RED + PREFIX + message);
        } else {
            sender.sendMessage(PREFIX + message);
        }
    }
    
    public static void sendInfoMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.AQUA + PREFIX + message);
        } else {
            sender.sendMessage(PREFIX + message);
        }
    }
    
    public static void sendAllInfoMessage(CommandSender sender, AbstractGame game, String message) {
        for(Player onlinePlayer : game.getOnlinePlayers()) {
            if(onlinePlayer!=null) {
                if(!((sender instanceof Player) && PlayerUtil.isSame((Player) sender,onlinePlayer))) {
                    onlinePlayer.sendMessage(ChatColor.AQUA + PREFIX + message);
                } 
            }
        }
        Player manager = PlayerUtil.getOnlinePlayer(game.getManager());
        if(manager!=null && !PluginData.isInGame(manager)) {
            if(!((sender instanceof Player) && PlayerUtil.isSame((Player) sender,manager))) {
                manager.sendMessage(ChatColor.AQUA + PREFIX + message);
            }
        }
    }
    
    public static void sendNoPrefixInfoMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.AQUA + NOPREFIX + message);
        } else {
            sender.sendMessage(NOPREFIX + message);
        }
    }
    
    public static void sendChatMessage(CommandSender sender, Player recipient, String message) {
        if (!(sender instanceof Player)) {
            recipient.sendMessage(ChatColor.AQUA + CHATPREFIX
                                        + ChatColor.RED+ "<Server> " 
                                        + ChatColor.WHITE + message);
        } else {
            AbstractGame game = PluginData.getGame((Player)sender);
            if(game==null) {
                if(PluginData.isSpectating((Player) sender)) {
                    recipient.sendMessage(ChatColor.AQUA + CHATPREFIX
                                        + ChatColor.YELLOW+"<Spectator " + sender.getName() + "> " 
                                        + ChatColor.WHITE + message);
                }
                else {
                    recipient.sendMessage(ChatColor.AQUA + CHATPREFIX
                                        + ChatColor.YELLOW+"<Player " + sender.getName() + "> " 
                                        + ChatColor.WHITE + message);
                }
            }
            else {
                recipient.sendMessage(ChatColor.AQUA + CHATPREFIX
                                        + game.getGameChatTag((Player) sender) + sender.getName() + "> " 
                                        + ChatColor.WHITE + message);
            }
        }
    }
    
    public static void sendBroadcastMessage(String string) {
        Bukkit.getServer().broadcastMessage(ChatColor.AQUA + PREFIX + string);
    }

}
