/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.pluginutils.message.MessageUtil;
import java.io.File;
import java.util.Calendar;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class QuizGameQuestionsReview extends AbstractGameCommand{
    
    public QuizGameQuestionsReview(String... permissionNodes) {
        super(0, true, permissionNodes);
        cmdGroup = CmdGroup.LORE_QUIZ;
        setShortDescription(": Creates a new mini game.");
        setUsageDescription(" quiz|race|hide <gamename>: Creates a lore quiz or a race or a hide and seek game with name <gamename>. The location of the player issuing the command becomes the warp of the game.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        if(args.length>0 && args[0].equalsIgnoreCase("check")) {
            sendReviewCheckMessage(cs, PluginData.getQuestionSubmitGame().countQuestions());
            return;
        }
        if(!isAlreadyInGame((Player)cs) && !isAlreadyManager((Player) cs)) {
            File submittedFile = PluginData.getSubmittedQuestionsFile();
            if(submittedFile.exists()) {
                Calendar calendar = Calendar.getInstance();
                String filename = "r"
                                   +calendar.get(Calendar.YEAR)+"_"
                                   +calendar.get(Calendar.MONTH)+"_"
                                   +calendar.get(Calendar.DAY_OF_MONTH);
                File file = new File(PluginData.getQuestionDir(),filename+".json");
                int i = 1;
                while(file.exists()) {
                    i++;
                    file = new File(PluginData.getQuestionDir(),filename+"_"+i+".json");
                }
                if(i>1) {
                    filename+="_"+i;
                }
                submittedFile.renameTo(file);
                PluginData.getQuestionSubmitGame().clearQuestions();
                int index = 1;
                while(PluginData.getGame("review"+index)!=null) {
                    index++;
                }
                PluginData.stopSpectating((Player)cs);
                QuizGame game = new QuizGame((Player) cs, "review"+index);
                sendQuizGameCreateMessage(cs,filename);
                PluginData.addGame(game);
                game.setPrivat(true);
                MiniGamesPlugin.getPluginInstance().getCommand("game").getExecutor().
                                onCommand(cs, null, "game", new String[]{"loadquiz",filename});
            } else {
                sendNoQuestionsSubmittedMessage(cs);
            }
        }
    }
    
    public void sendQuizGameCreateMessage(CommandSender cs, String filename) {
        MessageUtil.sendInfoMessage(cs, "A quiz game with all submitted questions was created to review.");
        MessageUtil.sendIndentedInfoMessage(cs, "Submitted questions saved in file: "+ filename);
        MessageUtil.sendIndentedInfoMessage(cs, "Don't forget to delete when no loger needed:");
        MessageUtil.sendIndentedInfoMessage(cs, ChatColor.DARK_AQUA+"/game delete quiz "+filename);
    }

    private void sendNoQuestionsSubmittedMessage(CommandSender cs) {
        MessageUtil.sendInfoMessage(cs, "There are no submitted questions to review.");
    }

    private void sendReviewCheckMessage(CommandSender cs, int count) {
        if(count==0) {
            MessageUtil.sendInfoMessage(cs, "There are no submitted questions to review.");
        } else if (count==1) {
            MessageUtil.sendInfoMessage(cs, "There is ONE submitted questions to review.");
        } else {
            MessageUtil.sendInfoMessage(cs, "There are "+count+" submitted questions to review.");
        }
    }

 }
