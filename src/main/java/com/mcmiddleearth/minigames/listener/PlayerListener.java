/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.listener;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.*;

/**
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
            new FancyMessage(MessageType.INFO,PluginData.getMessageUtil())
                .addClickable("There is a game going on. For more information "
                                +PluginData.getMessageUtil().STRESSED+"click here"+PluginData.getMessageUtil().INFO+" or type /game check.",
                        "/game check")
                .send(event.getPlayer());
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
