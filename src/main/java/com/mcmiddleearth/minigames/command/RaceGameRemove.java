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
public class RaceGameRemove extends AbstractGameCommand{
    
    public RaceGameRemove(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": ");
        setUsageDescription(": ");
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
            if(args.length==0) {
                if(raceGame.getCheckpointManager().removeCheckpointLocation(loc)) {
                    sendRemovedMessage(cs);
                    return;
                }
                sendNotNearMessage(cs);
            } 
            else {
                try {
                    int id = Integer.parseInt(args[0]);
                    if(raceGame.getCheckpointManager().removeCheckpointLocation(id)) {
                        sendRemovedMessage(cs);
                        return;
                    }
                    sendIdNotValidException(cs);
                }
                catch(NumberFormatException e){
                    sendNotANumberException(cs);
                }
            }
        }
    }

    private void sendRemovedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Checkpoint removed.");
    }

    private void sendNotNearMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "No Checkpoint found near you.");
    }

    private void sendIdNotValidException(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "No Checkpoint with that ID.");
    }

    private void sendNotANumberException(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Not a number.");
    }

}
