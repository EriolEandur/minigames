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
public class RaceGameStart extends AbstractGameCommand{
    
    public RaceGameStart(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": ");
        setUsageDescription(": ");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game)
                        && isCorrectGameType((Player) cs, game, GameType.RACE)) {
            if(!game.isAnnounced()) {
                sendNotAnnouncedErrorMessage(cs);
                return;
            }
            RaceGame raceGame = (RaceGame) game;
            if(raceGame.isStarted()) {
                sendAlreadySteadyMessage(cs);
            }
            if(raceGame.getPlayers().size()<2) {
                sendNotEnoughPlayerMessage(cs);
                return;
            }
            raceGame.steady();
        }
    }

    private void sendNotEnoughPlayerMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You need at least two players for a race.");
    }
    private void sendAlreadySteadyMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You already started the race.");
    }
}
