/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.conversation.Confirmationable;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.utils.MessageUtil;
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
        super(1, true, permissionNodes);
        setShortDescription(": Deletes saved mini games.");
        setUsageDescription(": ");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
            file = new File(PluginData.getQuestionDir(), args[0] + ".json");
            if(file.exists()) {
                PluginData.getConfirmationFactory().start((Player) cs, 
                        "Are you sure to delete this quiz data file? There is no undo.", this);
            }
    }

    @Override
    public void confirmed(Player player) {
        if(file.delete()) {
            MessageUtil.sendInfoMessage(player, "File deleted.");
        }
        else {
            MessageUtil.sendErrorMessage(player, "There was an error deleting the file.");
        }
    }

    @Override
    public void cancelled(Player player) {
        MessageUtil.sendInfoMessage(player, "You cancelled deleting.");
    }
    
}
