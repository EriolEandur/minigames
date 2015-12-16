/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Eriol_Eandur
 */
public class GameCheck extends AbstractCommand{
    
    public GameCheck(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Lists all currently active mini game.");
        setUsageDescription(": ");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        if(PluginData.getGames().isEmpty()) {
            MessageUtil.sendInfoMessage(cs, "There are no minigames running. Check again later.");
        } else {
            MessageUtil.sendInfoMessage(cs, "Running minigames:");
            for(AbstractGame game : PluginData.getGames()) {
                MessageUtil.sendNoPrefixInfoMessage(cs, game.getName() + ": "
                                                      + game.getType() + " with " 
                                                      + game.getManager().getName()+" and "
                                                      + game.getPlayers().size() + " players.");
            }
        }
    }
    
}
