/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameStart extends AbstractGameCommand{
    
    public GameStart(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Starts a race or golf course.");
        setUsageDescription(": race|golf: Cages the players at the start line or teleports players to start tee.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game)) {
            if(!game.isAnnounced()) {
                sendNotAnnouncedErrorMessage(cs);
                return;
            }

            if(game.getType()==null) {
                sendInvalidGameTypeErrorMessage(cs);
                return;
            }

            switch(game.getType()) {
                case RACE:
                    RaceGame raceGame = (RaceGame) game;
                    if(!raceGame.hasStart() || !raceGame.hasFinish()) {
                        sendNoRaceStartFinishMessage(cs);
                        return;
                    }
                    if(raceGame.isStarted()) {
                        sendAlreadySteadyMessage(cs);
                        return;
                    }
                    if(raceGame.getPlayers().size()<2) {
                        sendRaceNotEnoughPlayerMessage(cs);
                        return;
                    }
                    raceGame.steady();
                    break;
                case GOLF:
                    GolfGame golfGame = (GolfGame) game;
                    if(!golfGame.hasTeeStart() || !golfGame.hasTeeEnd() || !golfGame.hasHoleStart() || !golfGame.hasHoleEnd()) {
                        sendNoGolfStartFinishMessage(cs);
                        return;
                    }
                    if(golfGame.isStarted()) {
                        sendAlreadyReadyMessage(cs);
                        return;
                    }
                    if(golfGame.getPlayers().size()<2) {
                        sendGolfNotEnoughPlayerMessage(cs);
                        return;
                    }
                    golfGame.ready();
                    break;
                default:
                    sendInvalidGameTypeErrorMessage(cs);
            }
        }
    }

    private void sendRaceNotEnoughPlayerMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You need at least two players for a race.");
    }

    private void sendGolfNotEnoughPlayerMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You need at least two players for a golf course.");
    }

    private void sendAlreadySteadyMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You already started the race.");
    }

    private void sendAlreadyReadyMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You already started the course.");
    }

    private void sendNoRaceStartFinishMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "A race needs a start and finish.");
    }

    private void sendNoGolfStartFinishMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "A golf course needs a start tee or hole/end tee or hole.");
    }

    private void sendGameExistsMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "A game with that name already exists.");
    }

    private void sendInvalidGameTypeErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You specified an invalid game type.");
    }
}
