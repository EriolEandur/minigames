/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

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
public class QuizGameClear extends AbstractGameCommand{
    
    public QuizGameClear(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Removes all questions from a quiz game.");
        setUsageDescription(": Removes all questions from a quiz game.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game) 
                        && isCorrectGameType((Player) cs, game, GameType.LORE_QUIZ)) {
            QuizGame quizGame = (QuizGame) game;
            quizGame.clearQuestions();
            sendClearedGameMessage(cs);
        }
    }

    private void sendClearedGameMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You removed all questions from this Lore Quiz.");
    }
    
}
