/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.utils.MinigamesMessageUtil;
import com.mcmiddleearth.pluginutils.message.MessageUtil;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameBan extends AbstractGameCommand{
    
    public GameBan(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Bans a player from a game.");
        setUsageDescription(" <player>: Bans the player from the current game. He will not be able to rejoin or spectate at the game.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game)) {
            OfflinePlayer player = Bukkit.getPlayer(args[0]);
            if(player==null) {
                OfflinePlayer[] players = Bukkit.getServer().getOfflinePlayers();
                for(OfflinePlayer search : players) {
                    if(search.getName().equals(args[0])) {
                        player = search;
                        break;
                    }
                }
            }
            if(player==null) {
                sendNoPlayerFoundMessage(cs);
            }
            else {
                if(game.isBanned(player)) {
                    sendAlreadyBannedMessage(cs);
                }
                else {
Logger.getGlobal().info("banned: "+player.getUniqueId());                    
                    game.removePlayer(player);
                    game.setBanned(player);
                    sendPlayerBannedMessage(cs, player, game);
                    Player bannedPlayer = Bukkit.getPlayer(player.getUniqueId());
                    if(bannedPlayer!=null) {
Logger.getGlobal().info("online");                        
                        game.removeSpectator(bannedPlayer);
                        if(args.length>1) {
                            sendBannedPlayerMessage(bannedPlayer, cs, " for "+args[1]);
                        }
                        else {
                            sendBannedPlayerMessage(bannedPlayer, cs, "");
                        }
                    }
                }
            }
        }
    }

    private void sendBannedPlayerMessage(Player bannedPlayer, CommandSender player, String string) {
        MessageUtil.sendInfoMessage(bannedPlayer, "You were banned by "+player.getName()+" from the minigame" + string+".");
    }

    private void sendPlayerBannedMessage(CommandSender cs, OfflinePlayer player, AbstractGame game) {
        MessageUtil.sendInfoMessage(cs, "You banned "+player.getName()+" from your game.");
        MinigamesMessageUtil.sendAllInfoMessage(cs, game, player.getName() +" was banned from this game.");
    }

    private void sendNoPlayerFoundMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "No player with that name.");
    }

    private void sendAlreadyBannedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Player is already banned.");
    }
    
 }
