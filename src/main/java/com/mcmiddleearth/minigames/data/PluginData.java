/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.data;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.conversation.confirmation.ConfirmationFactory;
import com.mcmiddleearth.minigames.conversation.quiz.CreateQuestionConversationFactory;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.minigames.raceCheckpoint.Checkpoint;
import com.mcmiddleearth.pluginutil.PlayerUtil;
import com.mcmiddleearth.minigames.utils.GameChatUtil;
import com.mcmiddleearth.pluginutil.message.MessageUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Eriol_Eandur
 */
public class PluginData {
    
    @Getter
    private final static MessageUtil messageUtil = new MessageUtil();
    
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
    private static final File questionDataTable = new File(questionDir,"questionTable.dat");
    
    @Getter
    private static final File submittedQuestionsFile = new File(questionDir,"submitted.json");
    
    @Getter 
    static final QuizGame questionSubmitGame = new QuizGame(null, "submitQuestions");
    
    @Getter
    private static final File questionCategoriesFile = new File(questionDir,"questionCategories.dat");
    
    @Getter
    private static final List<String> questionCategories = new ArrayList<>();
    
    @Getter
    private static final File raceDir = new File(MiniGamesPlugin.getPluginInstance().getDataFolder()
                                                    + File.separator + "Races");

    @Getter
    private static final File golfDir = new File(MiniGamesPlugin.getPluginInstance().getDataFolder()
            + File.separator + "Courses");

    @Getter
    private static final File pvpDirectory = new File(MiniGamesPlugin.getPluginInstance().getDataFolder()
            + File.separator + "Arenas");

    @Getter
    private static final File loadoutDirectory = new File(MiniGamesPlugin.getPluginInstance().getDataFolder()
            + File.separator + "Loadouts");
    
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

        if(!golfDir.exists()) {
            golfDir.mkdirs();
        }

        if(!pvpDirectory.exists()) {
            pvpDirectory.mkdirs();
        }

        if(!loadoutDirectory.exists()) {
            loadoutDirectory.mkdirs();
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
            if(game.getPlayers().contains(player.getUniqueId())) {
                return game;
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
                GameChatUtil.sendAllInfoMessage(player, game, player.getName()+" stopped spectating.");
                PluginData.getMessageUtil().sendInfoMessage(player, "You stopped spectating.");
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
    
    public static void load() {
        try {
            try (Scanner reader = new Scanner(questionCategoriesFile, StandardCharsets.UTF_8.name())) {
                questionCategories.clear();
                if(reader.hasNext()) {
                    reader.nextLine();
                }
                while(reader.hasNext()){
                    questionCategories.add(reader.nextLine());
                }
            }
        } catch (FileNotFoundException ex) {
            MiniGamesPlugin.getPluginInstance().getLogger().log(Level.SEVERE, null, ex);
        }
        try {
            questionSubmitGame.loadQuestionsFromJson(submittedQuestionsFile);
        } catch (FileNotFoundException | ParseException ex) {
            Logger.getLogger(PluginData.class.getName()).log(Level.INFO, "No submitted questions found.");
        }
    }
    
    public static boolean areValidCategories(String categories) {
        for(Character letter: categories.toCharArray()) {
            boolean found = false;
            for(String search: questionCategories) {
                if(search.charAt(0)==letter) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                return false;
            }
        }
        return true;
    }

    public static WorldEditPlugin getWorldEdit() {
        return (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
    }
}
