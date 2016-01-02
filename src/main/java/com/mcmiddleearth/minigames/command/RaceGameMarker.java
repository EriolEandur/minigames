/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.RaceGame;
import com.mcmiddleearth.minigames.raceCheckpoint.CheckpointManager;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import java.io.FileNotFoundException;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class RaceGameMarker extends AbstractGameCommand {
    
    
    public RaceGameMarker(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": ");
        setUsageDescription(": ");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game)
                        && isCorrectGameType((Player) cs, game, GameType.RACE)) {
            RaceGame raceGame = (RaceGame) game;
            if(raceGame.isSteady()){
                sendNotWhileSteadyMessage(cs);
                return;
            }
            CheckpointManager checkpointManager = raceGame.getCheckpointManager();
            Location loc = ((Player) cs).getLocation();
            try {
                if(args.length>1) {
                    if(args[1].equalsIgnoreCase("start")) {
                        checkpointManager.setStartMarker(args[0]);
                        sendStartMarkerMessage(cs);
                    } else if(args[1].equalsIgnoreCase("finish")) {
                        checkpointManager.setFinishMarker(args[0]);
                        sendFinishMarkerMessage(cs);
                    } else if(args[1].equalsIgnoreCase("checkpoint")) {
                        checkpointManager.setCheckpointMarker(args[0]);
                        sendCheckMarkerMessage(cs);
                    } else if(args[1].equalsIgnoreCase("all")) {
                        checkpointManager.setStartMarker(args[0]);
                        checkpointManager.setFinishMarker(args[0]);
                        checkpointManager.setCheckpointMarker(args[0]);
                        sendAllMarkerMessage(cs);
                    } else {
                        sendInvalidArgumentMessage(cs);
                    }
                }
                else {
                    if(checkpointManager.setMarker(args[0], loc)) {
                        sendMarkerMessage(cs);
                    }
                    else {
                        sendNotNearMessage(cs);
                    }
                }
            } catch(FileNotFoundException e) {
                sendFileNotFoundMessage(cs);
            }
        }
    }

    private void sendStartMarkerMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Start marker loaded.");
    }

    private void sendFinishMarkerMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Finish marker loaded.");
    }

    private void sendCheckMarkerMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Markers for all checkpoints loaded.");
    }

    private void sendMarkerMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Marker for this Checkpoint loaded.");
    }
    
    private void sendInvalidArgumentMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Invalid Argument. Try: /game marker <filename> [start|finish|checkpoint|all]");
    }

    private void sendFileNotFoundMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Marker file not found.");
    }

    private void sendNotNearMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You are not near a checkpoint.");
    }

    private void sendNotWhileSteadyMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You can't change markers while players are steady.");
    }

    private void sendAllMarkerMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "All Markers loaded.");
    }

}
