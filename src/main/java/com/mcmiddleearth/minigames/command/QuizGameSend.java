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
import com.mcmiddleearth.minigames.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class QuizGameSend extends AbstractGameCommand{
    
    public QuizGameSend(String... permissionNodes) {
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
            int answerTime = 0;
            if(args.length>0) {
                try{
                    answerTime = Integer.parseInt(args[0]);
                } catch(NumberFormatException e) { }
            }
            quizGame.sendQuestion(answerTime);
    }
    
 }

    /*private void sendNotAnnouncedErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You have to announce the game first with /game question done.");
    }*/

    private void sendNoPlayersErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "There are no players in game to send the question to.");
    }

    private void sendNoMoreQuestions(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "There are no more questions for this game.");
    }
}
