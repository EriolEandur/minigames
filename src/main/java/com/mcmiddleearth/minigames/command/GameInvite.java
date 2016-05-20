/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.pluginutils.message.FancyMessage;
import com.mcmiddleearth.pluginutils.message.MessageType;
import org.bukkit.Bukkit;
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
            Player player = Bukkit.getPlayer(args[0]);
            if(player != null) {
                game.invite(player);
                sendPlayerInvited(cs, player, game);
            }
            else {
                sendNotFoundMessage(cs);
            }
        }
    }
    
    private void sendPlayerInvited(CommandSender cs, Player player, AbstractGame game) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You invited "+player.getName()+" to your game.");
            new FancyMessage(MessageType.INFO,PluginData.getMessageUtil())
                    .addClickable(PluginData.getMessageUtil().STRESSED+cs.getName()
                                    +PluginData.getMessageUtil().INFO+" invited you to a "
                                    +PluginData.getMessageUtil().STRESSED+game.getType().toString()
                                    +PluginData.getMessageUtil().INFO+" game. "
                                    +PluginData.getMessageUtil().STRESSED+"Click here"
                                    +PluginData.getMessageUtil().INFO+" to join or type in chat: /game join "+game.getName(),
                                  "/game join "+game.getName())
                    .send(player);
    }

    private void sendNotFoundMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Player not found, you need to type in the full name and the player needs to be online.");
    }
}
