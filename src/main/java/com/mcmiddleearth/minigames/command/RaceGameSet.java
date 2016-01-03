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
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class RaceGameSet extends AbstractGameCommand{
    
    public RaceGameSet(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Defines a race game location.");
        setUsageDescription(" start|finish|checkpoint [checkpointID] [-i]: With argument start or finish defines the start or finish of the game. With argument checkpoint defines a race checkpoint which racing players have to visit in proper order. Without further arguments after 'checkpoint' a new checkpoint is added after the last existing checkpoint. With argument 'checkpointID' the checkpoint with that ID is moved to your location. With additional optioin '-i' a new checkpoint is inserted in front of the checkpoint with ID 'checkpointID'.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game)
                        && isCorrectGameType((Player) cs, game, GameType.RACE)) {
            if(game.isAnnounced()) {
                sendAlreadyAnnouncedErrorMessage(cs);
                return;
            }
            RaceGame raceGame = (RaceGame) game;
            Location loc = ((Player) cs).getLocation();
            if(args[0].equalsIgnoreCase("start")) {
                if(raceGame.getCheckpointManager().setStartLocation(loc)) {
                    sendStartSetMessage(cs);
                    return;
                }
            } else if(args[0].equalsIgnoreCase("finish")) {
                if(raceGame.getCheckpointManager().setFinishLocation(loc)) {
                    sendFinishSetMessage(cs);
                    return;
                }
            } else if(args[0].equalsIgnoreCase("checkpoint")) {
                if(args.length<2) {
                    if(raceGame.getCheckpointManager().addCheckpointLocation(loc)) {
                        sendCheckpointAddedMessage(cs);
                        return;
                    }
                }
                else {
                    try {
                        int id = Integer.parseInt(args[1]);
                        if(raceGame.getCheckpointManager().isIdValid(id)) {
                            if(args.length>2 && args[2].equals("-i")) {
                                raceGame.getCheckpointManager().insertCheckpointLocation(loc, id);
                                sendCheckpointAddedMessage(cs);
                            }
                            else {
                                raceGame.getCheckpointManager().setCheckpointLocation(loc, id);
                                sendCheckpointMovedMessage(cs);
                            }
                            return;
                        }
                        sendIdNotValidMessage(cs);
                        return;
                    }
                    catch(NumberFormatException e) {
                        sendNotANumberMessage(cs);
                        return;
                    }
                }
            } else {
                sendInvalidArgumentMessage(cs);
                return;
            }
            sendInvalidLocationMessage(cs);
        }
    }

    private void sendStartSetMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Start location saved.");
    }

    private void sendFinishSetMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Finish location saved.");
    }

    private void sendCheckpointAddedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Checkpoint saved.");
    }

    private void sendIdNotValidMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You did not specify a valid Checkpoint ID.");
    }

    private void sendNotANumberMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Not a number.");
    }

    private void sendInvalidArgumentMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Invalid Argument. Usage: /game set start|finish|checkpoint.");
    }

    private void sendInvalidLocationMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Your location is too near to another Checkpoint.");
    }

    private void sendCheckpointMovedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Checkpoint moved to your location.");
    }
    
}
