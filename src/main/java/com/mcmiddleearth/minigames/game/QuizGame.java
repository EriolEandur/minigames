/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.conversation.AskQuestionConversationFactory;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.quizQuestion.AbstractQuestion;
import com.mcmiddleearth.minigames.quizQuestion.ChoiceQuestion;
import com.mcmiddleearth.minigames.quizQuestion.FreeQuestion;
import com.mcmiddleearth.minigames.quizQuestion.NumberQuestion;
import com.mcmiddleearth.minigames.quizQuestion.QuestionType;
import com.mcmiddleearth.minigames.quizQuestion.SingleChoiceQuestion;
import com.mcmiddleearth.minigames.scoreboard.QuizGameScoreboard;
import com.mcmiddleearth.minigames.utils.PlayerUtil;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import com.mcmiddleearth.minigames.utils.TitleUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 *
 * @author Eriol_Eandur
 */
public class QuizGame extends AbstractGame {

    @Getter
    private final List<AbstractQuestion> questions = new ArrayList<>();
    
    private boolean randomQuestions = true;

    @Getter
    private boolean randomChoices = true;
    
    private int nextQuestion = 0;

    private final int defaultAnswerTime = 60; //seconds
    
    private final List<Player> playersInQuestion = new ArrayList<>();

    public QuizGame(Player manager, String name) {
        super(manager, name, GameType.LORE_QUIZ, new QuizGameScoreboard());
    }

    @Override
    public void addPlayer(Player player) {
        super.addPlayer(player);
        ((QuizGameScoreboard)getBoard()).addPlayer(player.getName());
    }
    
    @Override
    public void playerMove(PlayerMoveEvent event) {
        //overrides limited game area in AbstractGame
    }
   
    @Override
    public String getGameChatTag(Player player) {
        if(PlayerUtil.isSame(getManager(), player)) {
            return ChatColor.DARK_AQUA + "<Presentator ";
        }
        else {
            return super.getGameChatTag(player);
        }
    }
    
    public void setRandom(boolean question, boolean choice) {
        if(this.randomQuestions && !question) {
            nextQuestion = 0;
        }
        this.randomQuestions = question;
        this.randomChoices = choice;
    }
    
    public void addQuestion(AbstractQuestion question, int index) {
        if(index==-1) {
            questions.add(question);
Logger.getGlobal().info("add question");
        }
        else {
Logger.getGlobal().info("insert question");
            questions.add(index, question);
            if(nextQuestion>=index) {
                nextQuestion++;
            }
        }
        ((QuizGameScoreboard)getBoard()).addQuestion();
    }
    
    public void removeQuestion(int index) {
        questions.remove(index);
        ((QuizGameScoreboard)getBoard()).removeQuestion();
    }
    
    public void resetQuestions() {
        nextQuestion = 0;
        for(AbstractQuestion search: questions) {
            search.setAnswered(false);
        }
        ((QuizGameScoreboard)getBoard()).restart();
    }
    
    public boolean hasNextQuestion() {
        for(AbstractQuestion search: questions) {
            if(!search.isAnswered()){
                return true;
            }
        }
        return false;
    }
    
    public AbstractQuestion getNextQuestion() {
        if(hasNextQuestion()) {
            if(randomQuestions) {
                int questionsLeft = 0;
                for(AbstractQuestion search: questions) {
                    if(!search.isAnswered()){
                        questionsLeft++;
                    }
                }
                int rand = (int) Math.round(Math.floor(questionsLeft*Math.random()));
                nextQuestion = 0;
                while(questions.get(nextQuestion).isAnswered()) {
                    nextQuestion++;
                }
                for(int i=0; i<rand; i++) {
                    nextQuestion++;
                    while(questions.get(nextQuestion).isAnswered()) {
                        nextQuestion++;
                    }
                }
            }
            return questions.get(nextQuestion);
        }
        else { 
            return null;
        }
    }
    
    public void sendQuestion(int answerTime) {
        if(hasNextQuestion()) {
            AbstractQuestion question = getNextQuestion();
            nextQuestion++;
            question.setAnswered(true);
            if(answerTime <= 0) {
                answerTime = defaultAnswerTime;
            }
            playersInQuestion.addAll(getOnlinePlayers());
            ((QuizGameScoreboard)getBoard()).startQuestion(answerTime);
            AskQuestionConversationFactory askQuestionFactory 
                    = new AskQuestionConversationFactory(MiniGamesPlugin.getPluginInstance(),answerTime);
            for (Player player : getOnlinePlayers()) {
                askQuestionFactory.start(player, this, question);
            }
        }
    }

    public void stopQuestion() {
        ((QuizGameScoreboard)getBoard()).stopQuestion();
        playersInQuestion.clear();
        if(!hasNextQuestion()) {
            if(!announceWinner(false)) {
                Player manager = Bukkit.getPlayer(getManager().getUniqueId());
                if(manager!=null) {
                    sendManagerWinnerInfo(manager);
                }
            }
        }
    }

