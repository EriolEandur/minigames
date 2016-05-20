/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.pluginutils.PlayerUtil;
import com.mcmiddleearth.minigames.utils.GameChatUtil;
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
        PluginData.getMessageUtil().sendErrorMessage(cs, "No player with that name in the game.");
    }

    private void sendPlayerRemovedMessage(CommandSender cs, OfflinePlayer player, AbstractGame game) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You kicked "+player.getName()+" from game.");
        GameChatUtil.sendAllInfoMessage(cs, game, player.getName() +" was removed from this game.");
    }

    private void sendKickedPlayerMessage(Player player, CommandSender kicker, String arg) {
        PluginData.getMessageUtil().sendInfoMessage(player, "You were kicked from the game by " 
                                           + kicker.getName()+arg+".");
    }

    private void sendSpectatorRemovedMessage(CommandSender cs, OfflinePlayer player, AbstractGame game) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You kicked spectator "+player.getName()+" from game.");
        GameChatUtil.sendAllInfoMessage(cs, game, "Spectator " + player.getName() +" was removed from this game.");
    }
    
 }
