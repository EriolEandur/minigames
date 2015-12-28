/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames;

import com.mcmiddleearth.minigames.command.GCCommandExecutor;
import com.mcmiddleearth.minigames.command.GameCommandExecutor;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.listener.PlayerListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Eriol_Eandur
 */
public class MiniGamesPlugin extends JavaPlugin{
 
    @Getter
    private static MiniGamesPlugin pluginInstance;

    @Override
    public void onEnable() {
        pluginInstance = this;
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getCommand("game").setExecutor(new GameCommandExecutor());
        getCommand("gc").setExecutor(new GCCommandExecutor());
        PluginData.createConversationFactories();
        getLogger().info("Enabled!");
    }
}
