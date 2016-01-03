/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class GameInvite extends AbstractGameCommand{
    
    public GameInvite(String... permissionNodes) {
        super(1, true, permissionNodes);
        setShortDescription(": Invites a player to join a game.");
        setUsageDescription(" <player>: Invites <player> to join the game. This is neeed if '/game deny join' was used.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game!=null && isManager((Player) cs, game)) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
            game.invite(player);
            sendPlayerInvited(cs, player);
        }
    }
    
    private void sendPlayerInvited(CommandSender cs, OfflinePlayer player) {
        MessageUtil.sendInfoMessage(cs, "You invited "+player.getName()+" to your game.");
    }
}
