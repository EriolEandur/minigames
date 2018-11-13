/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GolfGame;
import com.mcmiddleearth.minigames.game.PvPGame;
import com.mcmiddleearth.minigames.game.RaceGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameReady extends AbstractGameCommand{
    
    public GameReady(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Announces a game.");
        setUsageDescription(": Announces a game which is sending a message to all online players.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game)) {
            if(game.isAnnounced()) {
                sendAlreadyAnnouncedErrorMessage(cs);
            } else {
                if(game instanceof RaceGame) {
                    RaceGame raceGame = (RaceGame) game; 
                    if(!raceGame.hasStart() || !raceGame.hasFinish()) {
                        sendRaceNoStartFinishMessage(cs);
                        return;
                    }
                }

                if(game instanceof GolfGame) {
                    GolfGame golfGame = (GolfGame) game;
                    if(!golfGame.hasTeeStart() || !golfGame.hasTeeEnd() || !golfGame.hasHoleStart() || !golfGame.hasHoleEnd()) {
                        sendNoGolfStartFinishMessage(cs);
                        return;
                    }

                    if(!golfGame.hasEnoughTees()) {
                        sendNotEnoughTeesMessage(cs);
                        return;
                    }

                    if(!golfGame.hasEnoughHoles()) {
                        sendNotEnoughHolesMessage(cs);
                        return;
                    }
                }

                if(game instanceof PvPGame) {
                    PvPGame pvpGame = (PvPGame) game;

                    if (!pvpGame.hasRedSpawn() || !pvpGame.hasBlueSpawn()) {
                        sendNoRedBlueSpawnMessage(cs);
                        return;
                    }

                    if (pvpGame.isCuboidArena()) {
                        if (!pvpGame.hasArenaMax() || !pvpGame.hasArenaMin()) {
                            sendNoArenaRegionMessage(cs);
                            return;
                        }
                    } else if (!pvpGame.hasArenaCenter()) {
                        sendNoArenaRegionMessage(cs);
                        return;
                    }
                }

                game.announceGame();
            }
        }
    }

    private void sendRaceNoStartFinishMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You need to set a start and finish before announcing a race game. Also please remember that you can't add checkpoints after announcing a race.");
    }

    private void sendNoGolfStartFinishMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "A golf course needs a start tee or hole/end tee or hole.");
    }

    private void sendNotEnoughTeesMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "A golf course needs a tee amount of 9, 18, 27, 14 or 20.");
    }

    private void sendNotEnoughHolesMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "A golf course needs a hole amount of 9, 18, 27, 14 or 20.");
    }

    private void sendNoRedBlueSpawnMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "A pvp match needs a spawn for the red and blue team.");
    }

    private void sendNoArenaRegionMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "A pvp match needs arena region.");
    }
}
