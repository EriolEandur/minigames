/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.conversation.confirmation.Confirmationable;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.raceCheckpoint.Checkpoint;
import java.io.File;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameDelete extends AbstractCommand implements Confirmationable{
    
    private File file;
    
    public GameDelete(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Deletes saved minigame files.");
        setUsageDescription(" quiz|race|marker|golf|pvp <filename>: Deletes a quiz, race, marker, golf or pvp data file with name <filename>.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        if (args[0].equalsIgnoreCase("quiz")) {
            file = new File(PluginData.getQuestionDir(), args[1] + ".json");
        } else if(args[0].equalsIgnoreCase("race")) {
            file = new File(PluginData.getRaceDir(), args[1] + ".json");
        } else if(args[0].equalsIgnoreCase("marker")) {
            file = new File(Checkpoint.getMarkerDir(), args[1] + "."+ Checkpoint.getMarkerExt());
        } else if(args[0].equalsIgnoreCase("golf")) {
            file = new File(PluginData.getGolfDir(), args[1] + ".json");
        } else if(args[0].equalsIgnoreCase("pvp")) {
            file = new File(PluginData.getPvpDirectory(), args[1] + ".json");
        } else {
            sendInvalidDataTypeMessage(cs);
            return;
        }

        if(file.exists()) {
            PluginData.getConfirmationFactory().start((Player) cs, 
                    "Are you sure to delete "
                            +PluginData.getMessageUtil().HIGHLIGHT_STRESSED+file.getName()
                            +PluginData.getMessageUtil().HIGHLIGHT+"? There is no undo.", this);
        } else {
            sendFileNotFoundMessage(cs);
        }
    }

    @Override
    public void confirmed(Player player) {
        if(file.delete()) {
            PluginData.getMessageUtil().sendInfoMessage(player, "File deleted.");
        }
        else {
            PluginData.getMessageUtil().sendErrorMessage(player, "There was an error deleting the file.");
        }
    }

    @Override
    public void cancelled(Player player) {
        PluginData.getMessageUtil().sendInfoMessage(player, "You cancelled deletion.");
    }

    private void sendInvalidDataTypeMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Invalid data type. Try /game delete quiz|race|marker");
    }
    
    private void sendFileNotFoundMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "File not found.");
    }
    
}
