/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.QuizGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class QuizGameRandom extends AbstractGameCommand{
    
    public QuizGameRandom(String... permissionNodes) {
        super(0, true, permissionNodes);
        cmdGroup = CmdGroup.LORE_QUIZ;
        setShortDescription(": Defines the order of questions.");
        setUsageDescription(" off|questions|choices|all: 'off' will set all questions and choices to be shown in saved order. 'questions' will show questions in random order. 'choices' will show possible answers for a question in random order. 'all' or just no argument will show questions and choices in random order");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game) 
                        && isCorrectGameType((Player) cs, game, GameType.LORE_QUIZ)) {
            QuizGame quizGame = (QuizGame) game;
            if(args.length>0 && args[0].equalsIgnoreCase("off")) {
                quizGame.setRandom(false, false);
                sendRandomOffMessage(cs);
            }
            else if(args.length>0 && args[0].equalsIgnoreCase("questions")) {
                quizGame.setRandom(true,false); 
                sendRandomQuestionsMessage(cs);
            }
            else if(args.length>0 && args[0].equalsIgnoreCase("choices")) {
                quizGame.setRandom(false,true); 
                sendRandomChoicesMessage(cs);
            }
            else {
                quizGame.setRandom(true,true);
                sendRandomAllMessage(cs);
            }
        }
    }

    private void sendNoWinnerMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "There is no winner.");
    }

    private void sendRandomOffMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Questions and choices will be presented in proper order.");
    }

    private void sendRandomQuestionsMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Questions will be sended in random order.");
    }
    
    private void sendRandomChoicesMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Choices will be presented in random order.");
    }
    
    private void sendRandomAllMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Questions and Choises will be presented in random order.");
    }
}
