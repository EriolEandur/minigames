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
public class QuizGameRestart extends AbstractGameCommand{
    
    public QuizGameRestart(String... permissionNodes) {
        super(0, true, permissionNodes);
        cmdGroup = CmdGroup.LORE_QUIZ;
        setShortDescription(": Restarts a quiz game.");
        setUsageDescription(": Restarts a quiz game which is resetting all scores and marking all questions as not answered.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game) 
                        && isCorrectGameType((Player) cs, game, GameType.LORE_QUIZ)) {
            QuizGame quizGame = (QuizGame) game;
            quizGame.resetQuestions();
            sendResetGameMessage(cs);
        }
    }

    private void sendResetGameMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "You restarted the Lore Quiz.");
    }
    
}
