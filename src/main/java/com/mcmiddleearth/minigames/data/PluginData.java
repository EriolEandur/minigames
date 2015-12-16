/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.data;

import com.mcmiddleearth.minigames.utils.DynmapUtil;
import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.utils.BukkitUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Eriol_Eandur
 */
public class PluginData {
    
    private static final List<OfflinePlayer> noGameChat = new ArrayList<>();
    
    @Getter
    private static final List<AbstractGame> games = new ArrayList<>();
    
    public static AbstractGame getGame(Player player) {
        for(AbstractGame game : games) {
            if(BukkitUtil.isSame(game.getManager(),player)) {
                return game;
            }
            for(OfflinePlayer search: game.getPlayers()) {
                if(BukkitUtil.isSame(search,player)) {
                    return game;
                }
            }
        }
        return null;
    }
    
    public static AbstractGame getGame(String name) {
        for(AbstractGame game : games) {
            if(game.getName().equalsIgnoreCase(name)) {
                return game;
            }
        }
        return null;
    }
    
    public static void addGame(AbstractGame game) {
        games.add(game);
    }
    
    public static void removeGame(AbstractGame game) {
        games.remove(game);
    }
    
    public static boolean isInGame(Player player) {
        for(AbstractGame game : games) {
            return game.isInGame(player);
        }
        return false;
    }
    
    public static boolean isManager(Player player) {
        for(AbstractGame game : games) {
            if(BukkitUtil.isSame(game.getManager(),player)) {
                return true;
            }
        }
        return false;
    }

    public static void setGameChat(Player player, boolean b) {
        if(!b) {
            noGameChat.add(player);
        }
        else {
            noGameChat.remove(player);
        }
    }

    public static boolean getGameChat(OfflinePlayer player) {
        for(OfflinePlayer search: noGameChat) {
            if(BukkitUtil.isSame(search,player)) {
                return false;
            }
        }
       return true;
    }
    
    public static DynmapUtil getDynmap() {
            Plugin dynmap = MiniGamesPlugin.getPluginInstance().getServer().getPluginManager().getPlugin("dynmap");
            if(dynmap==null) {
                Logger.getGlobal().info("Dynmap not found");
                return null;
            }
            else {
                return new DynmapUtil((JavaPlugin) dynmap);
            }
    }
}
