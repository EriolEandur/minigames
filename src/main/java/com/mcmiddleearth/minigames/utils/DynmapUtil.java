/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.utils;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Eriol_Eandur
 */
public class DynmapUtil {
    
    private final JavaPlugin dynmapPlugin;
    
    public DynmapUtil(JavaPlugin dynmapPlugin) {
        this.dynmapPlugin = dynmapPlugin;
    }
    
    public void hide(Player player) {
        dynmapPlugin.getCommand("dynmap").execute(player, "dynmap", new String[]{"hide"});
    }
    
    public void show(Player player) {
        dynmapPlugin.getCommand("dynmap").execute(player, "dynmap", new String[]{"show"});
    }
}
