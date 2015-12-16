/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.utils.MessageUtil;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Eriol_Eandur
 */
public class GameHelp extends AbstractCommand{
    
    public GameHelp(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": ");
        setUsageDescription(": ");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        sendHelpStartMessage(cs);
        Map <String, AbstractCommand> commands = ((GameCommandExecutor)Bukkit.getPluginCommand("game").getExecutor())
                                                           .getCommands();
        if(args.length>0){
            AbstractCommand command = commands.get(args[0]);
            if(command==null) {
                sendNoSuchCommandMessage(cs, args[0]);
            }
            else {
                String description = command.getUsageDescription();
                if(description==null){
                    description = command.getShortDescription();
                }
                if(description!=null){
                    sendDescriptionMessage(cs, args[0], description);
                }
                else {
                    sendNoDescriptionMessage(cs, args[0]);
                }
            }
        }
        else {
            Set<String> keys = commands.keySet();
            for(String key : keys) {
                String description = commands.get(key).getShortDescription();
                if(description!=null){
                    sendDescriptionMessage(cs, key, description);
                }
                else {
                    sendNoDescriptionMessage(cs, key);
                }
            }
            sendChatDescriptionMessage(cs);
        }
        sendManualMessage(cs);
    }

    private void sendHelpStartMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Help for minigames plugin.");
    }

    private void sendNoSuchCommandMessage(CommandSender cs, String arg) {
        MessageUtil.sendNoPrefixInfoMessage(cs, "/game "+arg+": There is no such command.");    
    }

    private void sendDescriptionMessage(CommandSender cs, String arg, String description) {
        MessageUtil.sendNoPrefixInfoMessage(cs, "/game "+arg+description);
    }

    private void sendNoDescriptionMessage(CommandSender cs, String arg) {
        MessageUtil.sendNoPrefixInfoMessage(cs, "/game "+arg+": There is no help for this command.");
    }

   private void sendManualMessage(CommandSender cs) {
        //MessageUtil.sendNoPrefixInfoMessage(cs, "Manual for plotbuild plugin: ... .");
    }

    private void sendChatDescriptionMessage(CommandSender cs) {
        MessageUtil.sendNoPrefixInfoMessage(cs, "/g <message>: Send a game chat message.");
        MessageUtil.sendNoPrefixInfoMessage(cs, "/g !on: Switch on receiving game chat messages (default).");
        MessageUtil.sendNoPrefixInfoMessage(cs, "/g !off: Switch off receiving game chat messages.");
    }
    
 }
