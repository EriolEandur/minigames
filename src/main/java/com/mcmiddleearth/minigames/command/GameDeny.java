/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.pluginutils.message.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameDeny extends AbstractGameCommand{
    
    public GameDeny(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Denies various actions for a game.");
        setUsageDescription(" flight|teleport|join|warp: flight/teleport allows for players in the game to fly or teleport. join allows players to join without invitation. warp allows player to warp to game location.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game!=null && isManager((Player) cs, game)) {
            if(args[0].equalsIgnoreCase("warp")) {
                game.setWarpAllowed(false);
                sendWarpAllowedMessage(cs);
            } 
            else if(args[0].equalsIgnoreCase("join")) {
                game.setPrivat(true);
                sendJoinAllowedMessage(cs);
            } 
            else if(args[0].equalsIgnoreCase("spectate")) {
                game.setSpectateAllowed(false);
                sendSpectateAllowedMessage(cs);
            }
            else if(args[0].equalsIgnoreCase("flight")) {
                game.setFlightAllowed(false);
                sendFlightAllowedMessage(cs);
            } 
            else if(args[0].equalsIgnoreCase("teleport")) {
                game.setTeleportAllowed(false);
                sendTeleportAllowedMessage(cs);
            } 
        }
    }
    
    private void sendWarpAllowedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You denied all players to warp to this game.");
    }

    private void sendJoinAllowedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Only invited players may join the game now.");
    }

    private void sendSpectateAllowedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You denied all players to spectate to this game.");
    }
    private void sendFlightAllowedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You denied players of this game to fly.");
    }

    private void sendTeleportAllowedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You denied players of this game to teleport (using commands like /tpa and /warp).");
    }
 }
