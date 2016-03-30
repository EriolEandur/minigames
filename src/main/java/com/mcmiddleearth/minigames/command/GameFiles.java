/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.raceCheckpoint.Checkpoint;
import com.mcmiddleearth.minigames.utils.FileUtil;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Eriol_Eandur
 */
public class GameFiles extends AbstractCommand{
    
    public GameFiles(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Lists all saved game data files.");
        setUsageDescription(" quiz|race|marker: Lists all quiz or race or marker data files. For quiz and race data files a descripion of the saved data will be shown.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        String command = null;
        File[] files;
        if(args[0].equalsIgnoreCase("quiz")) {
            files = PluginData.getQuestionDir().listFiles(FileUtil.getFileExtFilter("json"));
            command = "/game loadquiz ";
        }
        else if(args[0].equalsIgnoreCase("race")) {
            files = PluginData.getRaceDir().listFiles(FileUtil.getFileExtFilter("json"));
            command = "/game loadrace ";
        }
        else if(args[0].equalsIgnoreCase("marker")) {
            files = Checkpoint.getMarkerDir().listFiles(FileUtil.getFileExtFilter(Checkpoint.getMarkerExt()));
        }
        else {
            sendInvalidDataTypeMessage(cs);
            return;
        }
        int page=1;
        int maxPage=(files.length-1)/10+1;
        if(maxPage<1) {
            maxPage = 1;
        }
        if(args.length>1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                page = 1;
            }
        }
        if(page>maxPage) {
            page = maxPage;
        }
        sendHeaderMessage(cs, args[0], page, maxPage);
        for(int i = files.length-1-(page-1)*10; i >= 0 && i > files.length-1-(page-1)*10-10; i--) {
            sendEntryMessage(cs, files[i].getName(), getDescription(files[i]), command);
        }
    }
    
    private String getDescription(File file) {
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
            } catch (FileNotFoundException | ParseException ex) {
                MiniGamesPlugin.getPluginInstance().getLogger().log(Level.SEVERE, null, ex);
                return "";
            }
        }
        else {
            return "";
        }
    }

    private void sendHeaderMessage(CommandSender cs, String type, int page, int maxPage) {
        MessageUtil.sendInfoMessage(cs, "Saved "+type+" data files [page " +page+"/"+maxPage+"]");
    }

    private void sendEntryMessage(CommandSender cs, String fileName, String description, String command) {
        String name = fileName.substring(0, fileName.lastIndexOf('.'));
        while(name.length()<15) {
            name = name.concat(" ");
        }
        if(command != null) {
            MessageUtil.sendClickableMessage((Player)cs, MessageUtil.INFO+MessageUtil.getNOPREFIX()
                                                +name+description, command+fileName.substring(0, fileName.lastIndexOf('.')));
        }
        else {
            MessageUtil.sendNoPrefixInfoMessage(cs, name+description);
        }
    }

    private void sendInvalidDataTypeMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Invalid file type. Try /game files quiz|race|marker");
    }
}
