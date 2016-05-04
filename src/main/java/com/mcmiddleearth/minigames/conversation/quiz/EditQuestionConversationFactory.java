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
import com.mcmiddleearth.minigames.quizQuestion.FreeQuestion;
import com.mcmiddleearth.minigames.quizQuestion.NumberQuestion;
import com.mcmiddleearth.minigames.quizQuestion.QuestionType;
import com.mcmiddleearth.pluginutils.message.MessageUtil;
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
public class EditQuestionConversationFactory implements ConversationAbandonedListener{
    private final ConversationFactory factory;
    
    public EditQuestionConversationFactory(Plugin plugin){
        factory = new ConversationFactory(plugin)
                .withModality(false)
                .withPrefix(new CreateQuestionPrefix())
                .withFirstPrompt(new EditQuestionPrompt())
                .withEscapeSequence("!cancel")
                .withTimeout(600)
                .addConversationAbandonedListener(this);
    }
    
    public void start(Player player, QuizGame game, int questionIndex) {
        Conversation conversation = factory.buildConversation(player);
        ConversationContext context = conversation.getContext();
        context.setSessionData("game", game);
        context.setSessionData("player", player);
        context.setSessionData("questionType", game.getQuestions().get(questionIndex).getType());
        context.setSessionData("createQuestion", true);
        context.setSessionData("questionIndex", questionIndex);
        conversation.begin();
    }
   
   
    @Override
    public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
        ConversationContext cc = abandonedEvent.getContext();
        if (!abandonedEvent.gracefulExit()){
            sendAbordMessage((Player) cc.getSessionData("player"));
        }
        else {
            sendQuestionCreatedMessage((Player) cc.getSessionData("player"));
            switch(((QuestionType)cc.getSessionData("questionType"))) {
                case FREE:
                    FreeQuestion question = (FreeQuestion) getQuestion(cc);
                    question.setQuestion((String) cc.getSessionData("question"));
                    question.setAnswer((String) cc.getSessionData("answer"));
                    question.setCategories((String) cc.getSessionData("categories"));
                    break;
                case NUMBER:
                    NumberQuestion nQuestion = (NumberQuestion) getQuestion(cc);
                    nQuestion.setQuestion((String) cc.getSessionData("question"));
                    nQuestion.setAnswer((Integer) cc.getSessionData("answer"));
                    nQuestion.setPrecision((Integer) cc.getSessionData("precision"));
                    nQuestion.setCategories((String) cc.getSessionData("categories"));
                    break;
                case MULTI:
                default:
                    ChoiceQuestion cQuestion = (ChoiceQuestion) getQuestion(cc);
                    cQuestion.setQuestion((String) cc.getSessionData("question"));
                    cQuestion.setCorrectAnswers((String) cc.getSessionData("answer"));
                    cQuestion.setAnswers((String[]) cc.getSessionData("choices"));
                    cQuestion.setCategories((String) cc.getSessionData("categories"));
                    break;
            }
        }
    }
 
    static AbstractQuestion getQuestion(ConversationContext cc) {
        return ((QuizGame)cc.getSessionData("game")).getQuestions()
                            .get((int) cc.getSessionData("questionIndex"));
    }
    
    private void sendAbordMessage(Player player) {
        MessageUtil.sendInfoMessage(player, "You cancelled editing.");
    }
    
    private void sendQuestionCreatedMessage(Player player) {
        MessageUtil.sendInfoMessage(player, "Changes saved.");
    }
}
