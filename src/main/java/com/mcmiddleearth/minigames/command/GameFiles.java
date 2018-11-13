/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.raceCheckpoint.Checkpoint;
import com.mcmiddleearth.pluginutil.FileUtil;
import com.mcmiddleearth.pluginutil.NumericUtil;
import com.mcmiddleearth.pluginutil.message.FancyMessage;
import com.mcmiddleearth.pluginutil.message.MessageType;
import java.io.File;
import java.io.FileFilter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameFiles extends AbstractCommand{
    
    public GameFiles(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Lists all saved game data files.");
        setUsageDescription(" quiz|race|marker|golf|pvp|loadout: Lists all quiz, race, marker, golf course, pvp match or pvp loadout data files. For quiz, race, golf course, pvp match and pvp loadout data files a description of the saved data will be shown.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        String command = null;
        int page = 1;
        if(args.length>1 && NumericUtil.isInt(args[1])) {
            page = NumericUtil.getInt(args[1]);
        }
        File directory;
        FileFilter filter;
        FancyMessage header = new FancyMessage(MessageType.INFO,PluginData.getMessageUtil());
        if(args[0].equalsIgnoreCase("quiz")) {
            directory = PluginData.getQuestionDir();
            filter = FileUtil.getFileExtFilter("json");
            command = "/game loadquiz";
            header.addSimple("Saved "+PluginData.getMessageUtil().STRESSED+"quiz"+PluginData.getMessageUtil().INFO+" files.");
        }
        else if(args[0].equalsIgnoreCase("race")) {
            directory = PluginData.getRaceDir();
            filter = FileUtil.getFileExtFilter("json");
            command = "/game loadrace";
            header.addSimple("Saved "+PluginData.getMessageUtil().STRESSED+"race"+PluginData.getMessageUtil().INFO+" files.");
        }
        else if(args[0].equalsIgnoreCase("marker")) {
            directory = Checkpoint.getMarkerDir();
            filter =FileUtil.getFileExtFilter(Checkpoint.getMarkerExt());
            command = "/game marker";
            header.addSimple("Saved "+PluginData.getMessageUtil().STRESSED+"race marker"+PluginData.getMessageUtil().INFO+" files.");
        } else if(args[0].equalsIgnoreCase("golf")) {
            directory = PluginData.getGolfDir();
            filter = FileUtil.getFileExtFilter("json");
            command = "/game loadgolf";
            header.addSimple("Saved "+PluginData.getMessageUtil().STRESSED+"golf course"+PluginData.getMessageUtil().INFO+" files.");
        } else if(args[0].equalsIgnoreCase("pvp")) {
            directory = PluginData.getPvpDirectory();
            filter = FileUtil.getFileExtFilter("json");
            command = "/game loadpvp";
            header.addSimple("Saved "+PluginData.getMessageUtil().STRESSED+"pvp match"+PluginData.getMessageUtil().INFO+" files.");
        } else if(args[0].equalsIgnoreCase("loadout")) {
            directory = PluginData.getLoadoutDirectory();
            filter = FileUtil.getFileExtFilter("json");
            command = "/game loadout";
            header.addSimple("Saved "+PluginData.getMessageUtil().STRESSED+"pvp loadout"+PluginData.getMessageUtil().INFO+" files.");
        }
        else {
            sendInvalidDataTypeMessage(cs);
            return;
        }
        PluginData.getMessageUtil().sendFancyFileListMessage((Player) cs, header, directory, filter, 
                                             new String[]{page+""}, "/game files "+ args[0], command, true);
    }
    /*
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
            command = "game marker ";
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
            new FancyMessage(MessageType.INFO_INDENTED)
                    .addClickable(name+description, 
                                  command+fileName.substring(0, fileName.lastIndexOf('.')))
                    .send((Player)cs);
        }
        else {
            MessageUtil.sendIndentedInfoMessage(cs, name+description);
        }
    }*/

    private void sendInvalidDataTypeMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Invalid file type. Try /game files quiz|race|marker|golf|loadout");
    }
}
