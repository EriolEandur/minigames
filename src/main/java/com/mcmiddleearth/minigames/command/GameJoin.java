/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.utils.GameChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameJoin extends AbstractGameCommand{
    
    public GameJoin(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Joins a minigame.");
        setUsageDescription(" <gamename>: The player issuing this command joins the game with name <gamename>.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        if(!isAlreadyInGame((Player)cs)) {
            AbstractGame game = PluginData.getGame(args[0]);
            if(game == null) {
                sendNoSuchGameErrorMessage(cs);
            }
            else {
                if(isAlreadyManagerOfOtherGame((Player) cs, game)) {
                    return;
                }
                if(game.isBanned((Player) cs)) {
                    sendPlayerBannedMessage(cs);
                }
                else {
                    if(game.joinAllowed()) {
                        if(game.isPrivat() && !game.isInvited((Player) cs)) {
                            sendNotInvitedMessage(cs);
                            return;
                        }
                        PluginData.stopSpectating((Player)cs);
                        game.addPlayer((Player) cs);
                        PluginData.setGameChat((Player) cs,true);
                        sendPlayerJoinMessage(cs, game);
                    }
                    else {
                        sendJoinNotAllowedMessage(cs);
                    }
                }
            }
        }
    }
    
    public void sendPlayerJoinMessage(CommandSender cs, AbstractGame game) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You joined the minigame "+ game.getName()
                                   +". Please use the game chat with "+PluginData.getMessageUtil().STRESSED+"/gc <message>");
        GameChatUtil.sendAllInfoMessage(cs, game, cs.getName()+" joined the game.");
    }

    public void sendNoSuchGameErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "No minigame with that name.");
    }

    private void sendPlayerBannedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You are banned from this minigame.");
    }

    private void sendJoinNotAllowedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You can't join this game at the moment, try later.");
    }

    private void sendNotInvitedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "The game is private. You can ask the manager to invite you.");
    }

 }
