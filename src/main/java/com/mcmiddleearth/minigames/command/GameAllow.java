/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameAllow extends AbstractGameCommand{
    
    public GameAllow(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Allows various actions for a game.");
        setUsageDescription(" flight|teleport|join|warp|spectate: flight/teleport allows for players in the game to fly or teleport. join allows players to join without invitation. warp allows player to warp to game location. spectate allows players to see scoreboard of the game without participating.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game!=null && isManager((Player) cs, game)) {
            if(args[0].equalsIgnoreCase("warp")) {
                game.setWarpAllowed(true);
                sendWarpAllowedMessage(cs);
            } 
            else if(args[0].equalsIgnoreCase("join")) {
                game.setPrivat(false);
                sendJoinAllowedMessage(cs);
            } 
            else if(args[0].equalsIgnoreCase("spectate")) {
                game.setSpectateAllowed(true);
                sendSpectateAllowedMessage(cs);
            } 
            else if(args[0].equalsIgnoreCase("flight")) {
                game.setFlightAllowed(true);
                sendFlightAllowedMessage(cs);
            } 
            else if(args[0].equalsIgnoreCase("teleport")) {
                game.setTeleportAllowed(true);
                sendTeleportAllowedMessage(cs);
            } 
            else {
                sendInvalidArgumentMessage(cs);
            }
        }
    }
    
    private void sendWarpAllowedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You allowed all players to warp to this game.");
    }

    private void sendJoinAllowedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You allowed all players to join this game.");
    }
    
    private void sendSpectateAllowedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You allowed all players to spectate to this game.");
    }

    private void sendFlightAllowedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You allowed players of this game to fly.");
    }

    private void sendTeleportAllowedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You allowed players of this game to teleport (using commands like /tpa and /warp).");
    }

    private void sendInvalidArgumentMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Invalid Argument.");
    }
}
