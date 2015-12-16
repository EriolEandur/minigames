/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.utils;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class BukkitUtil {
    
    public static boolean isSame(OfflinePlayer player1, OfflinePlayer player2) {
        if(player1 == null  || player2 == null) {
            return false;
        }
        return player1.getUniqueId().equals(player2.getUniqueId());
    }
    
    public static Player getOnlinePlayer(OfflinePlayer player) {
        return Bukkit.getPlayer(player.getUniqueId());
    }
    public static Player getOnlinePlayer(List<OfflinePlayer> playerList, OfflinePlayer player) {
        for(OfflinePlayer search : playerList) {
            if(search.getUniqueId().equals(player.getUniqueId())) {
                return getOnlinePlayer(search);
            }
        }
        return null;
    }
}
