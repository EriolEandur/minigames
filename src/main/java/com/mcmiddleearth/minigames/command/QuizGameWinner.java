/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.pluginutils.message.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class QuizGameWinner extends AbstractGameCommand{
    
    public QuizGameWinner(String... permissionNodes) {
        super(0, true, permissionNodes);
        cmdGroup = CmdGroup.LORE_QUIZ;
        setShortDescription(": Announces the winner of a quiz game.");
        setUsageDescription(": Announces the winner of a quiz game. If two or more player have same score after last question, a winner will not be announced automaticall. The manager can add more questions or announce multiple winners with this command.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game) 
                        && isCorrectGameType((Player) cs, game, GameType.LORE_QUIZ)) {
            QuizGame quizGame = (QuizGame) game;
            if(!quizGame.announceWinner(true)) {
                sendNoWinnerMessage(cs);
            }
        }
    }

    private void sendNoWinnerMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "There is no winner.");
    }
}
