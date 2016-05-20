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
public class QuizGameSend extends AbstractGameCommand{
    
    public QuizGameSend(String... permissionNodes) {
        super(0, true, permissionNodes);
        cmdGroup = CmdGroup.LORE_QUIZ;
        setShortDescription(": Sends the next question.");
        setUsageDescription(" [answerTime]: Sends the next question to all participating players. Without a given [answerTime] players will have 30 sec to answer. A specified [answerTime] will be use for all later questions too.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game) 
                        && isCorrectGameType((Player) cs, game, GameType.LORE_QUIZ)) {
            QuizGame quizGame = (QuizGame) game;
            /*if(!((QuizGame)game).isQuestionsPrepared()) {
                sendNotAnnouncedErrorMessage(cs);
                return;
            }*/
            if(!quizGame.hasNextQuestion()) {
                sendNoMoreQuestions(cs);
                return;
            }
            if(quizGame.countOnlinePlayer()<1) {
                sendNoPlayersErrorMessage(cs);
                return;
            }
            if(args.length>0) {
                try{
                    int answerTime = Integer.parseInt(args[0]);
                    quizGame.setAnswerTime(answerTime);
                } catch(NumberFormatException e) { }
            }
            quizGame.sendQuestion();
    }
    
 }

    /*private void sendNotAnnouncedErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You have to announce the game first with /game question done.");
    }*/

    private void sendNoPlayersErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "There are no players in game to send the question to.");
    }

    private void sendNoMoreQuestions(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "There are no more questions for this game.");
    }
}
