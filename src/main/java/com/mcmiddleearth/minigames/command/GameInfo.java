/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import org.bukkit.OfflinePlayer;
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
        AbstractGame game = getGame((Player) cs);
        if(game != null) {
            MessageUtil.sendInfoMessage(cs, "This is a " + game.getType() 
                                          + " game, managed by " + game.getManager().getName()); 
            if(game.getPlayers().size()<=0) {
                MessageUtil.sendNoPrefixInfoMessage(cs, "No players within the game.");
            }
            else {
                MessageUtil.sendNoPrefixInfoMessage(cs, "Players within the game are:");
                for(OfflinePlayer player : game.getPlayers()) {
                    MessageUtil.sendNoPrefixInfoMessage(cs, player.getName());
                }
            }
        }
    }
    
 }
