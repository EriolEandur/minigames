/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameManager extends AbstractGameCommand{
    
    public GameManager(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Makes another player manager of a game.");
        setUsageDescription(" <player>: Makes <player> new manager of the game. <player> must have game manager permission.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game)) {
            Player newManager = Bukkit.getServer().getPlayer(args[0]); 
            if(newManager == null) {
                sendPlayerNotFoundMessage(cs);
            }
            else {
                if(isAlreadyManager(newManager)) {
                    return;
                }
                if(!hasPermissions(newManager)) {
                    sendNoPermissionMessage(cs);
                }
                else {
                    game.setManager(newManager);
                    sendNewManagerMessage(cs, newManager,game.getName(), game);
                }
            }
        }
    }

    private void sendPlayerNotFoundMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Player not found.");
    }

    private void sendNoPermissionMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Player has no permission to manage a game (default: Guides only)");
    }

    private void sendNewManagerMessage(CommandSender cs, Player newManager, String name, AbstractGame game) {
        MessageUtil.sendInfoMessage(cs,"You tranfered the management of the game "+name+" to "+newManager.getName()+".");
        MessageUtil.sendInfoMessage(newManager, "You are now manager of the minigame "+name+".");
        MessageUtil.sendAllInfoMessage(newManager, game, newManager.getName() +" is now managing this game.");
    }
    
 }
