/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.utils.PlayerUtil;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import org.bukkit.Bukkit;
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
        setShortDescription(": Kicks a player from a game.");
        setUsageDescription(" <player>: Removes <player> from a game, he may join again.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game)) {
            OfflinePlayer player = game.getPlayer(args[0]);
            if(player==null) {
                Player spectator = Bukkit.getPlayer(args[0]);
                if(!game.isSpectating(spectator)) {
                    sendNoPlayerFoundMessage(cs);
                }
                else {
                    game.removeSpectator(spectator);
                    sendSpectatorRemovedMessage(cs, spectator, game);
                    if(args.length>1) {
                        sendKickedPlayerMessage(spectator, cs, " for "+args[1]);
                    }
                    else {
                        sendKickedPlayerMessage(spectator, cs, "");
                    }
                }
            } else {
                game.removePlayer(player);
                sendPlayerRemovedMessage(cs, player, game);
                Player kickedPlayer = PlayerUtil.getOnlinePlayer(player);
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

    private void sendSpectatorRemovedMessage(CommandSender cs, OfflinePlayer player, AbstractGame game) {
        MessageUtil.sendInfoMessage(cs, "You kicked spectator "+player.getName()+" from game.");
        MessageUtil.sendAllInfoMessage(cs, game, "Spectator " + player.getName() +" was removed from this game.");
    }
    
 }
