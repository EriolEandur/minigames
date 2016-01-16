/*
 * Copyright (C) 2016 MCME
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
package com.mcmiddleearth.minigames.utils;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class TitleUtil {

    public static void _invalid_showTitle(Player player, String color, String title, String subtitle) {
        player.sendTitle(color+title, subtitle);
    }
    
    public static void showTitle(Player player, String color, String title, String subtitle) {
        showTitle(player, color, title, subtitle, 20,80,20);
    }
    
    public static void showTitle(Player player, String color, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if(false) {          
            sendTitle(player, fadeIn, stay, fadeOut, title, subtitle);
        }
        else {
            setTimes(player,fadeIn, stay, fadeOut);
            setTitle(player, color, title);
            setSubtitle(player, subtitle);
        }
    }
    
    public static void setTimes(Player player, int fadeIn, int stay, int fadeOut) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+player.getName()+" times +"+fadeIn+" "+stay+" "+fadeOut);
    }
    
    public static void setTitle(Player player, String color, String title) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+player.getName()+" title "+"{text:\""+title+"\",color:"+color+"}");
    }
    
    public static void setSubtitle(Player player, String subtitle) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title "+player.getName()+" subtitle "+"\""+subtitle+"\"");
    }
    
    public static void showTitleAll(List<Player> playerList, List<Player> except, String color, String title, String subtitle) {
        for(Player player: playerList) {
            if(!PlayerUtil.isPlayerInList(except, player)) {
                showTitle(player,color,title, subtitle);
            }
        }
    }
    
    public static void showTitleAll(List<Player> playerList, Player except, String color, String title, String subtitle) {
        for(Player player: playerList) {
            if(!PlayerUtil.isSame(player, except)) {
                showTitle(player,color,title, subtitle);
            }
        }
    }

    public static void setTitleAll(List<Player> playerList, Player except, String color, String title) {
        for(Player player: playerList) {
            if(!PlayerUtil.isSame(player, except)) {
                setTitle(player,color,title);
            }
        }
    }

    public static void setSubtitleAll(List<Player> playerList, Player except, String subtitle) {
        for(Player player: playerList) {
            if(!PlayerUtil.isSame(player, except)) {
                setSubtitle(player,subtitle);
            }
        }
    }

    public static void setTimesAll(List<Player> playerList, Player except, int fadeIn, int stay, int fadeOut) {
        for(Player player: playerList) {
            if(!PlayerUtil.isSame(player, except)) {
                setTimes(player,fadeIn, stay, fadeOut);
            }
        }
    }

    
    //Invalid
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
