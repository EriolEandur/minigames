/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.pluginutils.message.FancyMessage;
import com.mcmiddleearth.pluginutils.message.MessageType;
import com.mcmiddleearth.pluginutils.message.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameCheck extends AbstractCommand{
    
    public GameCheck(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Lists all current mini game.");
        setUsageDescription(": Lists all current mini game.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        if(!PluginData.gameRunning()) {
            MessageUtil.sendInfoMessage(cs, "There are no minigames running. Check again later.");
        } else {
            MessageUtil.sendInfoMessage(cs, "Running minigames (click to join):");
            for(AbstractGame game : PluginData.getGames()) {
                if(game.isAnnounced()) {
                    FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX)
                        .addClickable(ChatColor.DARK_AQUA+game.getName() +": "
                                      + MessageUtil.STRESSED+game.getType() 
                                      + MessageUtil.INFO+" with " 
                                      + MessageUtil.STRESSED+game.getManager().getName(), 
                                "/game join "+game.getName());
                    if(game.isPrivat()) {
                        message.addSimple(" (Private)");
                    } else {
                        message.addSimple(" ("+ game.getPlayers().size() 
                                              + (game.getPlayers().size()==1?" player)":" players)"));
                    }
                    message.send((Player)cs);
                }
            }
        }
    }
    
}
