/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.QuizGame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class QuizGameStatus extends AbstractGameCommand{
    
    public QuizGameStatus(String... permissionNodes) {
        super(0, true, permissionNodes);
        cmdGroup = CmdGroup.LORE_QUIZ;
        setShortDescription(": Shows players in question conversation.");
        setUsageDescription(": Shows all players who are still in the conversation to answer a question. They may have made an invalid input and aren't aware that they are still in the conversation.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game) 
                        && isCorrectGameType((Player) cs, game, GameType.LORE_QUIZ)) {
            QuizGame quizGame = (QuizGame) game;
            if(!quizGame.isPlayerInQuestion()) {
                PluginData.getMessageUtil().sendInfoMessage(cs, ChatColor.RED+"NO QUESTION running."+ChatColor.AQUA+" Online players:");
                for(Player player: quizGame.getOnlinePlayers()) {
                    PluginData.getMessageUtil().sendIndentedInfoMessage(cs, "- "+player.getName());
                }
            } else {
                PluginData.getMessageUtil().sendInfoMessage(cs, "Players in question conversation:");
                for(Player player: quizGame.getPlayersInQuestion().keySet()) {
                    PluginData.getMessageUtil().sendIndentedInfoMessage(cs, "- "+player.getName());
                }
            }
    }
    
 }

    /*private void sendNotAnnouncedErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You have to announce the game first with /game question done.");
    }*/

    private void sendNoPlayersErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "There are no players in game to send the question to.");
    }

    private void sendNoPlayerInQuestion(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "There are no players in question conversation.");
    }
}
