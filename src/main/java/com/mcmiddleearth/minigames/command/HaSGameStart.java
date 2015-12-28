/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.HideAndSeekGame;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import com.mcmiddleearth.minigames.utils.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur 
 */
public class HaSGameStart extends AbstractGameCommand{
    
    public HaSGameStart(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": ");
        setUsageDescription(": ");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game) 
                        && isCorrectGameType((Player)cs, game, GameType.HIDE_AND_SEEK)) {
            if(game.countOnlinePlayer()<2) {
                sendNotEnoughPlayerErrorMessage(cs);
            }
            else {
                HideAndSeekGame hasGame = (HideAndSeekGame) game;
                int radius = StringUtil.parseInt(args[0]);
                if(args.length>1) {
                    int searchTime = StringUtil.parseInt(args[1]);
                    hasGame.setSeekTime(searchTime);
                }
                if(args.length>2) {
                    int hideTime = StringUtil.parseInt(args[2]);
                    hasGame.setHideTime(hideTime);
                }
                hasGame.hiding(radius);
            }
        }
    }

    private void sendNotEnoughPlayerErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Not enough players in game. Minimum is two.");
    }
    
 }
