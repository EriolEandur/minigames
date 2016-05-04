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
import com.mcmiddleearth.pluginutils.message.MessageUtil;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class QuizGameQuestionsAccept extends AbstractGameCommand implements Confirmationable{
    
    private QuizGame quizGame;
    
    public QuizGameQuestionsAccept(String... permissionNodes) {
        super(0, true, permissionNodes);
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
            PluginData.getConfirmationFactory().start((Player) cs, 
                    "Are you sure to add all questions of this quiz to the MCME question data table?", this);
        }
    }
    
    @Override
    public void confirmed(Player player) {
        try {
            quizGame.saveQuestionsToDataFile(PluginData.getQuestionDataTable());
            sendQuestionsSavedMessage(player);
        } catch (IOException ex) {
            sendIOErrorMessage(player);
            Logger.getLogger(QuizGameQuestionsAccept.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void cancelled(Player player) {
        sendAbordMessage(player);
    }

    private void sendIOErrorMessage(Player player) {
        MessageUtil.sendErrorMessage(player, "There was an error. Nothing was saved.");
    }

    private void sendQuestionsSavedMessage(Player player) {
        MessageUtil.sendInfoMessage(player, "Questions of the quiz were accepted.");
    }

    private void sendAbordMessage(Player player) {
        MessageUtil.sendInfoMessage(player, "Accepting questions cancelled.");
    }

}
