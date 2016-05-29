/*
 * Copyright (C) 2016 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.minigames.utils;

import com.mcmiddleearth.pluginutil.PlayerUtil;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */

public class GameChatUtil {
    
    private static final String CHATPREFIX = "";//[GameChat] ";

    public static void sendAllInfoMessage(CommandSender sender, AbstractGame game, String message) {
        for(Player onlinePlayer : game.getOnlinePlayers()) {
            if(onlinePlayer!=null) {
                if(!((sender instanceof Player) && PlayerUtil.isSame((Player) sender,onlinePlayer))) {
                    onlinePlayer.sendMessage(PluginData.getMessageUtil().INFO + PluginData.getMessageUtil().getPREFIX() + message);
                } 
            }
        }
        Player manager = PlayerUtil.getOnlinePlayer(game.getManager());
        if(manager!=null && !PluginData.isInGame(manager)) {
            if(!((sender instanceof Player) && PlayerUtil.isSame((Player) sender,manager))) {
                manager.sendMessage(PluginData.getMessageUtil().INFO + PluginData.getMessageUtil().getPREFIX() + message);
            }
        }
    }
    
    public static void sendChatMessage(CommandSender sender, Player recipient, String message) {
        if (!(sender instanceof Player)) {
            recipient.sendMessage(ChatColor.AQUA + CHATPREFIX
                                        + ChatColor.RED+ "<Server> " 
                                        + ChatColor.WHITE + message);
        } else {
            AbstractGame game = PluginData.getGame((Player)sender);
            if(game==null) {
                if(PluginData.isSpectating((Player) sender)) {
                    recipient.sendMessage(ChatColor.AQUA + CHATPREFIX
                                        + ChatColor.YELLOW+"<Spectator " + sender.getName() + "> " 
                                        + ChatColor.WHITE + message);
                }
                else {
                    recipient.sendMessage(ChatColor.AQUA + CHATPREFIX
                                        + ChatColor.YELLOW+"<Player " + sender.getName() + "> " 
                                        + ChatColor.WHITE + message);
                }
            }
            else {
                recipient.sendMessage(ChatColor.AQUA + CHATPREFIX
                                        + game.getGameChatTag((Player) sender) + sender.getName() + "> " 
                                        + ChatColor.WHITE + message);
            }
        }
    }
    
}

