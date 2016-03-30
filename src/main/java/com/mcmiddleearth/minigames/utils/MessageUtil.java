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

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Ivanpl, Eriol_Eandur
 */

public class MessageUtil {
    
    
    public static final ChatColor INFO = ChatColor.AQUA;
    public static final ChatColor ERROR = ChatColor.RED;
    public static final ChatColor STRESSED = ChatColor.BLUE;
    
    @Getter
    @Setter
    private static String PREFIX   = "[Plugin] ";
    
    @Getter
    private static final String NOPREFIX = "    ";
    
    private static final String CHATPREFIX = "[GameChat] ";

    private static final int PAGE_LENGTH = 13;
    
    public static void sendErrorMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage(ERROR + PREFIX + message);
        } else {
            sender.sendMessage(PREFIX + message);
        }
    }
    
    public static void sendInfoMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage(INFO + PREFIX + message);
        } else {
            sender.sendMessage(PREFIX + message);
        }
    }
    
    public static void sendNoPrefixInfoMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage(INFO + NOPREFIX + message);
        } else {
            sender.sendMessage(NOPREFIX + message);
        }
    }
    
    public static void sendBroadcastMessage(String string) {
        Bukkit.getServer().broadcastMessage(INFO + PREFIX + string);
    }

    public static void sendClickableMessage(Player sender, String message, String onClickCommand) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw "+ sender.getName()+" "
                +"{ text:\""+message+"\", "
                  +"clickEvent:{ action:run_command,"
                               + "value:\""+ onClickCommand +"\"}}");
    }
        
    public static void sendClickableMessage(Player sender, LinkedHashMap<String,String> data) {
        String rawText = "tellraw "+ sender.getName()+" [";
        boolean first = true;
        for(String message: data.keySet()) {
            if(first) {
                first = false; 
            }
            else {
                rawText = rawText.concat(",");
            }
            rawText = rawText.concat("{text:\""+message+"\"");
            String command = data.get(message);
            if(command!=null) {
                rawText = rawText.concat(",clickEvent:{ action:run_command,value:\"");
                rawText = rawText.concat(command+"\"}");
            }
            rawText = rawText.concat("}");
        }
        rawText = rawText.concat("]");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rawText);
    }
        
    public static void sendClickableFileListMessage(Player recipient, String header,
                                                    File baseDir, FileFilter filter, String[] args,
                                                    String listCommand, String selectCommand) {
        // args may be length 0 or include: [relative Dir] [#page]
        // list command must be like: /listCommand [relativeDirectory] [#page]
        // select command must be like: /selectCommand <filename>
        int page=1;
        String relativeDir = "";
        if(args.length>0) {
            try {
                page = Integer.parseInt(args[args.length-1]);
            } catch (NumberFormatException ex) {
                relativeDir = argsToDir(args[0]);
            }
        }
        if(args.length>1) {
            relativeDir = argsToDir(args[0]);
        }
        File dir = new File(baseDir+"/"+relativeDir);
        if(!dir.exists()) {
            sendErrorMessage(recipient, "Directory not found.");
            return;
        }
        if(!baseDir.exists()) {
            sendErrorMessage(recipient, "Base Directory not found.");
            return;
        }            
        File[] dirs = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
        File[] files = dir.listFiles(filter);
        List<LinkedHashMap<String,String>> list = new ArrayList<>();
        LinkedHashMap<String,String> entry;
        header = header + relativeDir;
        LinkedHashMap<String,String> headerMap = new LinkedHashMap<>();
        headerMap.put(header, null);
        if(!dir.equals(baseDir)) {
            entry = new LinkedHashMap<>();
            String parentDir = new File(relativeDir).getParent();
            if(parentDir == null) {
                parentDir = "";
            }
            entry.put(".. parent directory", listCommand+" "+parentDir);
            list.add(entry);
        }
        for(File subDir:dirs) {
            entry = new LinkedHashMap<>();
            entry.put("<"+subDir.getName()+">", listCommand+" "
                      +(relativeDir.length()>0?relativeDir+"/":"")+subDir.getName());
            list.add(entry);
        }
        for(File file:files) {
            entry = new LinkedHashMap<>();
            String filename = file.getName().substring(0,file.getName().lastIndexOf('.'));
            entry.put(filename,selectCommand+" "+(relativeDir.length()>0?relativeDir+"/":"")+filename);
            entry.put(getDescription(file),null);
            list.add(entry);
        }
        sendClickableListMessage(recipient, headerMap, list, listCommand+" "+relativeDir, page);
    }

    private static String argsToDir(String args) {
        String relativeDir;
        if(args.startsWith("/")|| args.startsWith("\\")) {
            relativeDir = args.substring(1);
        } else {
            relativeDir = args;
        }
        return relativeDir;
    }
    
    public static void sendClickableListMessage(Player recipient, LinkedHashMap<String,String> header,
                                                List<LinkedHashMap<String,String>> list, 
                                                String listCommand, int page) {
        // list command must be like: /listCommand [#page]
        int maxPage=Math.max((int) Math.ceil(list.size()/((float)PAGE_LENGTH)),1);
        if(page>maxPage) {
            page = maxPage;
        }
        header.put(" [page " +page+"/"+maxPage+"]", null);
        LinkedHashMap<String,String> pageUp = new LinkedHashMap<>();
        pageUp.put("---^ page up ^---", listCommand+" "+(page-1));
        LinkedHashMap<String,String> pageDown = new LinkedHashMap<>();
        pageDown.put("---v page down v--", listCommand+" "+(page+1));
        sendClickableMessage(recipient, header);
        if(page>1) {
            sendClickableMessage(recipient, pageUp);
        }
        for(int i = (page-1)*PAGE_LENGTH; i < list.size() && i < page*PAGE_LENGTH; i++) {
            sendClickableMessage(recipient, list.get(i));
        }
        if(page<maxPage) {
            sendClickableMessage(recipient, pageDown);
        }
    }

    private static String getDescription(File file) {
        if(file.getName().endsWith("json")) {
            try {
                String input;
                try (Scanner reader = new Scanner(file)) {
                    input = "";
                    while(reader.hasNext()){
                        input = input+reader.nextLine();
                    }
                }
                JSONObject jInput = (JSONObject) new JSONParser().parse(input);
                return (String) jInput.get("description");
            } catch (FileNotFoundException | ParseException ex) {}
        }
        return "";
    }
    
    public static ChatColor randomColor() {
        List<ChatColor> list = new ArrayList<>();
        for (ChatColor color : ChatColor.values()) {
            if (!(color.equals(ChatColor.BOLD)
                    || color.equals(ChatColor.COLOR_CHAR)
                    || color.equals(ChatColor.MAGIC)
                    || color.equals(ChatColor.RESET)
                    || color.equals(ChatColor.STRIKETHROUGH)
                    || color.equals(ChatColor.UNDERLINE))) {
                list.add(color);
            }
        }
        return list.get(NumericUtil.getRandom(0, list.size() - 1));
    }

    public static void sendAllInfoMessage(CommandSender sender, AbstractGame game, String message) {
        for(Player onlinePlayer : game.getOnlinePlayers()) {
            if(onlinePlayer!=null) {
                if(!((sender instanceof Player) && PlayerUtil.isSame((Player) sender,onlinePlayer))) {
                    onlinePlayer.sendMessage(ChatColor.AQUA + PREFIX + message);
                } 
            }
        }
        Player manager = PlayerUtil.getOnlinePlayer(game.getManager());
        if(manager!=null && !PluginData.isInGame(manager)) {
            if(!((sender instanceof Player) && PlayerUtil.isSame((Player) sender,manager))) {
                manager.sendMessage(ChatColor.AQUA + PREFIX + message);
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
