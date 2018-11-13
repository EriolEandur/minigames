/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.*;
import com.mcmiddleearth.pluginutil.NumericUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameStart extends AbstractGameCommand{
    
    public GameStart(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Starts a race, golf course or pvp match.");
        setUsageDescription(": seconds: Cages the players at the start line, teleports players to start tee or starts a pvp game with the length of <seconds>.");
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

                    if (!raceGame.hasStart() || !raceGame.hasFinish()) {
                        sendNoRaceStartFinishMessage(cs);
                        return;
                    }

                    if (raceGame.isStarted()) {
                        sendAlreadySteadyMessage(cs);
                        return;
                    }

                    if (raceGame.getPlayers().size() < 2) {
                        sendRaceNotEnoughPlayerMessage(cs);
                        return;
                    }

                    raceGame.steady();
                    break;
                case GOLF:
                    GolfGame golfGame = (GolfGame) game;

                    if (!golfGame.hasTeeStart() || !golfGame.hasTeeEnd() || !golfGame.hasHoleStart() || !golfGame.hasHoleEnd()) {
                        sendNoGolfStartFinishMessage(cs);
                        return;
                    }

                    if (golfGame.isReady()) {
                        sendGolfAlreadyReadyMessage(cs);
                        return;
                    }

                    if (golfGame.getPlayers().size() < 2) {
                        sendGolfNotEnoughPlayerMessage(cs);
                        return;
                    }

                    golfGame.ready();
                    break;
                case PVP:
                    PvPGame pvpGame = (PvPGame) game;

                    if (args.length < 1) {
                        sendNoSecondsMessage(cs);
                        return;
                    } else {
                        if (!NumericUtil.isInt(args[0])) {
                            sendNoIntegerSecondsMessage(cs);
                            return;
                        }

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

                        if (pvpGame.isReady()) {
                            sendPvPAlreadyReadyMessage(cs);
                            return;
                        }

                        if (pvpGame.getPlayers().size() < 2) {
                            sendPvPNotEnoughPlayerMessage(cs);
                            return;
                        }

                        pvpGame.ready(Integer.parseInt(args[0]));
                    }

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

    private void sendPvPNotEnoughPlayerMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You need at least two players for a pvp match.");
    }

    private void sendAlreadySteadyMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You already started the race.");
    }

    private void sendGolfAlreadyReadyMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You already started the course.");
    }

    private void sendPvPAlreadyReadyMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You already started the pvp match.");
    }

    private void sendNoRaceStartFinishMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "A race needs a start and finish.");
    }

    private void sendNoGolfStartFinishMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "A golf course needs a start tee or hole/end tee or hole.");
    }

    private void sendNoRedBlueSpawnMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "A pvp match needs a spawn for the red and blue team.");
    }

    private void sendNoArenaRegionMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "A pvp match needs arena region.");
    }

    private void sendNoSecondsMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "A pvp match needs an indicated game length in seconds.");
    }

    private void sendNoIntegerSecondsMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "A pvp match length needs to be an integer (seconds).");
    }

    private void sendInvalidGameTypeErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You specified an invalid game type.");
    }
}
