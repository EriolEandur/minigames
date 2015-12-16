/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import com.mcmiddleearth.minigames.utils.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GCCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args) {
        if(!label.equalsIgnoreCase("gc")) {
            return false;
        }
        if(args == null || args.length == 0) {
            sendNoMessageErrorMessage(cs);
            return true;
        }
        if(args[0].equals("!off")) {
            if(!(cs instanceof Player)) {
                sendNoPlayerMessage(cs);
            }
            else {
                PluginData.setGameChat((Player) cs, false);
                sendGameChatOffMessage(cs);
            }
            return true;
        }
        if(args[0].equals("!on")) {
            if(!(cs instanceof Player)) {
                sendNoPlayerMessage(cs);
            }
            else {
                PluginData.setGameChat((Player) cs,true);
                sendGameChatOnMessage(cs);
            }
            return true;
        }
        if(cs instanceof Player) {
            PluginData.setGameChat((Player) cs,true);
        }
        for(Player search : Bukkit.getServer().getOnlinePlayers()) {
            if(PluginData.getGameChat(search)) {
                Player player = search;
                if(player!=null) {
                    MessageUtil.sendChatMessage(cs, player, StringUtil.concat(args));
                }
            }
        }
        return true;
    }

    private void sendNoMessageErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You did not specify a message.");
    }

    private void sendNoPlayerMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You need to be a player to issue this command.");
    }

    private void sendGameChatOffMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs,"You switched game chat messages OFF.");
    }

    private void sendGameChatOnMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs,"You switched game chat messages ON.");
    }
    /**
     * private game chat
     * 
        AbstractGame game = null;
        if(cs instanceof Player) {
            game = PluginData.getGame((Player) cs);
            String message = StringUtil.concat(args);
        }
        else {
            game = PluginData.getGame(args[0]);
            String message = StringUtil.concat(Arrays.copyOfRange(args, 1, args.length));
        }
        if(game == null) {
            send
            if(game.getManager().getPlayer()!=null) {
                MessageUtil.sendChatMessage(cs, game.getManager().getPlayer(), StringUtil.concat(args));
            }
            for(OfflinePlayer offPlayer : game.getPlayers()) {
                if(offPlayer.getPlayer()!=null) {
                    MessageUtil.sendChatMessage(cs, offPlayer.getPlayer(), StringUtil.concat(args));
                }
            }
        }
        return true;
    }     */

}
