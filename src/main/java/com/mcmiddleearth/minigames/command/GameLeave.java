/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.utils.MinigamesMessageUtil;
import com.mcmiddleearth.pluginutils.message.FancyMessage;
import com.mcmiddleearth.pluginutils.message.MessageType;
import com.mcmiddleearth.pluginutils.message.MessageUtil;
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
        setUsageDescription(": The player issuing the command leaves the game he is participating.");
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
        MessageUtil.sendInfoMessage(cs, "You left the minigame "+game.getName()+".");
        MinigamesMessageUtil.sendAllInfoMessage(cs, game, cs.getName() +" left this game.");
    }

    private void sendManagerCantLeaveErrorMessage(CommandSender cs) {
        new FancyMessage(MessageType.ERROR)
                .addSimple("You are not part of a game. You can end a game you manage with: ")
                .addClickable(MessageUtil.ERROR_STRESSED+"/game end","/game end")
                .send((Player) cs);
    }

 }
