/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.Permissions;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.utils.GameChatUtil;
import com.mcmiddleearth.pluginutils.StringUtil;
import com.mcmiddleearth.pluginutils.message.MessageUtil;
import org.bukkit.Bukkit;
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
        if(!cs.hasPermission(Permissions.USER)) {
            sendNoPermsErrorMessage(cs);
            return true;
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
                    GameChatUtil.sendChatMessage(cs, player, StringUtil.concat(args));
                }
            }
        }
        return true;
    }

    private void sendNoPermsErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You don't have permission to run this command.");
    }
    
    private void sendNoMessageErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You can send a game chat message with /gc <message>. "
                                         +"You can switch on/off game chat with /gc !on|!off");
    }

    private void sendNoPlayerMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You need to be a player to issue this command.");
    }

    private void sendGameChatOffMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs,"You switched game chat messages OFF.");
    }

    private void sendGameChatOnMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs,"You switched game chat messages ON.");
    }
}
