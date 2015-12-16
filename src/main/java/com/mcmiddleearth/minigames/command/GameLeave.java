/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameLeave extends AbstractGameCommand{
    
    public GameLeave(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Leaves a mini game.");
        setUsageDescription(": ");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null) {
            if(game.isInGame((Player) cs)) {
                game.removePlayer((Player) cs);
                sendPlayerLeaveMessage(cs, game);
            }
            else {
                sendManagerCantLeaveErrorMessage(cs);
            }
        }
    }
    
    public void sendPlayerLeaveMessage(CommandSender cs, AbstractGame game) {
        MessageUtil.sendInfoMessage(cs, "You left the minigame "+game.getName());
        MessageUtil.sendAllInfoMessage(cs, game, cs.getName() +" left this game.");
    }

    private void sendManagerCantLeaveErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Use /game end instead.");
    }

 }
