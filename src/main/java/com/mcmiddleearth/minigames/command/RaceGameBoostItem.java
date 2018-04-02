/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.RaceGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class RaceGameBoostItem extends AbstractGameCommand{
    
    public RaceGameBoostItem(String... permissionNodes) {
        super(1, true, permissionNodes);
        cmdGroup = CmdGroup.RACE;
        setShortDescription(": Adds or removes boost items.");
        setUsageDescription(" true|false: Adds or removes boost items from a race game. Can be used before or during the race.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game)
                        && isCorrectGameType((Player) cs, game, GameType.RACE)) {
            RaceGame raceGame = (RaceGame) game;
            //TODO
        }
    }

}
