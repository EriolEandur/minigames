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
import com.mcmiddleearth.pluginutil.NumericUtil;
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
        setUsageDescription(" <categories> | <questionIDs> [matchAll]: Loads questions from the MCME question data table which match the specified categories (see manual). The questions will be appended to the existing questions. If [MatchAll] is 'true' (default is 'false) a question needs to match all specified categories. <questionIDs> may be a list ('2 5 12') or a range ('2-13') of question IDs.");
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
            if(args[0].indexOf("-")>0 && NumericUtil.isInt(args[0].substring(0, args[0].indexOf("-")))) {
                try {
                    List<Integer> questionIds = new ArrayList<>();
                    int start = Integer.parseInt(args[0].substring(0, args[0].indexOf("-")));
                    int end = Integer.parseInt(args[0].substring(args[0].indexOf("-")+1));
                    for(int i = start; i<=end; i++) {
                        questionIds.add(i);
                    }
                    try {
                        int[] result = quizGame.loadQuestionsFromDataFile(file, questionIds);
                        sendQuestionsLoadedMessage(cs, result,questionIds.size());
                    } catch (FileNotFoundException ex) {
                        sendFileNotFoundMessage(cs);
                    }
                } catch(Exception e) {
                    sendInvalidRangeFormat(cs);
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
                //quizGame.clearQuestions(); keep score when loading additional questions
                int[] result = quizGame.loadQuestionsFromDataFile(file, categories, matchAll, maxNumber);
                sendQuestionsLoadedMessage(cs, result, maxNumber);
            } catch (FileNotFoundException ex) {
                sendFileNotFoundMessage(cs);
            }
        }
    }

    private void sendQuestionsLoadedMessage(CommandSender cs, int[] result, int maxNumber) {
        if(result[0]==0) {
            PluginData.getMessageUtil().sendErrorMessage(cs, "Sorry, no question found matching your query.");
        } else if(result[0]<10 && result[0]<maxNumber) {
            PluginData.getMessageUtil().sendInfoMessage(cs, ChatColor.GOLD+"Warning!"+PluginData.getMessageUtil().INFO
                                           +" Only "+PluginData.getMessageUtil().STRESSED+result[0]+PluginData.getMessageUtil().INFO                                           +" questions were found matching your query.");
        } else if(result[0]>result[1]) {
            PluginData.getMessageUtil().sendInfoMessage(cs, "Found "+PluginData.getMessageUtil().STRESSED+result[0]+PluginData.getMessageUtil().INFO
                                            +" Questions. "+PluginData.getMessageUtil().STRESSED+result[1]+PluginData.getMessageUtil().INFO
                                            +" questions loaded.");
        } else if(result[0]<maxNumber) {
            PluginData.getMessageUtil().sendInfoMessage(cs, "Only "+PluginData.getMessageUtil().STRESSED+result[0]+PluginData.getMessageUtil().INFO
                                            +" questions found and loaded.");
        } else {
            PluginData.getMessageUtil().sendInfoMessage(cs, ""+PluginData.getMessageUtil().STRESSED+result[0]+PluginData.getMessageUtil().INFO
                                            +" questions loaded from MCME question table.");
        }
    }

    private void sendFileNotFoundMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Question table file not found.");
    }

    private void sendInvalidRangeFormat(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Invalid question ID range. Format must be <#first>-<#last>.");
    }

}
