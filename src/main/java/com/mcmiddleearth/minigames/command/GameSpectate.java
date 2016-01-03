/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameSpectate extends AbstractGameCommand{
    
    public GameSpectate(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Spectate at a game.");
        setUsageDescription(" <gamename>: Switches on spectating a game wich is watching the game scoreboard.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        if(args[0].equalsIgnoreCase("!off")) {
            PluginData.stopSpectating((Player)cs);
        }
        if(!isAlreadyInGame((Player)cs) && !isAlreadyManager((Player) cs)) {
            AbstractGame game = PluginData.getGame(args[0]);
            if(game == null) {
                sendNoSuchGameErrorMessage(cs);
            }
            else {
                if(game.isBanned((Player) cs)) {
                    sendPlayerBannedMessage(cs);
                }
                else {
                    if(game.isSpectateAllowed()) {
                        game.addSpectator((Player) cs);
                        PluginData.setGameChat((Player) cs,true);
                        sendPlayerSpectatesMessage(cs, game);
                    }
                    else {
                        sendSpectateNotAllowedMessage(cs);
                    }
                }
            }
        }
    }
    
    public void sendPlayerSpectatesMessage(CommandSender cs, AbstractGame game) {
        MessageUtil.sendInfoMessage(cs, "You now spectates at the minigame "+ game.getName()
                                   +". Please use the game chat whith /gc <message>");
        MessageUtil.sendAllInfoMessage(cs, game, cs.getName()+" now spectates at the game.");
    }

    public void sendNoSuchGameErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "No minigame with that name.");
    }

    private void sendPlayerBannedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You are banned from this minigame.");
    }

    private void sendSpectateNotAllowedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "It is not allowed to spectate at this game.");
    }

 }
