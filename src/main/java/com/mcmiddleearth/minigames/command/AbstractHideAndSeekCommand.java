/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.HideAndSeekGame;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public abstract class AbstractHideAndSeekCommand extends AbstractGameCommand {

    public AbstractHideAndSeekCommand(int minArgs, boolean playerOnly, String... permissionNodes) {
        super(minArgs, playerOnly, permissionNodes);
    }
    
    protected boolean isHideAndSeekGame(Player player, AbstractGame game) {
        if(game instanceof HideAndSeekGame) {
            return true;
        }
        else {
            sendNotHideAndSeekErrorMessage(player);
            return false;
        }
    }
    
    private void sendNotHideAndSeekErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "This is not a HideAndSeek game.");
    }

    
    
}
