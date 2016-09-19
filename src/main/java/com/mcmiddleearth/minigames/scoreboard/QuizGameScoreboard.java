/*
 * Copyright (C) 2015 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.minigames.scoreboard;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

/**
 *
 * @author Eriol_Eandur
 */
public class QuizGameScoreboard extends GameScoreboard{
    
    private final Objective quizObjective, timerObjective;
    
    private final Score answerTimeScore, unfinishedScore;
    
    private int questionCount = 0;
    
    private int currentQuestion = 0;
    
    private final List<String> players = new ArrayList<>();

    private BukkitRunnable timerTask;
    
    public QuizGameScoreboard() {
        super("Quiz");
        quizObjective = scoreboard.registerNewObjective("quiz", "dummy");
        timerObjective = scoreboard.registerNewObjective("timer", "dummy");
        setQuestionDisplay();
        timerObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        answerTimeScore = timerObjective.getScore(ChatColor.YELLOW+" time remaining: ");
        answerTimeScore.setScore(0);
        unfinishedScore = timerObjective.getScore(ChatColor.RED+" players thinking: ");
        unfinishedScore.setScore(0);
    }
    
    public void startQuestion(int time, int players) {
        answerTimeScore.setScore(time);
        unfinishedScore.setScore(players);
        currentQuestion++;
        setQuestionDisplay();
        if(timerTask!=null) {
            timerTask.cancel();
        }
        timerObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                answerTimeScore.setScore(answerTimeScore.getScore()-1);
                if(answerTimeScore.getScore()<1) {
                    quizObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    cancel();
                }
            }};
        timerTask.runTaskTimer(MiniGamesPlugin.getPluginInstance(), 20, 20);
    }
    
    public void stopQuestion() {
        if(timerTask!=null) {
            timerTask.cancel();
        }
        quizObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }
    
    public void addPlayer(String playerName) {
        if(players.isEmpty()) {
            quizObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        Score score = quizObjective.getScore(playerName);
        players.add(playerName);
        score.setScore(0);
    }
    
    public void score(String playerName) {
        Score score = quizObjective.getScore(playerName);
        score.setScore(score.getScore()+1);
    }
    
    public int getScore(String playerName) {
        return quizObjective.getScore(playerName).getScore();
    }
    
    public void addQuestion() {
        questionCount++;
        setQuestionDisplay();
    }
    
    public void removeQuestion() {
        questionCount--;
        if(currentQuestion>questionCount) {
            currentQuestion = questionCount;
        }
        setQuestionDisplay();
    }
    
    public void restart() {
        currentQuestion = 0;
        for(String name: players) {
            quizObjective.getScore(name).setScore(0);
        }
        setQuestionDisplay();
    }
    
    private void setQuestionDisplay() {
        quizObjective.setDisplayName("Question "+currentQuestion+" / " + questionCount);
        timerObjective.setDisplayName("Question "+currentQuestion+" / " + questionCount);
    }

    public void clearQuestions() {
        questionCount  = 0;
        restart();
    }

    public void playerFinished() {
        unfinishedScore.setScore(unfinishedScore.getScore()-1);
    }

}
