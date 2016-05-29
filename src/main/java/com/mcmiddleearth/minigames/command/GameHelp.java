/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.pluginutil.NumericUtil;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameHelp extends AbstractCommand{
    
    public GameHelp(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Displays help.");
        setUsageDescription(" quiz|race|hide|general|[subcommand]: Without argument displays short descriptions about all subcommands. With argument quiz, race, hide or general displays only commands belonging to that group. Argument [subcommand] shows detailed help about that subcommand.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        Map <String, AbstractCommand> commands = ((GameCommandExecutor)Bukkit.getPluginCommand("game").getExecutor())
                                                           .getCommands();
        CmdGroup wantedCmdGroup=CmdGroup.ALL;
        if(args.length>0) {
            wantedCmdGroup = CmdGroup.getCmdGroup(args[0]);
        }
        if(args.length<1 || NumericUtil.isInt(args[0])
                         || !wantedCmdGroup.equals(CmdGroup.ALL)){
            int page = 1;
            int pageIndex = (wantedCmdGroup.equals(CmdGroup.ALL)?0:1);
            if(args.length>pageIndex && NumericUtil.isInt(args[pageIndex])) {
                page = NumericUtil.getInt(args[pageIndex]);
            }
            FancyMessage header = new FancyMessage(MessageType.INFO,PluginData.getMessageUtil())
                                            .addSimple("Help for "
                                                        +PluginData.getMessageUtil().STRESSED+wantedCmdGroup.name()
                                                        +PluginData.getMessageUtil().INFO+" commands.");
            Set<String> keys = commands.keySet();
            List<FancyMessage> list = new ArrayList<>();
            for(String key : keys) {
                if(wantedCmdGroup.isCommandInGroup(commands.get(key))) {
                    String shortDescription = commands.get(key).getShortDescription();
                    String usageDescription = commands.get(key).getUsageDescription();
                    if(shortDescription!=null){
                    }
                    else {
                        shortDescription = ": Sorry, there is no help about this command.";
                    }
                    if(usageDescription==null){
                        usageDescription = ": Sorry, no help here.";
                    }
                    int separator = shortDescription.indexOf(":");
                    if(separator < 0) {
                        separator = shortDescription.length();
                    }
                    FancyMessage message = new FancyMessage(MessageType.WHITE,PluginData.getMessageUtil())
                            .addFancy(ChatColor.DARK_AQUA+"/game "+key+ChatColor.WHITE
                                        +shortDescription.substring(0,separator), 
                                          "/game "+key, hoverFormat("/game "+key+usageDescription));
                    if(separator<shortDescription.length()) {
                        message.addSimple(shortDescription.substring(separator));
                    }
                    list.add(message);
                }
            }
            if(wantedCmdGroup.equals(CmdGroup.ALL) || wantedCmdGroup.equals(CmdGroup.GENERAL)) {
                list.add(new FancyMessage(MessageType.WHITE,PluginData.getMessageUtil())
                        .addFancy(ChatColor.DARK_AQUA+"/gc", 
                                  "/gc ",
                                  ChatColor.GOLD+"/gc <message> | !on | !off: \n"
                                    +ChatColor.YELLOW+"Send a <message> in game chat,\n"
                                    +ChatColor.YELLOW+"switch !on receiving game chat messages (default) or\n"
                                    +ChatColor.YELLOW+"switch !off receiving game chat messages.")
                        .addSimple(ChatColor.WHITE+": Use game chat messages."));
            }
            PluginData.getMessageUtil().sendFancyListMessage((Player) cs, header, list, "/game help "+wantedCmdGroup.getName(), page);
        }
        else {
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
                    sendHelpStartMessage(cs);
                    int separator = description.indexOf(":");
                    new FancyMessage(MessageType.WHITE,PluginData.getMessageUtil())
                                .addClickable(ChatColor.DARK_AQUA+"/game "+args[0]
                                                        +(separator>0?description.substring(0, separator):"")
                                                        +ChatColor.WHITE+description.substring(separator), 
                                                     "/game "+args[0])
                                .send((Player)cs);
                }
                else {
                    sendNoDescriptionMessage(cs, args[0]);
                }
            }
        }
        sendManualMessage(cs);
    }

    private String hoverFormat(String hoverMessage) {
        class MyScanner {
            private final Scanner scanner;
            public String currentToken=null;
            public MyScanner(String string) {
                scanner = new Scanner(string);
                scanner.useDelimiter(" ");
                if(scanner.hasNext()) {
                    currentToken = scanner.next();
                }
            }
            public String next() {
                if(scanner.hasNext()) {
                    currentToken = scanner.next();
                } else {
                    currentToken = null;
                }
                return currentToken;
            }
            public boolean hasCurrent() {
                return currentToken != null;
            }
            public boolean hasNext() {
                return scanner.hasNext();
            }
        }
        int LENGTH_OF_LINE = 40;
        String result = ChatColor.GOLD+"";
        int separator = hoverMessage.indexOf(":");
        result = result.concat(hoverMessage.substring(0,separator+1)+"\n");
        MyScanner scanner = new MyScanner(hoverMessage.substring(separator+1));
        while (scanner.hasCurrent()) {
            String line = ChatColor.YELLOW+scanner.currentToken+" ";
            scanner.next();
            while(scanner.hasCurrent() && line.length()+scanner.currentToken.length()<LENGTH_OF_LINE) {
                line = line.concat(scanner.currentToken+" ");
                scanner.next();
            }
            if(scanner.hasCurrent()) {
                line = line.concat("\n");
            }
            result = result.concat(line);
        }
        return result;
    }
    
    private void sendHelpStartMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Help for minigames command:");
    }

    private void sendNoSuchCommandMessage(CommandSender cs, String arg) {
        PluginData.getMessageUtil().sendIndentedInfoMessage(cs, "/game "+arg+ChatColor.RED+": There is no such command.");    
    }

    private void sendDescriptionMessage(CommandSender cs, String arg, String description) {
        PluginData.getMessageUtil().sendIndentedInfoMessage(cs, "/game "+arg+ChatColor.WHITE+description);
    }

    private void sendNoDescriptionMessage(CommandSender cs, String arg) {
        PluginData.getMessageUtil().sendIndentedInfoMessage(cs, "/game "+arg+ChatColor.RED+": There is no help for this command.");
    }

   private void sendManualMessage(CommandSender cs) {
        cs.sendMessage(ChatColor.YELLOW+"http://www.mcmiddleearth.com/resources/minigames-manual.94");
    }

    private void sendChatDescriptionMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendIndentedInfoMessage(cs, "/gc <message>: Send a game chat message.");
        PluginData.getMessageUtil().sendIndentedInfoMessage(cs, "/gc !on: Switch on receiving game chat messages.");
        PluginData.getMessageUtil().sendIndentedInfoMessage(cs, "/gc !off: Switch off receiving game chat messages.");
    }
    
 }
