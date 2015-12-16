/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.utils.BukkitUtil;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameKick extends AbstractGameCommand{
    
    public GameKick(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": ");
        setUsageDescription(": ");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game)) {
            OfflinePlayer player = game.getPlayer(args[0]);
            if(player==null) {
                sendNoPlayerFoundMessage(cs);
            } else {
                game.removePlayer(player);
                sendPlayerRemovedMessage(cs, player, game);
                Player kickedPlayer = BukkitUtil.getOnlinePlayer(player);
                if(kickedPlayer!=null) {
                    if(args.length>1) {
                        sendKickedPlayerMessage(kickedPlayer, cs, " for "+args[1]);
                    }
                    else {
                        sendKickedPlayerMessage(kickedPlayer, cs, "");
                    }
                }
            }
        }
    }

    private void sendNoPlayerFoundMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "No player with that name in the game.");
    }

    private void sendPlayerRemovedMessage(CommandSender cs, OfflinePlayer player, AbstractGame game) {
        MessageUtil.sendInfoMessage(cs, "You kicked "+player.getName()+" from game.");
        MessageUtil.sendAllInfoMessage(cs, game, player.getName() +" was removed from this game.");
    }

    private void sendKickedPlayerMessage(Player player, CommandSender kicker, String arg) {
        MessageUtil.sendInfoMessage(player, "You were kicked from the game by " 
                                           + kicker.getName()+arg+".");
    }
    
 }
