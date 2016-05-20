/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.Permissions;
import com.mcmiddleearth.minigames.conversation.quiz.EditQuestionConversationFactory;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.minigames.quizQuestion.AbstractQuestion;
import com.mcmiddleearth.minigames.quizQuestion.QuestionType;
import com.mcmiddleearth.pluginutils.NumericUtil;
import com.mcmiddleearth.pluginutils.message.FancyMessage;
import com.mcmiddleearth.pluginutils.message.MessageType;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class QuizGameQuestion extends AbstractGameCommand{
    
    public QuizGameQuestion(String... permissionNodes) {
        super(0, true, permissionNodes);
        cmdGroup = CmdGroup.LORE_QUIZ;
        setShortDescription(": Manipulates questions of a quiz game.");
        setUsageDescription(" single|multi|free|number|list|remove [#ID]: Arguments single, multi, free and number will initiate a conversation to create a new question, which will be added to the quiz. Argument 'remove' will delete the questions number [questionID]. Argument list will display a list of all questions of a game.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        if(args.length>0) {
            String[] newArgs = new String[args.length];
            System.arraycopy(args, 1, newArgs, 1, args.length-1);
            if(args[0].equalsIgnoreCase("submit")) {
                newArgs[0]="submitquestion";
                MiniGamesPlugin.getPluginInstance().getCommand("game").getExecutor().
                                onCommand(cs, null, "game", newArgs);
                return;
            }else if(args[0].equalsIgnoreCase("review")) {
                newArgs[0]="reviewquestions";
                MiniGamesPlugin.getPluginInstance().getCommand("game").getExecutor().
                                onCommand(cs, null, "game", newArgs);
                return;
            }else if(args[0].equalsIgnoreCase("accept")) {
                newArgs[0]="acceptquestions";
                MiniGamesPlugin.getPluginInstance().getCommand("game").getExecutor().
                                onCommand(cs, null, "game", newArgs);
                return;
            }else if(args[0].equalsIgnoreCase("load")) {
                newArgs[0]="loadquestions";
                MiniGamesPlugin.getPluginInstance().getCommand("game").getExecutor().
                                onCommand(cs, null, "game", newArgs);
                return;
            }
        }
        if(!((Player)cs).hasPermission(Permissions.MANAGER)) {
            sendNoPermsErrorMessage(cs);
            return;
        }
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game) 
                        && isCorrectGameType((Player) cs, game, GameType.LORE_QUIZ)) {
            QuizGame quizGame = (QuizGame) game;
            int questionIndex = -1;
            if(args.length>1) {
                try {
                    questionIndex = Integer.parseInt(args[1])-1;
                    if(questionIndex <0 || questionIndex > quizGame.getQuestions().size()) {
                        questionIndex=-1;
                    }
                }
                catch(NumberFormatException e) {
                    questionIndex = -1;
                }
            }
            QuestionType type = null;
            if(args.length>0) {
                if(args[0].equalsIgnoreCase("remove")) {
                    if(quizGame.isAnnounced()) {
                        sendAlreadyAnnouncedErrorMessage(cs);
                        return;
                    }
                    if(questionIndex==-1) {
                        sendInvalidIdMessage(cs);
                        return;
                    }
                    quizGame.removeQuestion(questionIndex);
                    sendQuestionRemovedMessage(cs);
                    return;
                }
                else if(args[0].equalsIgnoreCase("edit")) {
                    if(questionIndex==-1) {
                        sendInvalidIdMessage(cs);
                        return;
                    }
                    if(((Player)cs).isConversing()) {
                        sendAlreadyConversionMessage(cs);
                        return;
                    }
                    EditQuestionConversationFactory editFactory 
                             = new EditQuestionConversationFactory(MiniGamesPlugin.getPluginInstance());
                    editFactory.start((Player)cs, quizGame, questionIndex);
                    return;
                }
                else if (args[0].equalsIgnoreCase("list")) {
                    int page = 1;
                    if(args.length>1 && NumericUtil.isInt(args[1])){
                        page = NumericUtil.getInt(args[1]);
                    }
                    sendListQuestionsMessage(cs, quizGame, page);
                    return;
                }
                else {    
                    type = QuestionType.getQuestionType(args[0]);
                    if(type==null) {
                        sendInvalidQuestionType(cs);
                        return;
                    }
                }
            }
            if(type == null) {
                type = QuestionType.SINGLE;
            }
            if(((Player)cs).isConversing()) {
                sendAlreadyConversionMessage(cs);
                return;
            }
            PluginData.getCreateQuestionFactory().start((Player)cs, quizGame, type, questionIndex);
        }
    }
 
    private void sendInvalidQuestionType(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You specified an invalid question type. Valid question types are: /game question [free|number|mulit|single]");
    }

    private void sendInvalidIdMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You have to specify a valid question id");
    }

    private void sendQuestionRemovedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Question removed.");
    }

    private void sendListQuestionsMessage(CommandSender cs, QuizGame quizGame, int page) {
        if(quizGame.getQuestions().isEmpty()) {
            PluginData.getMessageUtil().sendInfoMessage(cs, "No Questions in this game.");
            return;
        }
        FancyMessage header = new FancyMessage(MessageType.INFO,PluginData.getMessageUtil())
                                    .addSimple("Questions in this game. ");
        List<FancyMessage> list = new ArrayList<>();
        int id = 1;
        for(AbstractQuestion question : quizGame.getQuestions()) {
            String questionText = question.getQuestion().replaceAll("\"", ";\"");
            questionText = questionText.replace(';', '\\');
            String[] detailText = question.getDetails();
            list.add(new FancyMessage(MessageType.WHITE,PluginData.getMessageUtil())
                        .addFancy(ChatColor.DARK_GREEN+""+id+ChatColor.AQUA+" ["
                                    +(question.getId()==0?"-":question.getId())+"]"
                                    +": ",
                                "/game question edit "+id,
                                hoverFormat(detailText))
                        .addClickable(ChatColor.WHITE+questionText, "/game question edit "+id));
            id++;
        }
        PluginData.getMessageUtil().sendFancyListMessage((Player)cs, header, list, "/game question list", page);
    }

    private void sendAlreadyConversionMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You are already in a Conversation.");
    }
    
    private String hoverFormat(String[] hoverMessage) {
        class MyScanner {
            private final Scanner scanner;
            public String currentToken=null;
            public MyScanner(String string) {
                scanner = new Scanner(string);
                scanner.useDelimiter(" ");
                if(scanner.hasNext()) {
                    currentToken = scanner.next();
                }
            }
            public String next() {
                if(scanner.hasNext()) {
                    currentToken = scanner.next();
                } else {
                    currentToken = null;
                }
                return currentToken;
            }
            public boolean hasCurrent() {
                return currentToken != null;
            }
            public boolean hasNext() {
                return scanner.hasNext();
            }
        }
        int LENGTH_OF_LINE = 70;
        String result = "";
        for(String str:hoverMessage) {
            MyScanner scanner = new MyScanner(str);
            while (scanner.hasCurrent()) {
                String line = scanner.currentToken+" ";
                scanner.next();
                while(scanner.hasCurrent() && line.length()+scanner.currentToken.length()<LENGTH_OF_LINE) {
                    line = line.concat(scanner.currentToken+" ");
                    scanner.next();
                }
                line = line.concat("\n"+PluginData.getMessageUtil().HIGHLIGHT_STRESSED);
                result = result.concat(line);
            }
        }
        return result;
    }
    

 }
