/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameReady extends AbstractGameCommand{
    
    public GameReady(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Announces a mini game.");
        setUsageDescription(": ");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game)) {
            if(game.isAnnounced()) {
                sendAlreadyAnnouncedErrorMessage(cs);
            }
            else {
                game.announceGame();
            }
        }
    }

    private void sendAlreadyAnnouncedErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "This game was already announced.");
    }
    
}
