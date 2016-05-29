/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
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
            PluginData.getMessageUtil().sendInfoMessage(cs, "There are no minigames running. Check again later.");
        } else {
            PluginData.getMessageUtil().sendInfoMessage(cs, "Running minigames (click to join):");
            for(AbstractGame game : PluginData.getGames()) {
                if(game.isAnnounced()) {
                    FancyMessage message = new FancyMessage(MessageType.INFO_NO_PREFIX,PluginData.getMessageUtil())
                        .addClickable(ChatColor.DARK_AQUA+game.getName() +": "
                                      + PluginData.getMessageUtil().STRESSED+game.getType() 
                                      + PluginData.getMessageUtil().INFO+" with " 
                                      + PluginData.getMessageUtil().STRESSED+game.getManager().getName(), 
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
