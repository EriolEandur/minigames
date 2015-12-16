/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.scoreboard.GameScoreboard;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class QuizGame extends AbstractGame {

    public QuizGame(Player manager, String name) {
        super(manager, name, GameType.LORE_QUIZ, new GameScoreboard("Quiz Game"));
    }

    
}
