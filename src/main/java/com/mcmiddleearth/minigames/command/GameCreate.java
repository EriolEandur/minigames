/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.HideAndSeekGame;
import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.minigames.game.RaceGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameCreate extends AbstractGameCommand{
    
    public GameCreate(String... permissionNodes) {
        super(2, true, permissionNodes);
        setShortDescription(": Creates a new mini game.");
        setUsageDescription(" quiz|race|hide <gamename>: Creates a lore quiz or a race or a hide and seek game with name <gamename>. The location of the player issuing the command becomes the warp of the game.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        if(!isAlreadyInGame((Player)cs) && !isAlreadyManager((Player) cs)) {
            if(PluginData.getGame(args[1]) != null) {
                sendGameExistsMessage(cs);
                return;
            }
            AbstractGame game;
            GameType type = GameType.getGameType(args[0]);
            if(type==null) {
                sendInvalidGameTypeErrorMessage(cs);
                return;
            }
            switch(type) {
                case HIDE_AND_SEEK:
                    PluginData.stopSpectating((Player)cs);
                    game = new HideAndSeekGame((Player) cs, args[1]);
                    break;
                case RACE:
                    PluginData.stopSpectating((Player)cs);
                    game = new RaceGame((Player) cs, args[1]);
                    sendRaceGameCreateMessage(cs);
                    break;
                case LORE_QUIZ:
                    PluginData.stopSpectating((Player)cs);
                    game = new QuizGame((Player) cs, args[1]);
                    sendQuizGameCreateMessage(cs);
                    break;
                default:
                    sendInvalidGameTypeErrorMessage(cs);
                    return;
            }
            if(args.length>2 && args[2].equalsIgnoreCase("private")) {
                game.setPrivat(true);
            }
            PluginData.addGame(game);
        }
    }
    
    public void sendQuizGameCreateMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You created a new Lore Quiz game.");
    }

    private void sendInvalidGameTypeErrorMessage(CommandSender cs) {
         PluginData.getMessageUtil().sendErrorMessage(cs, "You specified an invalid game type.");
    }
    
    private void sendGameExistsMessage(CommandSender cs) {
         PluginData.getMessageUtil().sendErrorMessage(cs, "A game with that name already exists.");
    }

    private void sendRaceGameCreateMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You created a new Race game.");
    }

 }
