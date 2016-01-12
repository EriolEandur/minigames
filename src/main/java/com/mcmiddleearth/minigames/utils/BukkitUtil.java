/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.utils;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
        if(player!=null) {
            return Bukkit.getPlayer(player.getUniqueId());
        }
        return null;
    }
    
    public static Player getOnlinePlayer(List<OfflinePlayer> playerList, OfflinePlayer player) {
        return getOnlinePlayer(getOfflinePlayer(playerList, player));
    }
    
    public static OfflinePlayer getOfflinePlayer(List<OfflinePlayer> playerList, OfflinePlayer player) {
        for(OfflinePlayer search : playerList) {
            if(search.getUniqueId().equals(player.getUniqueId())) {
                return search;
            }
        }
        return null;
    }
    
    public static boolean isPlayerInList(List playerList, OfflinePlayer player) {
        for(Object search: playerList) {
            if(isSame((OfflinePlayer)search,player)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isSameBlock(Location loc1, Location loc2) {
        return loc1.getBlockX()==loc2.getBlockX() 
            && loc1.getBlockY()==loc2.getBlockY() 
            && loc1.getBlockZ()==loc2.getBlockZ(); 
    }
        
    public static boolean isTransparent(Location loc) {
        Material mat = loc.getBlock().getType();
        switch(mat) {
            case AIR:
            case GLASS:
            case LONG_GRASS:
            case STRING:
            case FLOWER_POT:
            case WALL_SIGN:
            case SIGN_POST:
            case LEAVES:
            case STAINED_GLASS:
            case WEB:
            case DEAD_BUSH:
            case TORCH:
            case REDSTONE_TORCH_ON:
            case REDSTONE_TORCH_OFF:
            case LEVER:
            case WOOD_BUTTON:
            case STONE_BUTTON:
            case TRAP_DOOR:
            case WOOD_DOOR:
            case IRON_DOOR:
            case WOOD_PLATE:
            case STONE_PLATE:
            case IRON_PLATE:
            case GOLD_PLATE:
            case THIN_GLASS:
            case VINE:
            case IRON_FENCE:
            case FENCE:
            case SAPLING:
            case ANVIL:
            case ENCHANTMENT_TABLE:
            case DOUBLE_PLANT:
            case SNOW:
            case CARPET:
            case STAINED_GLASS_PANE:
            case BED_BLOCK:
            case SKULL:
            case LADDER:
            case RAILS:
            case POWERED_RAIL:
            case ACTIVATOR_RAIL:
            case DETECTOR_RAIL:
            case STANDING_BANNER:
            case WALL_BANNER:
                return true;
            default: 
                return false;
        }
    }
    

    
}
