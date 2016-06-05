/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.HideAndSeekGame;
import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.minigames.game.RaceGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameWarp extends AbstractCommand{
    
    public GameWarp(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Teleports to a minigame.");
        setUsageDescription("<name>: Teleports you to the location of minigame 'name'.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = PluginData.getGame(args[0]);
        if(game == null) {
            sendNoSuchGameErrorMessage(cs);
            return;
        }
        if(!game.isWarpAllowed()) {
            sendNotAllowed(cs);
            return;
        }
        if(args.length == 1 || game instanceof QuizGame || game instanceof HideAndSeekGame) {
            game.warp((Player)cs,game.getWarp());
        }
        else if(game instanceof RaceGame) {
            if(args[1].equalsIgnoreCase("start")) {
                game.warp((Player)cs,((RaceGame)game).getCheckpointManager().getStart().getLocation());
            }
            else if(args[1].equalsIgnoreCase("finish")) {
                game.warp((Player)cs,((RaceGame)game).getCheckpointManager().getFinish().getLocation());
            }
            else {
                try {
                    int checkId = Integer.parseInt(args[1]);
                    if(((RaceGame)game).getCheckpointManager().isIdValid(checkId)) {
                        game.warp((Player)cs,((RaceGame)game).getCheckpointManager().getCheckpoint(checkId).getLocation());
                    }
                    else {
                        sendIdNotValidMessage(cs);
                    }
                }
                catch(NumberFormatException e) {
                    sendNotANumberMessage(cs);
                }
            }
        }
        else {
            sendInvalidArguments(cs);
        }

    }

    private void sendNoSuchGameErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs,"No game with that name.");
    }

    private void sendIdNotValidMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs,"You entered an invalid checkpoint ID.");
    }

    private void sendNotANumberMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs,"You have to enter a checkpoint ID (a number)");
    }

    private void sendInvalidArguments(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs,"You entered invalid arguments for this game.");
    }

    private void sendNotAllowed(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs,"It's not allowed to warp to this game.");
    }
    
}
