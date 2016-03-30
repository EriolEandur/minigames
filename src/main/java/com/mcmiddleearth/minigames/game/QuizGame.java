/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.game;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.conversation.quiz.AskQuestionConversationFactory;
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
import com.mcmiddleearth.minigames.utils.NumericUtil;
import com.mcmiddleearth.minigames.utils.StringUtil;
import com.mcmiddleearth.minigames.utils.TitleUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;
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

    private int answerTime = 30; //seconds
    
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
            return ChatColor.DARK_AQUA + "<Host ";
        }
        else {
            return super.getGameChatTag(player);
        }
    }
    
    public int countQuestions() {
        return questions.size();
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
        }
        else {
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
    
    public void setAnswerTime(int answerTime) {
        if(answerTime > 0) {
            this.answerTime = answerTime;
        }
    }
    
    public void sendQuestion() {
        if(hasNextQuestion()) {
            AbstractQuestion question = getNextQuestion();
            nextQuestion++;
            question.setAnswered(true);
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
        if(isPlayerInQuestion()) {
            stopQuestion();
        }
        questions.clear();
        ((QuizGameScoreboard)getBoard()).clearQuestions();
        resetQuestions();
    }
    
    public void saveQuestionsToJson(File file, String description) throws IOException {
        saveQuestionsToJson(file, description, questions);
    }
    
    public static void saveQuestionsToJson(File file, String description, List<AbstractQuestion> questions) 
                                     throws IOException {
        JSONArray jQuestionArray = new JSONArray();
        for (AbstractQuestion question : questions) {
            JSONObject jQuestion = new JSONObject();
            jQuestion.put("Question",question.getQuestion());
            jQuestion.put("Type", question.getType().getName());
            jQuestion.put("Categories", question.getCategories());
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
                    jQuestion.put("Correct", ((ChoiceQuestion)question).getCorrectAnswer());
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
    
    public void loadQuestionsFromJson(File file) throws FileNotFoundException, ParseException{
        List<AbstractQuestion> newQuestions = new ArrayList<>();
        loadQuestionsFromJson(file, newQuestions);
        for(AbstractQuestion question : newQuestions) {
            addQuestion(question, -1);
        }
    }
    
    public static void loadQuestionsFromJson(File file, List<AbstractQuestion> questions) 
                                        throws FileNotFoundException, ParseException {
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
                                                       (String) jQuestion.get("Answer"),
                                                       (String) jQuestion.get("Categories"));
                        break;
                    case NUMBER:
                        newQuestion = new NumberQuestion((String) jQuestion.get("Question"),
                                                         ((Long) jQuestion.get("Answer")).intValue(),
                                                         ((Long) jQuestion.get("Precision")).intValue(),
                                                         (String) jQuestion.get("Categories"));
                        break;
                    case MULTI:
                        newQuestion = new ChoiceQuestion((String) jQuestion.get("Question"),
                                                          readStringArray(jQuestion,"Choices"),
                                                        (String) jQuestion.get("Correct"),
                                                        (String) jQuestion.get("Categories"));
                        break;
                    case SINGLE:
                        newQuestion = new SingleChoiceQuestion((String) jQuestion.get("Question"),
                                                         readStringArray(jQuestion,"Choices"),
                                                        (String) jQuestion.get("Correct"),
                                                        (String) jQuestion.get("Categories"));
                        break;
                    default:
                        throw new ParseException(ParseException.ERROR_UNEXPECTED_TOKEN);
                }
                questions.add(newQuestion);
            }
        } catch (FileNotFoundException | ParseException ex) {
            //MiniGamesPlugin.getPluginInstance().getLogger().log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    private static String[] readStringArray(JSONObject jQuestion, String key) {
        JSONArray jAnswers = (JSONArray) jQuestion.get(key);
        List<String> answers= new ArrayList<>();
        for(Object answerObject : jAnswers) {
            answers.add((String) answerObject);
        }
        return answers.toArray(new String[0]);
    }
    
    public int[] loadQuestionsFromDataFile(File file, List<Integer> questionIds) throws FileNotFoundException {
        Collections.sort(questionIds);
        int found = 0;
        try {
            int line = 1;
            try (Scanner reader = new Scanner(file)) {
                for(Integer questionId: questionIds) {
                    try {
                        while(line<questionId && reader.hasNext()) {
                            reader.nextLine();
                            line++;
                        }
                        if(reader.hasNext()) {
                            StringTokenizer tokenizer = new StringTokenizer(reader.nextLine(),";");
                            String questionCategories = tokenizer.nextToken();
                            AbstractQuestion question = questionFromString(tokenizer, questionCategories);
                            question.setId(line);
                            addQuestion(question,-1);
                            found++;
                            line++;
                        }
                    } catch (ParseException | NoSuchElementException ex) {
                        Logger.getLogger(QuizGame.class.getName()).log(Level.SEVERE, 
                                         "Error reading questions from data file in line "+line
                                        +". Question skipped. ", ex);
                        line++;
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            MiniGamesPlugin.getPluginInstance().getLogger().log(Level.SEVERE, null, ex);
            throw ex;
        }
        return new int[]{found,found};

    }
    
    public int[] loadQuestionsFromDataFile(File file, String quizCategories, 
                                          boolean matchAllCategories, int maxNumber) throws FileNotFoundException {
        List<AbstractQuestion> newQuestions = new ArrayList<>();
        boolean loadAll = quizCategories.equalsIgnoreCase("all");
        int found=0;
        try {
            int line = 0;
            try (Scanner reader = new Scanner(file)) {
                while(reader.hasNext()){
                    try {
                        line++;
                        StringTokenizer tokenizer = new StringTokenizer(reader.nextLine(),";");
                        String questionCategories = tokenizer.nextToken();
                        if(loadAll || isQuestionInQuizCategories(quizCategories, questionCategories, matchAllCategories)) {
                            AbstractQuestion newQuestion = questionFromString(tokenizer, questionCategories);
                            newQuestion.setId(line);
                            newQuestions.add(newQuestion);
                        }
                    } catch (ParseException | NoSuchElementException ex) {
                        Logger.getLogger(QuizGame.class.getName()).log(Level.SEVERE, 
                                         "Error reading questions from data file in line "+line
                                        +". Question skipped. ", ex);
                    }
                }
                found=newQuestions.size();
                while(newQuestions.size()>maxNumber) {
                    //int random = new Double(Math.floor(Math.random()*newQuestions.size())).intValue();
                    int random = NumericUtil.getRandom(0, newQuestions.size()-1);
                    if(random >= newQuestions.size()) {
                        random = newQuestions.size()-1;
                    }
                    newQuestions.remove(random);
                }
                for(AbstractQuestion question: newQuestions) {
                    addQuestion(question,-1);
                }
            }
        } catch (FileNotFoundException ex) {
            MiniGamesPlugin.getPluginInstance().getLogger().log(Level.SEVERE, null, ex);
            throw ex;
        }
        return new int[]{found,newQuestions.size()};
    }
    
    private AbstractQuestion questionFromString(StringTokenizer tokenizer, String questionCategories) throws ParseException {
        QuestionType type = getQuestionType(StringUtil.parseInt(tokenizer.nextToken()));
        String question = tokenizer.nextToken();
        String[] choices = new String[]{"","","",""};
        if(type.equals(QuestionType.MULTI) || type.equals(QuestionType.SINGLE)) {
            for(int i = 0; i<4; i++) {
                choices[i] = tokenizer.nextToken();
            }
        }
        String answer = tokenizer.nextToken();
        AbstractQuestion newQuestion;
        switch(type) {
            case FREE:
                newQuestion = new FreeQuestion(question,answer,questionCategories);
                break;
            case NUMBER:
                int precision = StringUtil.parseInt(tokenizer.nextToken());
                int answerInt = StringUtil.parseInt(answer);
                newQuestion = new NumberQuestion(question,
                                                 answerInt,
                                                 precision,questionCategories);
                break;
            case MULTI:
                newQuestion = new ChoiceQuestion(question,
                                                 choices,
                                                 answer,questionCategories);
                break;
            case SINGLE:
                newQuestion = new SingleChoiceQuestion(question,
                                                choices,
                                                answer,questionCategories);
                break;
            default:
                throw new ParseException(ParseException.ERROR_UNEXPECTED_TOKEN);
        }
        return newQuestion;
    }
    
    public void saveQuestionsToDataFile(File file) throws FileNotFoundException, IOException {
        for(AbstractQuestion question: questions) {
            if(question.getId()!=0) {
                storeQuestionsToDataFile(file);
                return;
            }
        }
        addQuestionsToDataFile(file);
    }
    
    private void addQuestionsToDataFile(File file) throws FileNotFoundException, IOException {
        try (FileWriter fw = new FileWriter(file, true); 
             PrintWriter writer = new PrintWriter(fw)) {
            for(AbstractQuestion question: questions) {
                writer.println(questionToString(question));
            }
        }
    }
    
    private void storeQuestionsToDataFile(File file) throws FileNotFoundException, IOException {
        List<AbstractQuestion> saveQuestions = new ArrayList<>();
        saveQuestions.addAll(questions);
        Comparator<AbstractQuestion> comp = new Comparator<AbstractQuestion>(){
            @Override
            public int compare(AbstractQuestion o1, AbstractQuestion o2) {
                if(o1.getId()==0) return 1;
                if(o2.getId()==0) return -1;
                if(o1.getId()==o2.getId()) return 0;
                return (o1.getId()<o2.getId()?-1:1);
            }
        };
        Collections.sort(saveQuestions, comp);
        File tmpFile = new File(file.toString()+".tmp");
        try (FileWriter fw = new FileWriter(tmpFile, true);
             PrintWriter writer = new PrintWriter(fw);
             Scanner reader = new Scanner(file)) {
            int line = 1;
            for(AbstractQuestion question: saveQuestions) {
                int id = question.getId();
                if(id==0) {
                    while(reader.hasNext()) {
                        writer.println(reader.nextLine());
                    }
                    writer.println(questionToString(question));
                } else {
                    while(line<id) {
                        writer.println(reader.nextLine());
                        line++;
                    }
                    reader.nextLine();
                    writer.println(questionToString(question));
                    line++;
                }
            }
            while(reader.hasNext()) {
                writer.println(reader.nextLine());
            }
        }
        file.delete();
        tmpFile.renameTo(file);
    }
    
    private String questionToString(AbstractQuestion question) {
        String line = question.getCategories()+";"+
                +getQuestionTypeNumber(question.getType())+";"
                +question.getQuestion()+";";
        switch(question.getType()) {
            case FREE:
                line = line+question.getCorrectAnswer();
                break;
            case NUMBER:
                line = line+question.getCorrectAnswer()+";"+((NumberQuestion)question).getPrecision();
                break;
            case SINGLE:
            case MULTI:
                for(String choice: ((ChoiceQuestion)question).getAnswers()) {
                    line = line+choice+";";
                }
                line = line+((ChoiceQuestion)question).getCorrectAnswer();
                break;
        }
        return line;
    }
    
    private boolean isQuestionInQuizCategories(String quizCategories, 
                                               String questionCategories,
                                               boolean matchAllCategories) {
        String wantedCategories= "";
        String excludedCategories="";
        boolean exclude = false;
        for(char character: quizCategories.toCharArray()) {
            if(character=='-') {
                exclude = true; 
            } else {
                if(exclude) {
                    excludedCategories+=character;
                } else {
                    wantedCategories+=character;
                }
                exclude = false;
            }
        }
        if(checkWantedCategories(wantedCategories, questionCategories, matchAllCategories)) {
            return checkExcludedCategories(excludedCategories, questionCategories);
        } else {
            return false;
        }
    }
    
    private boolean checkWantedCategories(String wantedQuizCategories,
                                     String questionCategories,
                                     boolean matchAllCategories) {
        for(char character: wantedQuizCategories.toCharArray()) {
            if(matchAllCategories) {
                if(questionCategories.indexOf(character)<0) {
                    return false;
                }
            } else {
                if(questionCategories.indexOf(character)>=0) {
                    return true;
                }
            }
        }
        return matchAllCategories;
    }
    
    private boolean checkExcludedCategories(String excludedCategories,
                                            String questionCategories) {
        for(char character: excludedCategories.toCharArray()) {
            if(questionCategories.indexOf(character)>=0) {
                return false;
            }
        }
        return true;
    }
    
    private QuestionType getQuestionType(int index) {
        switch(index) {
            case 1: return QuestionType.FREE;
            case 2: return QuestionType.NUMBER;
            case 3: return QuestionType.SINGLE;
            case 4: return QuestionType.MULTI;
        }
        return QuestionType.FREE;
    }
    
    private int getQuestionTypeNumber(QuestionType type) {
        switch(type) {
            case FREE: return 1;
            case NUMBER: return 2;
            case SINGLE: return 3;
            case MULTI: return 4;
            default: return 1;
        }
    }
    
    private void sendManagerWinnerInfo(Player manager) {
        MessageUtil.sendInfoMessage(manager, "There is no single winner. You can add more questions or announce multiple winners with /game winner");

    }

}
