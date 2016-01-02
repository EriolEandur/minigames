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
import com.mcmiddleearth.minigames.quizQuestion.AbstractQuestion;
import com.mcmiddleearth.minigames.quizQuestion.QuestionType;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import java.util.logging.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class QuizGameQuestion extends AbstractGameCommand{
    
    public QuizGameQuestion(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": ");
        setUsageDescription(": ");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game) 
                        && isCorrectGameType((Player) cs, game, GameType.LORE_QUIZ)) {
            QuizGame quizGame = (QuizGame) game;
            int questionIndex = -1;
            if(args.length>1) {
                try {
Logger.getGlobal().info("read question index "+questionIndex);
                    questionIndex = Integer.parseInt(args[1])-1;
Logger.getGlobal().info("read question index "+questionIndex+ " question size "+quizGame.getQuestions().size());
                    if(questionIndex <0 || questionIndex > quizGame.getQuestions().size()) {
                        questionIndex=-1;
                    }
                }
                catch(NumberFormatException e) {
                    questionIndex = -1;
                }
            }
            QuestionType type = null;
            if(args.length>0) {
                if(args[0].equalsIgnoreCase("remove")) {
                    if(quizGame.isAnnounced()) {
                        sendAlreadyAnnouncedErrorMessage(cs);
                        return;
                    }
                    if(questionIndex==-1) {
                        sendInvalidIdMessage(cs);
                        return;
                    }
                    else {
                        if(args[0].equalsIgnoreCase("remove")) {
                            quizGame.removeQuestion(questionIndex);
                            sendQuestionRemovedMessage(cs);
                            return;
                        }
                    }
                }
                else if (args[0].equalsIgnoreCase("list")) {
                    sendListQuestionsMessage(cs, quizGame);
                    return;
                }
                else {    
                    type = QuestionType.getQuestionType(args[0]);
                    if(type==null) {
                        sendInvalidQuestionType(cs);
                        return;
                    }
                }
            }
            if(type == null) {
                type = QuestionType.SINGLE;
            }
Logger.getGlobal().info("create question index "+questionIndex);
            PluginData.getCreateQuestionFactory().start((Player)cs, quizGame, type, questionIndex);
        }
    }
 
    private void sendInvalidQuestionType(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You specified an invalid question type. Valid question types are: /game question [free|number|mulit|single]");
    }

    private void sendInvalidIdMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You have to specify a valid question id");
    }

    private void sendQuestionRemovedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Question removed.");
    }

    private void sendListQuestionsMessage(CommandSender cs, QuizGame quizGame) {
        if(quizGame.getQuestions().isEmpty()) {
            MessageUtil.sendInfoMessage(cs, "No Questions in this game.");
            return;
        }
        MessageUtil.sendInfoMessage(cs, "Questions in this game:");
        int id = 1;
        for(AbstractQuestion question : quizGame.getQuestions()) {
            MessageUtil.sendNoPrefixInfoMessage(cs, id+": "+question.getQuestion());
            id++;
        }
    }
    
 }
