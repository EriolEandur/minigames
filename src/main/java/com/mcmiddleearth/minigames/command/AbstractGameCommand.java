/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.minigames.utils.BukkitUtil;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public abstract class AbstractGameCommand extends AbstractCommand {

    public AbstractGameCommand(int minArgs, boolean playerOnly, String... permissionNodes) {
        super(minArgs, playerOnly, permissionNodes);
    }
    
    protected AbstractGame getGame(Player player) {
        AbstractGame game = PluginData.getGame(player);
        if(game==null) {
            sendNotInGameErrorMessage(player);
        }
        return game;
    }
    
    protected boolean isAlreadyInGame(Player player) {
        if(PluginData.isInGame(player)) {
            sendAlreadyInGameErrorMessage(player);
            return true;
        }
        return false;
    }
    
    protected boolean isManager(Player player, AbstractGame game) {
        if(BukkitUtil.isSame(game.getManager(),player)) {
            return true;
        }
        else {
            sendNotManagerErrorMessage(player);
            return false;
        }
    }
    
    protected boolean isAlreadyManager(Player player) {
        if(PluginData.isManager(player)) {
            sendAlreadyManagerErrorMessage(player);
            return true;
        }
        return false;
    }
            
    protected boolean isAlreadyManagerOfOtherGame(Player player, AbstractGame game) {
        if(!BukkitUtil.isSame(game.getManager(),player) && PluginData.isManager(player)) {
            sendAlreadyManagerOfOtherGameErrorMessage(player);
            return true;
        }
        return false;
    }
    
    protected boolean isCorrectGameType(Player player, AbstractGame game, GameType gameType) {
        if(gameType.associatedClass().isInstance(game)) {
            return true;
        }
        else {
            sendWrongGameTypeErrorMessage(player, gameType);
            return false;
        }
    }
    
    protected void sendWrongGameTypeErrorMessage(CommandSender cs, GameType gameType) {
        MessageUtil.sendErrorMessage(cs, "This is not a "+gameType.toString()+" game.");
    }

    private void sendNotInGameErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You are not part of a mini game.");
    }
    
    private void sendAlreadyInGameErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You are already part of a mini game.");
    }

    private void sendNotManagerErrorMessage(Player player) {
        MessageUtil.sendErrorMessage(player, "You are not the manager of this game.");
    }

    private void sendAlreadyManagerErrorMessage(Player player) {
        MessageUtil.sendErrorMessage(player, "You are already the manager of a game.");
    }

    private void sendAlreadyManagerOfOtherGameErrorMessage(Player player) {
        MessageUtil.sendErrorMessage(player, "You are already the manager of an other game.");
    }
    
    
}
