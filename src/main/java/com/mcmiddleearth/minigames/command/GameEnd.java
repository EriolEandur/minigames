/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameEnd extends AbstractGameCommand{
    
    public GameEnd(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Ends a game.");
        setUsageDescription(": Ends the curent game of the player issuing this command.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game!=null && isManager((Player) cs, game)) {
            sendGameEndMessage(cs, game);
            game.end((Player) cs);
            ((Player)cs).setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }
    
    public void sendGameEndMessage(CommandSender cs, AbstractGame game) {
        MessageUtil.sendInfoMessage(cs, "You ended your minigame.");
    }
 }
