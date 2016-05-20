/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.RaceGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class RaceGameShow extends AbstractGameCommand{
    
    public RaceGameShow(String... permissionNodes) {
        super(0, true, permissionNodes);
        cmdGroup = CmdGroup.RACE;
        setShortDescription(": Shows rankings for a race.");
        setUsageDescription(" [start|finish|checkpointID]: 'start' shows the starter list to all participating and spectating players. 'finish' shows the final ranking of the game. An numeric argument shows the intermediate ranking at the checkpoin with checkpointID.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game)
                        && isCorrectGameType((Player) cs, game, GameType.RACE)) {
            RaceGame raceGame = (RaceGame) game;
            if(!raceGame.isStarted()) {
                sendNotStartedMessage(cs);
                return;
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
                        sendShowCheckMessage(cs, checkId);
                    }
                }
                catch(NumberFormatException e) {
                    sendNotANumberMessage(cs);
                }
            }
        }
    }

    private void sendNotStartedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You have to start the race first.");
    }

    private void sendAutoCheckpointMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Automatically showing rank list for most recent checkpont.");
    }

    private void sendShowStartMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Showing starter list now.");
    }

    private void sendShowFinishMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Showing finish rank list now.");
    }

    private void sendShowCheckMessage(CommandSender cs,int id) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Showing intermediate rank list at checkpoint "+id+".");
    }

    private void sendNotANumberMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You have to specify the ID of a checkpoint.");
    }
}
