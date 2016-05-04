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
import com.mcmiddleearth.minigames.utils.MinigamesMessageUtil;
import com.mcmiddleearth.pluginutils.NumericUtil;
import com.mcmiddleearth.pluginutils.message.MessageUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class QuizGameQuestionsLoad extends AbstractGameCommand{
    
    public QuizGameQuestionsLoad(String... permissionNodes) {
        super(1, true, permissionNodes);
        cmdGroup = CmdGroup.LORE_QUIZ;
        setShortDescription(": Loads questions from the question table.");
        setUsageDescription(" <categories> [matchAll]: Loads questions from the MCME question data table which match the specified categories (see manual). The questions will be appended to the existing questions. If [MatchAll] is 'true' (default is 'false) a question needs to match all specified categories.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game) 
                        && isCorrectGameType((Player) cs, game, GameType.LORE_QUIZ)) {
            QuizGame quizGame = (QuizGame) game;
            File file = PluginData.getQuestionDataTable();
            if(NumericUtil.isInt(args[0])) {
                List<Integer> questionIds = new ArrayList<>();
                questionIds.add(NumericUtil.getInt(args[0]));
                for(int i = 1; i<args.length;i++) {
                    if(NumericUtil.isInt(args[i])) {
                        questionIds.add(NumericUtil.getInt(args[i]));
                    }
                }
                try {
                    int[] result = quizGame.loadQuestionsFromDataFile(file, questionIds);
                    sendQuestionsLoadedMessage(cs, result,questionIds.size());
                } catch (FileNotFoundException ex) {
                    sendFileNotFoundMessage(cs);
                }
                return;
            }
            boolean matchAll = false;
            String categories = args[0];
            int maxNumber = 15;
            if(args.length>1 && args[1].equalsIgnoreCase("true")) {
                matchAll = true;
            }
            if(args.length==2 && NumericUtil.isInt(args[1])) {
                maxNumber = Math.min(100,NumericUtil.getInt(args[1]));
            }
            if(args.length>2 && NumericUtil.isInt(args[2])) {
                maxNumber = Math.min(100,NumericUtil.getInt(args[2]));
            }
            try {
                quizGame.clearQuestions();
                int[] result = quizGame.loadQuestionsFromDataFile(file, categories, matchAll, maxNumber);
                sendQuestionsLoadedMessage(cs, result, maxNumber);
            } catch (FileNotFoundException ex) {
                sendFileNotFoundMessage(cs);
            }
        }
    }

    private void sendQuestionsLoadedMessage(CommandSender cs, int[] result, int maxNumber) {
        if(result[0]==0) {
            MessageUtil.sendErrorMessage(cs, "Sorry, no question found matching your query.");
        } else if(result[0]<10 && result[0]<maxNumber) {
            MessageUtil.sendInfoMessage(cs, ChatColor.GOLD+"Warning!"+MessageUtil.INFO
                                           +" Only "+MessageUtil.STRESSED+result[0]+MessageUtil.INFO                                           +" questions were found matching your query.");
        } else if(result[0]>result[1]) {
            MessageUtil.sendInfoMessage(cs, "Found "+MessageUtil.STRESSED+result[0]+MessageUtil.INFO
                                            +" Questions. "+MessageUtil.STRESSED+result[1]+MessageUtil.INFO
                                            +" questions loaded.");
        } else if(result[0]<maxNumber) {
            MessageUtil.sendInfoMessage(cs, "Only "+MessageUtil.STRESSED+result[0]+MessageUtil.INFO
                                            +" questions found and loaded.");
        } else {
            MessageUtil.sendInfoMessage(cs, ""+MessageUtil.STRESSED+result[0]+MessageUtil.INFO
                                            +" questions loaded from MCME question table.");
        }
    }

    private void sendFileNotFoundMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Question table file not found.");
    }

}
