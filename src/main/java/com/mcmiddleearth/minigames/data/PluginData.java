/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.data;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.conversation.ConfirmationFactory;
import com.mcmiddleearth.minigames.conversation.CreateQuestionConversationFactory;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.raceCheckpoint.Checkpoint;
import com.mcmiddleearth.minigames.utils.PlayerUtil;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class PluginData {
    
    @Getter
    private static CreateQuestionConversationFactory createQuestionFactory;
    
    @Getter
    private static ConfirmationFactory confirmationFactory;
    
    private static final List<OfflinePlayer> noGameChat = new ArrayList<>();
    
    @Getter
    private static final List<AbstractGame> games = new ArrayList<>();
    
    @Getter
    private static final File questionDir = new File(MiniGamesPlugin.getPluginInstance().getDataFolder()
                                                    + File.separator + "QuizQuestions");
    
    @Getter
    private static final File raceDir = new File(MiniGamesPlugin.getPluginInstance().getDataFolder()
                                                    + File.separator + "Races");
    
    static {
        if(!MiniGamesPlugin.getPluginInstance().getDataFolder().exists()) {
            MiniGamesPlugin.getPluginInstance().getDataFolder().mkdirs();
        }
        if(!questionDir.exists()) {
            questionDir.mkdirs();
        }
        if(!raceDir.exists()) {
            raceDir.mkdirs();
        }
    }
   
    public static void createConversationFactories() {
        createQuestionFactory = new CreateQuestionConversationFactory(MiniGamesPlugin.getPluginInstance());
        confirmationFactory = new ConfirmationFactory(MiniGamesPlugin.getPluginInstance());
    }
    
    public static AbstractGame getGame(Player player) {
        for(AbstractGame game : games) {
            if(PlayerUtil.isSame(game.getManager(),player)) {
                return game;
            }
            for(OfflinePlayer search: game.getPlayers()) {
                if(PlayerUtil.isSame(search,player)) {
                    return game;
                }
            }
        }
        return null;
    }
    
    public static AbstractGame getGame(String name) {
        for(AbstractGame game : games) {
            if(game.getName().equalsIgnoreCase(name)) {
                return game;
            }
        }
        return null;
    }
    
    public static boolean gameRunning() {
        for(AbstractGame game : games) {
            if(game.isAnnounced()) {
                return true;
            }
        }
        return false;
    }
    
    public static void addGame(AbstractGame game) {
        games.add(game);
    }
    
    public static void removeGame(AbstractGame game) {
        games.remove(game);
    }
    
    public static boolean isInGame(Player player) {
        for(AbstractGame game : games) {
            return game.isInGame(player);
        }
        return false;
    }
    
    public static boolean isManager(Player player) {
        for(AbstractGame game : games) {
            if(PlayerUtil.isSame(game.getManager(),player)) {
                return true;
            }
        }
        return false;
    }

    public static void setGameChat(Player player, boolean b) {
        if(!b) {
            noGameChat.add(player);
        }
        else {
            noGameChat.remove(player);
        }
    }

    public static boolean getGameChat(OfflinePlayer player) {
        for(OfflinePlayer search: noGameChat) {
            if(PlayerUtil.isSame(search,player)) {
                return false;
            }
        }
       return true;
    }
    
    public static void stopSpectating(Player player) {
        for(AbstractGame game: games) {
            if(game.isSpectating(player)) {
                MessageUtil.sendAllInfoMessage(player, game, player.getName()+" stopped spectating.");
                game.removeSpectator(player);
                return;
            }
        }
    }
    
    public static boolean isSpectating(Player player) {
        for(AbstractGame game: games) {
            if(game.isSpectating(player)) {
                return true;
            }
        }
        return false;
    }
    
    public static void cleanup() {
        Checkpoint.cleanup();
    }
}
