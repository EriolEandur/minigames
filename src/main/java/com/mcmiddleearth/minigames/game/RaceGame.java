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
public class RaceGame extends AbstractGame {

    public RaceGame(Player manager, String name) {
        super(manager, name, GameType.RACE, new GameScoreboard("Race Game"));
    }

   
}
