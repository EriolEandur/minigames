/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.conversation.Confirmationable;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.RaceGame;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class RaceGameSave extends AbstractGameCommand implements Confirmationable{
    
    private RaceGame raceGame;
    
    private File file;
    
    private String description;
    
    public RaceGameSave(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Saves a race to file.");
        setUsageDescription(" <filename> <description>: Saves the race locations with the assigned marker names and a <description> to file <filename>.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game) 
                        && isCorrectGameType((Player) cs, game, GameType.RACE)) {
            raceGame = (RaceGame) game;
            file = new File(PluginData.getRaceDir(), args[0] + ".json");
            description = args[1];
            for(int i = 2; i < args.length;i++) {
                description = description + " "+ args[i];
            }
            if(file.exists()) {
                PluginData.getConfirmationFactory().start((Player) cs, 
                        "A race file with that name already exists. Overwrite it?", this);
            }
            else {
                confirmed((Player) cs);  
            }
        }
    }
    
    @Override
    public void confirmed(Player player) {
        try {
            raceGame.getCheckpointManager().saveRace(file, description);
            sendRaceSavedMessage(player);
        } catch (IOException ex) {
            sendIOErrorMessage(player);
            Logger.getLogger(RaceGameSave.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void cancelled(Player player) {
        sendAbordMessage(player);
    }

    private void sendIOErrorMessage(Player player) {
        MessageUtil.sendErrorMessage(player, "There was an error. Nothing was saved.");
    }

    private void sendRaceSavedMessage(Player player) {
        MessageUtil.sendInfoMessage(player, "Race was saved to disk.");
    }

    private void sendAbordMessage(Player player) {
        MessageUtil.sendInfoMessage(player, "Saving race cancelled.");
    }

}
