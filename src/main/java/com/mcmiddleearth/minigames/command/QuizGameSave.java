/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.conversation.confirmation.Confirmationable;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.QuizGame;
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
public class QuizGameSave extends AbstractGameCommand implements Confirmationable{
    
    private QuizGame quizGame;
    
    private File file;
    
    private String description;
    
    public QuizGameSave(String... permissionNodes) {
        super(2, true, permissionNodes);
        cmdGroup = CmdGroup.LORE_QUIZ;
        setShortDescription(": Saves questions to file.");
        setUsageDescription(" <filename> <description>: Saves all questions of the game to file <filename>. A <description> will be saved with the questions.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game) 
                        && isCorrectGameType((Player) cs, game, GameType.LORE_QUIZ)) {
            quizGame = (QuizGame) game;
            file = new File(PluginData.getQuestionDir(), args[0] + ".json");
            description = args[1];
            for(int i = 2; i < args.length;i++) {
                description = description + " "+ args[i];
            }
            if(file.exists()) {
                PluginData.getConfirmationFactory().start((Player) cs, 
                        "A question file with that name already exists. Overwrite it?", this);
            }
            else {
                confirmed((Player) cs);  
            }
        }
    }
    
    @Override
    public void confirmed(Player player) {
        try {
            quizGame.saveQuestionsToJson(file, description);
            sendQuestionsSavedMessage(player);
        } catch (IOException ex) {
            sendIOErrorMessage(player);
            Logger.getLogger(QuizGameSave.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void cancelled(Player player) {
        sendAbordMessage(player);
    }

    private void sendIOErrorMessage(Player player) {
        PluginData.getMessageUtil().sendErrorMessage(player, "There was an error. Nothing was saved.");
    }

    private void sendQuestionsSavedMessage(Player player) {
        PluginData.getMessageUtil().sendInfoMessage(player, "Questions of the game were saved to disk.");
    }

    private void sendAbordMessage(Player player) {
        PluginData.getMessageUtil().sendInfoMessage(player, "Saving questions cancelled.");
    }

}
