/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.conversation.confirmation.Confirmationable;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.raceCheckpoint.Checkpoint;
import com.mcmiddleearth.minigames.utils.MinigamesMessageUtil;
import com.mcmiddleearth.pluginutils.message.MessageUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class RaceGameSaveMarker extends AbstractCommand implements Confirmationable{
    
    private String filename;
    
    public RaceGameSaveMarker(String... permissionNodes) {
        super(1, true, permissionNodes);
        cmdGroup = CmdGroup.RACE;
        setShortDescription(": Creates and saves a race marker to file.");
        setUsageDescription(" <filename>: Creates and saves a race marker to file <filename>. All non-Air blocks within 10 blocks radius of the player who issues the command are saved to the marker. Use Netherrack for check locations. A racing player needs to move to a check location to be registered at the location. Signs will be labeled for races.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        try {
            Checkpoint.saveMarkerToFile(((Player) cs).getLocation(), args[0], false);
            sendMarkerSavedMessage(cs);
        } catch (FileNotFoundException ex) {
            filename = args[0];
            PluginData.getConfirmationFactory().start((Player) cs, 
                        "A marker file with that name already exists. Overwrite it?", this);
        } catch (IOException ex) {
            MessageUtil.sendErrorMessage(cs, "Can't save marker. Check if file already exists.");
        }
    }

    @Override
    public void confirmed(Player player) {
        try {
            Checkpoint.saveMarkerToFile(player.getLocation(), filename, true);
            sendMarkerSavedMessage(player);
        } catch (IOException ex) {
            Logger.getLogger(RaceGameSaveMarker.class.getName()).log(Level.SEVERE, "markerSaveConfirmed", ex);
        }
    }

    @Override
    public void cancelled(Player player) {
        sendAbordMessage(player);
    }
    
    private void sendAbordMessage(Player player) {
        MessageUtil.sendInfoMessage(player, "Saving marker cancelled.");
    }
    
    private void sendMarkerSavedMessage(CommandSender cs){
        MessageUtil.sendInfoMessage(cs, "Marker saved");
    }

}
