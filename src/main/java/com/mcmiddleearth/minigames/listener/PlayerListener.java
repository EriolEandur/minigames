/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.listener;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Eriol_Eandur
 */
public class PlayerListener implements Listener{
    
    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        //if(event.getFrom().getBlock()!=event.getTo().getBlock()) {
            AbstractGame game = PluginData.getGame(event.getPlayer());
            if(PluginData.isInGame(event.getPlayer())) {
                game.playerMove(event);
            }
        //}
    }
    
    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        if(PluginData.isInGame(event.getPlayer())) {
            PluginData.getGame(event.getPlayer()).playerLeaveServer(event);
        }
    }
    
    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        if(PluginData.isInGame(event.getPlayer())) {
            PluginData.getGame(event.getPlayer()).playerJoinServer(event);
        }
    }
    
}
