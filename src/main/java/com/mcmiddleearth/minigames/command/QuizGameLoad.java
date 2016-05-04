/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.pluginutils.message.MessageUtil;
import java.io.File;
import java.io.FileNotFoundException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Eriol_Eandur
 */
public class QuizGameLoad extends AbstractGameCommand{
    
    public QuizGameLoad(String... permissionNodes) {
        super(0, true, permissionNodes);
        cmdGroup = CmdGroup.LORE_QUIZ;
        setShortDescription(": Loads questions from a quiz data file.");
        setUsageDescription(" <filename>: Loads all questions from the file <filename>. The questions will be appended to the existing questions.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game) 
                        && isCorrectGameType((Player) cs, game, GameType.LORE_QUIZ)) {
            if(args.length==0) {
                MiniGamesPlugin.getPluginInstance().getCommand("game").getExecutor().onCommand(cs, null, "game", new String[]{"files","quiz"});
                return;
            }
            QuizGame quizGame = (QuizGame) game;
            File file = new File(PluginData.getQuestionDir(), args[0] + ".json");
            try {
                quizGame.loadQuestionsFromJson(file);
                sendQuestionsLoadedMessage(cs);
            } catch (FileNotFoundException ex) {
                sendFileNotFoundMessage(cs);
            } catch (ParseException ex) {
                sendInvalidFileMessage(cs);
            }
        }
    }

    private void sendQuestionsLoadedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "Questions loaded from file.");
    }

    private void sendFileNotFoundMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "File not found.");
    }

    private void sendInvalidFileMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "The file contains invalid data.");
    }

}
