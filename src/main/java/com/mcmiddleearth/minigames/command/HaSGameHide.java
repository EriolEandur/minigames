/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.HideAndSeekGame;
import com.mcmiddleearth.pluginutils.StringUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur 
 */
public class HaSGameHide extends AbstractGameCommand{
    
    public HaSGameHide(String... permissionNodes) {
        super(1, true, permissionNodes);
        cmdGroup = CmdGroup.HIDE_AND_SEEK;
        setShortDescription(": Starts a round of Hide and Seek.");
        setUsageDescription(" <radius> [seektime] [hidetime]: The number <radius> determines the size of a sphere which cages the players. When no [seektime] in seconds is given seek time is 300 sec by default. Without a given [hidetime] in seconds, time for hiding is 60 sec.");
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
        PluginData.getMessageUtil().sendErrorMessage(cs, "Not enough players in game. Minimum is two.");
    }
    
 }
