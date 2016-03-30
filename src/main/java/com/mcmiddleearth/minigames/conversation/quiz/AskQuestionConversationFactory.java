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
package com.mcmiddleearth.minigames.conversation.quiz;

import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.minigames.quizQuestion.AbstractQuestion;
import com.mcmiddleearth.minigames.quizQuestion.ChoiceQuestion;
import com.mcmiddleearth.minigames.quizQuestion.NumberQuestion;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import com.mcmiddleearth.minigames.utils.StringUtil;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Eriol_Eandur
 */
public class AskQuestionConversationFactory implements ConversationAbandonedListener{
    private final ConversationFactory factory;
    
    public AskQuestionConversationFactory(Plugin plugin, int answerTime){
        Map<Object,Object> initData = new HashMap<>();
        initData.put("input", false);
        factory = new ConversationFactory(plugin)
                .withModality(false)
                .withPrefix(new AskQuestionPrefix())
                .withFirstPrompt(new AskQuestionPrompt())
                .withTimeout(answerTime)
                .withInitialSessionData(initData)
                .addConversationAbandonedListener(this);
    }
    
    public void start(Player player, QuizGame game, AbstractQuestion question) {
        Conversation conversation = factory.buildConversation(player);
        ConversationContext context = conversation.getContext();
        context.setSessionData("game", game);
        context.setSessionData("player", player);
        context.setSessionData("question", question);
        context.setSessionData("createQuestion", false);
        conversation.begin();
    }
   
    @Override
    public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
        ConversationContext cc = abandonedEvent.getContext();
        Player player = (Player) cc.getSessionData("player");
        if (!abandonedEvent.gracefulExit()) {
            sendAbordMessage(player);
        }
        else {
            AbstractQuestion question = (AbstractQuestion)cc.getSessionData("question");
            String answer = (String) cc.getSessionData("answer");
            if(question.isCorrectAnswer(answer)) {
                ((QuizGame)cc.getSessionData("game")).incrementScore(player);
                if(question instanceof NumberQuestion 
                        && StringUtil.parseInt(answer)!=((NumberQuestion)question).getAnswer()) {
                    sendAlmostCorrectMessage(player, question.getCorrectAnswer());
                } else {
                    sendSuccessMessage(player);
                }
            }
            else {
                if(question instanceof NumberQuestion) {
                    sendFailNumberQuestionMessage((Player) cc.getSessionData("player"),
                                                  question.getCorrectAnswer(),
                                                  ((NumberQuestion)question).getPrecision());
                } else {
                    String correctAnswer;
                    if(question instanceof ChoiceQuestion) {
                        correctAnswer = question.getCorrectAnswer();
                        Character[] answerLetters = ChoiceQuestion.parseAnswer(correctAnswer);
                        correctAnswer = "";
                        for (Character answerLetter : answerLetters) {
                            String[] choices = (String[])cc.getSessionData("Choices");
                            for(int i = 0; i < choices.length;i++) {
                                if(choices[i].charAt(0)==answerLetter) {
                                    correctAnswer = correctAnswer+ChoiceQuestion.getAnswerCharacter(i);
                                    break;
                                }
                            }
                        }
                    } else {
                        correctAnswer = question.getCorrectAnswer();
                    }
                    sendFailMessage((Player) cc.getSessionData("player"), correctAnswer);  
                }
            }
        }
        QuizGame game = (QuizGame) cc.getSessionData("game");
        game.removePlayerFromQuestion(player);
        if(!game.isPlayerInQuestion()) {
            game.stopQuestion();
        }
    }

    private void sendAbordMessage(Player player) {
        MessageUtil.sendInfoMessage(player, "Time to answer expired.");
    }

    private void sendSuccessMessage(Player player) {
        MessageUtil.sendInfoMessage(player, "You answered this Question correctely.");
    }

    private void sendFailMessage(Player player, String answer) {
        MessageUtil.sendInfoMessage(player, "You failed to answer this Question correctely. Correct answer: "+answer);
    }

    private void sendFailNumberQuestionMessage(Player player, String answer, int precision) {
        MessageUtil.sendInfoMessage(player, "You failed to answer this Question correctely. Correct answer was "
                                           +answer+". Allowed deviation from correct answer was "+precision+".");
    }

    private void sendAlmostCorrectMessage(Player player, String answer) {
        MessageUtil.sendInfoMessage(player, "Almost! The right answer was "+answer+" but you were close enough.");
    }

}
