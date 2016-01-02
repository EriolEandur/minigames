/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.utils.BukkitUtil;
import com.mcmiddleearth.minigames.utils.MessageUtil;
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
        setShortDescription(": ");
        setUsageDescription(": ");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game)) {
            OfflinePlayer[] players = Bukkit.getServer().getOfflinePlayers();
            OfflinePlayer player = null;
            for(OfflinePlayer search : players) {
                if(search.getName().equalsIgnoreCase(args[0])) {
                    player = search;
                    break;
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
                    game.removePlayer(player);
                    if(player instanceof Player) {
                        game.removeSpectator((Player) player);
                    }
                    game.setBanned(player);
                    sendPlayerBannedMessage(cs, player, game);
                    Player bannedPlayer = BukkitUtil.getOnlinePlayer(player);
                    if(bannedPlayer!=null) {
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
        MessageUtil.sendAllInfoMessage(cs, game, player.getName() +" was banned from this game.");
    }

    private void sendNoPlayerFoundMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "No player with that name.");
    }

    private void sendAlreadyBannedMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Player is already banned.");
    }
    
 }
