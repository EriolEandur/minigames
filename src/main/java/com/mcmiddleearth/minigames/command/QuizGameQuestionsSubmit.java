/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.minigames.quizQuestion.AbstractQuestion;
import com.mcmiddleearth.minigames.quizQuestion.QuestionType;
import com.mcmiddleearth.pluginutils.message.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class QuizGameQuestionsSubmit extends AbstractGameCommand{
    
    public QuizGameQuestionsSubmit(String... permissionNodes) {
        super(0, true, permissionNodes);
        cmdGroup = CmdGroup.LORE_QUIZ;
        setShortDescription(": Submits a quiz question.");
        setUsageDescription(" single|multi|free|number: Initiates a conversation to create a new question of the specified type. Without a type a single choice question is created.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        QuestionType type = null;
        if(args.length>0) {
            type = QuestionType.getQuestionType(args[0]);
            if(type==null) {
                sendInvalidQuestionType(cs);
                return;
            }
        }
        if(type == null) {
            type = QuestionType.SINGLE;
        }
        PluginData.getCreateQuestionFactory().start((Player)cs, PluginData.getQuestionSubmitGame(), type, -1);
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
            MessageUtil.sendIndentedInfoMessage(cs, id+": "+question.getQuestion());
            id++;
        }
    }
    
 }
