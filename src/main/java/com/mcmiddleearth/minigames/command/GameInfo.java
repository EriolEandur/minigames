/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.pluginutils.message.MessageUtil;
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
                messageHead=MessageUtil.STRESSED+game.getName()+MessageUtil.INFO+" is a ";
            }
        }
        if(game == null) {
            sendGameNotFoundMessage(cs);
        } else {
            MessageUtil.sendInfoMessage(cs, messageHead 
                                            +MessageUtil.STRESSED+ game.getType() 
                                            +MessageUtil.INFO + " game.");
            MessageUtil.sendNoPrefixInfoMessage(cs, "Game manager is " 
                                                    +MessageUtil.STRESSED+ game.getManager().getName()
                                                    +MessageUtil.INFO+"." ); 
            if(game.getPlayers().size()<=0) {
                MessageUtil.sendNoPrefixInfoMessage(cs, "No players within the game.");
            }
            else {
                MessageUtil.sendNoPrefixInfoMessage(cs, "Players in the game are:");
                String playerNames="";
                for(UUID player : game.getPlayers()) {
                    playerNames = playerNames.concat(MessageUtil.INFO+", "
                                                     +MessageUtil.STRESSED
                                                     +Bukkit.getOfflinePlayer(player).getName());
                }
                MessageUtil.sendNoPrefixInfoMessage(cs, playerNames.substring(4));
            }
        }
    }

    private void sendGameNotFoundMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "No game found by that name.");
    }
    
 }