    public boolean announceWinner(boolean allowEqual) {
        int maxScore = 0;
        List<Player> winner = new ArrayList<>();
        boolean equalMaxScore = true;
        for(Player player: getOnlinePlayers()) {
            int score = ((QuizGameScoreboard)getBoard()).getScore(player.getName());
            if(score>maxScore) {
                maxScore = score;
                winner.clear();
                winner.add(player);
                equalMaxScore = false;
            }
            else if(score == maxScore) {
                equalMaxScore = true;
                winner.add(player);
            }
        }
        if(winner.size()>0 && (allowEqual || winner.size()==1)) {
            for(Player player: winner) {
                TitleUtil.showTitle(player, "gold", "Congrats","You won the quiz game.");
                String winnerNames = winner.get(0).getName();
                for(int i=1;i<winner.size()-1;i++) {
                    winnerNames = winnerNames + ", "+winner.get(i).getName();
                }
                if(winner.size()>1) {
                    winnerNames = winnerNames + " and "+winner.get(winner.size()-1).getName();
                }
                TitleUtil.showTitleAll(getOnlinePlayers(), winner, "blue", "game over", winnerNames+" won the quiz.");
                Player manager = Bukkit.getPlayer(getManager().getUniqueId());
                if(manager!=null && !PluginData.isInGame(manager)) {
                    TitleUtil.showTitle(manager, "blue", "game over", winnerNames+" won the quiz.");
                }
            }
            return true;
        }
        return false;
    }
    

    public boolean isPlayerInQuestion() {
        return !playersInQuestion.isEmpty();
    }
    
    public void removePlayerFromQuestion(Player player) {
        Player found = null;
        for(Player search: playersInQuestion) {
            if(search.getUniqueId()==player.getUniqueId()) {
                found = search; break;
            }
        }
        if(found!=null) {
            playersInQuestion.remove(found);
        }
    }
    
    public void incrementScore(Player player) {
        ((QuizGameScoreboard)getBoard()).score(player.getName());
    }
    
    public void clearQuestions() {
        stopQuestion();
        questions.clear();
        ((QuizGameScoreboard)getBoard()).clearQuestions();
        resetQuestions();
    }
    
    public void saveQuestions(File file, String description) throws IOException {
        JSONArray jQuestionArray = new JSONArray();
        for (AbstractQuestion question : questions) {
            JSONObject jQuestion = new JSONObject();
            jQuestion.put("Question",question.getQuestion());
            jQuestion.put("Type", question.getType().getName());
            switch(question.getType()) {
                case FREE:
                    jQuestion.put("Answer", ((FreeQuestion)question).getAnswer());
                    break;
                case NUMBER:
                    jQuestion.put("Answer", ((NumberQuestion)question).getAnswer());
                    jQuestion.put("Precision", ((NumberQuestion)question).getPrecision());
                    break;
                case SINGLE:
                case MULTI:
                    JSONArray jChoices = new JSONArray();
                    jChoices.addAll(Arrays.asList(((ChoiceQuestion)question).getAnswers()));
                    jQuestion.put("Choices", jChoices);
                    jQuestion.put("Correct", ((ChoiceQuestion)question).getCorrectAnswers());
            }
            jQuestionArray.add(jQuestion);
        }
        JSONObject jFile = new JSONObject();
        jFile.put("questions", jQuestionArray);
        jFile.put("description", description);
        try(FileWriter fw = new FileWriter(file)) {
            jFile.writeJSONString(fw);
        }
    }
    
    public void loadQuestions(File file) throws FileNotFoundException, ParseException{
        try {
            String input;
            try (Scanner reader = new Scanner(file)) {
                input = "";
                while(reader.hasNext()){
                    input = input+reader.nextLine();
                }
            }
            JSONObject jInput = (JSONObject) new JSONParser().parse(input);
            JSONArray jQuestions = (JSONArray) jInput.get("questions");
            for (Object questionObject : jQuestions) {
                    JSONObject jQuestion = (JSONObject) questionObject;
                QuestionType type = QuestionType.getQuestionType((String) jQuestion.get("Type"));
                AbstractQuestion newQuestion;
                switch(type) {
                    case FREE:
                        newQuestion = new FreeQuestion((String) jQuestion.get("Question"),
                                                       (String) jQuestion.get("Answer"));
                        break;
                    case NUMBER:
                        newQuestion = new NumberQuestion((String) jQuestion.get("Question"),
                                                         ((Long) jQuestion.get("Answer")).intValue(),
                                                         ((Long) jQuestion.get("Precision")).intValue());
                        break;
                    case MULTI:
                        newQuestion = new ChoiceQuestion((String) jQuestion.get("Question"),
                                                        readStringArray(jQuestion,"Choices"),
                                                        (String) jQuestion.get("Correct"));
                        break;
                    case SINGLE:
                        newQuestion = new SingleChoiceQuestion((String) jQuestion.get("Question"),
                                                        readStringArray(jQuestion,"Choices"),
                                                        (String) jQuestion.get("Correct"));
                        break;
                    default:
                        throw new ParseException(ParseException.ERROR_UNEXPECTED_TOKEN);
                }
                addQuestion(newQuestion,-1);
            }
        } catch (FileNotFoundException | ParseException ex) {
            MiniGamesPlugin.getPluginInstance().getLogger().log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    private String[] readStringArray(JSONObject jQuestion, String key) {
        JSONArray jAnswers = (JSONArray) jQuestion.get(key);
        List<String> answers= new ArrayList<>();
        for(Object answerObject : jAnswers) {
            answers.add((String) answerObject);
        }
        return answers.toArray(new String[0]);
    }

    private void sendManagerWinnerInfo(Player manager) {
        MessageUtil.sendInfoMessage(manager, "There is no single winner. You can add more questions or announce multiple winners with /game winner");

    }

}
