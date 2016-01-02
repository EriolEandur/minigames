/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.RaceGame;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class RaceGameShow extends AbstractGameCommand{
    
    public RaceGameShow(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": ");
        setUsageDescription(": ");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game)
                        && isCorrectGameType((Player) cs, game, GameType.RACE)) {
            RaceGame raceGame = (RaceGame) game;
            if(!raceGame.isStarted()) {
                sendNotStartedMessage(cs);
            }
            if(args.length<1) {
                raceGame.showAuto();
                sendAutoCheckpointMessage(cs);
            }
            else if(args[0].equalsIgnoreCase("start")) {
                raceGame.showStart();
                sendShowStartMessage(cs);
            }
            else if(args[0].equalsIgnoreCase("finish")) {
                raceGame.showFinish();
                sendShowFinishMessage(cs);
            }
            else {
                try {
                    int checkId = Integer.parseInt(args[0]);
                    if(!raceGame.getCheckpointManager().isIdValid(checkId)) {
                        sendNotANumberMessage(cs);
                    }
                    else {
                        raceGame.showCheckpoint(checkId);
                        sendShowCheckMessage(cs);
                    }
                }
                catch(NumberFormatException e) {
                    sendNotANumberMessage(cs);
                }
            }
        }
    }

    private void sendNotStartedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You have to started the race first.");
    }

    private void sendAutoCheckpointMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Automatically showing checkponts.");
    }

    private void sendShowStartMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Showing starter list now.");
    }

    private void sendShowFinishMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Showing finish rank list now.");
    }

    private void sendShowCheckMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Showing checkpoint list now.");
    }

    private void sendNotANumberMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You have to specify the ID of a checkpoint.");
    }
}
