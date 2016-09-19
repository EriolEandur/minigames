/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
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
            }
            else {
                if(game instanceof RaceGame) {
                    RaceGame raceGame = (RaceGame) game; 
                    if(!raceGame.hasStart() || !raceGame.hasFinish()) {
                        sendNoStartFinishMessage(cs);
                        return;
                    }
                }
                game.announceGame();
            }
        }
    }

    private void sendNoStartFinishMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You need to set a start and finish before announcing a race game. Also please remember that you can't add checkpoints after announcing a race.");
    }

}
