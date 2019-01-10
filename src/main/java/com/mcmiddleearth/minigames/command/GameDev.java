/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.utils.DevUtil;
import java.io.File;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameDev extends AbstractCommand {
    
    private File file;
    
    public GameDev(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Development logs.");
        setUsageDescription(" [true|false] [#level] [-r]: enables and disabled logging to console and player");
    }
    
    @Override
    protected void execute(CommandSender sender, String... args) {
        if(args.length>0 && args[0].equalsIgnoreCase("true")) {
            DevUtil.setConsoleOutput(true);
            showDetails(sender);
            return ;
        }
        else if(args.length>0 && args[0].equalsIgnoreCase("false")) {
            DevUtil.setConsoleOutput(false);
            showDetails(sender);
            return ;
        }
        else if(args.length>0) {
            try {
                int level = Integer.parseInt(args[0]);
                DevUtil.setLevel(level);
                showDetails(sender);
                return ;
            }
            catch(NumberFormatException e){};
        }
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(args.length>1 && args[1].equalsIgnoreCase("-r")) {
                DevUtil.remove(player);
                showDetails(sender);
                return ;
            }
            DevUtil.add(player);
            showDetails(sender);
            return ;
        }
    }

    private void showDetails(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs,"DevUtil: Level - "+DevUtil.getLevel()+"; Console - "+DevUtil.isConsoleOutput()+"; ");
        PluginData.getMessageUtil().sendNoPrefixInfoMessage(cs,"Developer:");
        for(OfflinePlayer player:DevUtil.getDeveloper()) {
        PluginData.getMessageUtil().sendNoPrefixInfoMessage(cs, "            "+player.getName());
        }
    }
    
    
}
