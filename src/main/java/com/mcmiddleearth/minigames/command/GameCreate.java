/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.HideAndSeekGame;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameCreate extends AbstractGameCommand{
    
    public GameCreate(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Creates a new mini game.");
        setUsageDescription(": ");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        if(!isAlreadyInGame((Player)cs) && !isAlreadyManager((Player) cs)) {
            if(PluginData.getGame(args[1]) != null) {
                sendGameExistsMessage(cs);
                return;
            }
            HideAndSeekGame game;
            GameType type = GameType.getGameType(args[0]);
            if(type==null) {
                sendInvalidGameTypeErrorMessage(cs);
                return;
            }
            switch(type) {
                case HIDE_AND_SEEK:
                    game = new HideAndSeekGame((Player) cs, args[1]);
                    sendNewHaSGameMessage(cs, game);
                    break;
                default:
                    sendInvalidGameTypeErrorMessage(cs);
                    return;
            }
            PluginData.addGame(game);
            sendGameCreateMessage(cs);
        }
    }
    
    public void sendGameCreateMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You created a new minigame.");
    }

    private void sendInvalidGameTypeErrorMessage(CommandSender cs) {
         MessageUtil.sendErrorMessage(cs, "You specified an invalid game type.");
    }
    
    private void sendGameExistsMessage(CommandSender cs) {
         MessageUtil.sendErrorMessage(cs, "A game with that name already exists.");
    }

    private void sendNewHaSGameMessage(CommandSender cs, HideAndSeekGame game) {
        MessageUtil.sendBroadcastMessage(cs.getName() + " started a new Hide and Seek game. "
                                        + "To play that game, type in chat: /game join "+game.getName());
    }
 }
