/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.utils.PlayerUtil;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameUnban extends AbstractGameCommand{
    
    public GameUnban(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Removes the ban of a player from a game.");
        setUsageDescription(" <player>: <player> is no longer banned from the game and may join/spectate again.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game)) {
            OfflinePlayer player = game.getBannedPlayer(args[0]);
            if(player==null) {
                sendNoBannedPlayerFoundMessage(cs);
            }
            else {
                game.setUnbanned(player);
                sendPlayerUnbannedMessage(cs, player);
                Player unbannedPlayer = PlayerUtil.getOnlinePlayer(player);
                if(unbannedPlayer!=null) {
                    sendUnbannedPlayerMessage(unbannedPlayer, game.getName());
                }
            }
        }
    }

    private void sendNoBannedPlayerFoundMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "No banned player with that name.");
    }

    private void sendPlayerUnbannedMessage(CommandSender cs, OfflinePlayer player) {
        MessageUtil.sendInfoMessage(cs, "You unbaned "+player.getName()+".");
    }

    private void sendUnbannedPlayerMessage(Player player, String name) {
        MessageUtil.sendInfoMessage(player, "You are no longer banned from game "+name+".");
    }
    
 }
