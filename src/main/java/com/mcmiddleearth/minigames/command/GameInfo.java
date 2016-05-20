/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameInfo extends AbstractGameCommand{
    
    public GameInfo(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Displays informations about a game.");
        setUsageDescription(": Displays the manager and the number of players participating a game.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game;
        String messageHead="";
        if(args.length==0) {
            game = getGame((Player) cs);
            if(game == null) {
                return;
            }
            messageHead="You are in a ";
        } else {
            game = PluginData.getGame(args[0]);
            if(game!=null) {
                messageHead=PluginData.getMessageUtil().STRESSED+game.getName()+PluginData.getMessageUtil().INFO+" is a ";
            }
        }
        if(game == null) {
            sendGameNotFoundMessage(cs);
        } else {
            PluginData.getMessageUtil().sendInfoMessage(cs, messageHead 
                                            +PluginData.getMessageUtil().STRESSED+ game.getType() 
                                            +PluginData.getMessageUtil().INFO + " game.");
            PluginData.getMessageUtil().sendNoPrefixInfoMessage(cs, "Game manager is " 
                                                    +PluginData.getMessageUtil().STRESSED+ game.getManager().getName()
                                                    +PluginData.getMessageUtil().INFO+"." ); 
            if(game.getPlayers().size()<=0) {
                PluginData.getMessageUtil().sendNoPrefixInfoMessage(cs, "No players within the game.");
            }
            else {
                PluginData.getMessageUtil().sendNoPrefixInfoMessage(cs, "Players in the game are:");
                String playerNames="";
                for(UUID player : game.getPlayers()) {
                    playerNames = playerNames.concat(PluginData.getMessageUtil().INFO+", "
                                                     +PluginData.getMessageUtil().STRESSED
                                                     +Bukkit.getOfflinePlayer(player).getName());
                }
                PluginData.getMessageUtil().sendNoPrefixInfoMessage(cs, playerNames.substring(4));
            }
        }
    }

    private void sendGameNotFoundMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "No game found by that name.");
    }
    
 }
