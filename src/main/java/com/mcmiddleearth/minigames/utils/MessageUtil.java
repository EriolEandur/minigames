/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.utils;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import java.lang.reflect.Constructor;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Ivanpl
 */

public class MessageUtil {
    
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
                if(!((sender instanceof Player) && BukkitUtil.isSame((Player) sender,onlinePlayer))) {
                    onlinePlayer.sendMessage(ChatColor.AQUA + PREFIX + message);
                } 
            }
        }
        Player manager = BukkitUtil.getOnlinePlayer(game.getManager());
        if(manager!=null && !PluginData.isInGame(manager)) {
            if(!((sender instanceof Player) && BukkitUtil.isSame((Player) sender,manager))) {
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
                                        + ChatColor.RED+ "<Server>" 
                                        + ChatColor.WHITE + message);
        } else {
            AbstractGame game = PluginData.getGame((Player)sender);
            if(game==null) {
                recipient.sendMessage(ChatColor.AQUA + CHATPREFIX
                                        + ChatColor.YELLOW+"<Spectator " + sender.getName() + "> " 
                                        + ChatColor.WHITE + message);
            }
            else {
                recipient.sendMessage(ChatColor.AQUA + CHATPREFIX
                                        + game.getGameChatTag((Player) sender) + sender.getName() + "> " 
                                        + ChatColor.WHITE + message);
            }
        }
    }
    
    public static void showTitle(Player player, String color, String title, String subtitle) {
        if(false) {          
            sendTitle(player, 20, 20, 20, title, subtitle);
        }
        else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+player.getName()+" times 20 80 20");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+player.getName()+" subtitle "+"\""+subtitle+"\"");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+player.getName()+" title "+"{text:\""+title+"\",color:"+color+"}");
        }
    }

    public static void sendBroadcastMessage(String string) {
        Bukkit.getServer().broadcastMessage(ChatColor.AQUA + PREFIX + string);
    }
    
    private static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        try {
            if (title != null) {
                title = ChatColor.translateAlternateColorCodes('&', title);
                title = title.replaceAll("%player%", player.getDisplayName());
                Object enumTitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
                Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + title + "\"}");
                Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
                Object titlePacket = titleConstructor.newInstance(enumTitle, chatTitle, fadeIn, stay, fadeOut);
                sendPacket(player, titlePacket);
            }

            if (subtitle != null) {
                subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
                subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
                Object enumSubtitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
                Object chatSubtitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + subtitle + "\"}");
                Constructor<?> subtitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
                Object subtitlePacket = subtitleConstructor.newInstance(enumSubtitle, chatSubtitle, fadeIn, stay, fadeOut);
                sendPacket(player, subtitlePacket);
            }
        } catch (Exception e) {
            Logger.getGlobal().info("sendTitle " + e.toString());
        }
    }
    
    private static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            Logger.getGlobal().info(e.toString());
            return null;
        }
    }

    private static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            Logger.getGlobal().info(e.toString());
        }
    }


}
