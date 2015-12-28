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
        setShortDescription(": ");
        setUsageDescription(": ");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game)
                        && isCorrectGameType((Player) cs, game, GameType.LORE_QUIZ)) {
            if(game.isAnnounced()) {
                sendAlreadyAnnouncedErrorMessage(cs);
                return;
            }
            RaceGame raceGame = (RaceGame) game;
            Location loc = ((Player) cs).getLocation();
            if(args[0].equalsIgnoreCase("start")) {
                if(raceGame.setStartLocation(loc)) {
                    sendStartSetMessage(cs);
                    return;
                }
            } else if(args[0].equalsIgnoreCase("finish")) {
                if(raceGame.setFinishLocation(loc)) {
                    sendFinishSetMessage(cs);
                    return;
                }
            } else if(args[0].equalsIgnoreCase("checkpoint")) {
                if(args.length<2) {
                    if(raceGame.setFinishLocation(loc)) {
                        sendFinishSetMessage(cs);
                        return;
                    }
                }
                else {
                    try {
                        int id = Integer.parseInt(args[1]);
                        if(raceGame.isIdValid(id)) {
                            raceGame.insertCheckpointLocation(loc, id);
                            sendCheckpointAddedMessage(cs);
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
            }
            sendInvalidArgumentMessage(cs);
        }
    }

    private void sendAlreadyAnnouncedErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "This game was already announced.");
    }

    private void sendStartSetMessage(CommandSender cs) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void sendFinishSetMessage(CommandSender cs) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void sendCheckpointAddedMessage(CommandSender cs) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void sendIdNotValidMessage(CommandSender cs) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void sendNotANumberMessage(CommandSender cs) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void sendInvalidArgumentMessage(CommandSender cs) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
