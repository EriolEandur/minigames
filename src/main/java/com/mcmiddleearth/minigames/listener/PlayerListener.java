/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.listener;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

/**
 *
 * @author Eriol_Eandur
 */
public class PlayerListener implements Listener{
    
    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        if(PluginData.isInGame(event.getPlayer())) {
            AbstractGame game = PluginData.getGame(event.getPlayer());
            game.playerMove(event);
        }
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
        else if(PluginData.gameRunning()) {
            MessageUtil.sendInfoMessage(event.getPlayer(),"There is a game going on. For more information type /game check.");
        }
    }
    
    @EventHandler
    public void playerTeleport(PlayerTeleportEvent event) {
        if(PluginData.isInGame(event.getPlayer())) {
            AbstractGame game = PluginData.getGame(event.getPlayer());
            game.playerTeleport(event);
        }
    }
    
    @EventHandler
    public void playerToggleFlight(PlayerToggleFlightEvent event) {
        if(PluginData.isInGame(event.getPlayer())) {
            AbstractGame game = PluginData.getGame(event.getPlayer());
            game.playerToggleFlight(event);
        }
    }
    
    @EventHandler
    public void playerChangeGameMode(PlayerGameModeChangeEvent event) {
        if(PluginData.isInGame(event.getPlayer())) {
            AbstractGame game = PluginData.getGame(event.getPlayer());
            game.playerChangeGameMode(event);
        }
    }
}
