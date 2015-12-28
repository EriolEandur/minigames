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
package com.mcmiddleearth.minigames.conversation;

import com.mcmiddleearth.minigames.game.QuizGame;
import com.mcmiddleearth.minigames.quizQuestion.ChoiceQuestion;
import com.mcmiddleearth.minigames.quizQuestion.FreeQuestion;
import com.mcmiddleearth.minigames.quizQuestion.NumberQuestion;
import com.mcmiddleearth.minigames.quizQuestion.QuestionType;
import com.mcmiddleearth.minigames.quizQuestion.SingleChoiceQuestion;
import com.mcmiddleearth.minigames.utils.MessageUtil;
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
public class CreateQuestionConversationFactory implements ConversationAbandonedListener{
    private final ConversationFactory factory;
    
    public CreateQuestionConversationFactory(Plugin plugin){
        factory = new ConversationFactory(plugin)
                .withModality(false)
                .withPrefix(new CreateQuestionPrefix())
                .withFirstPrompt(new EnterQuestionPrompt())
                .withEscapeSequence("!cancel")
                .withTimeout(600)
                .addConversationAbandonedListener(this);
    }
    
    public void start(Player player, QuizGame game, QuestionType questionType) {
        Conversation conversation = factory.buildConversation(player);
        ConversationContext context = conversation.getContext();
        context.setSessionData("game", game);
        context.setSessionData("player", player);
        context.setSessionData("questionType", questionType);
        context.setSessionData("createQuestion", true);
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
            QuizGame game = (QuizGame)cc.getSessionData("game");
            switch(((QuestionType)cc.getSessionData("questionType"))) {
                case FREE:
                    game.addQuestion(new FreeQuestion((String) cc.getSessionData("question"),
                                                      (String) cc.getSessionData("answer"))); break;
                case NUMBER:
                    game.addQuestion(new NumberQuestion((String) cc.getSessionData("question"),
                                                        Integer.parseInt((String) cc.getSessionData("answer")),
                                                        Integer.parseInt((String) cc.getSessionData("precision")))); break;
                case MULTI:
                    game.addQuestion(new ChoiceQuestion((String) cc.getSessionData("question"),
                                                        (String[]) cc.getSessionData("choices"),
                                                        (String) cc.getSessionData("answer"))); break;
                default:
                    game.addQuestion(new SingleChoiceQuestion((String) cc.getSessionData("question"),
                                                              (String[]) cc.getSessionData("choices"),
                                                              (String) cc.getSessionData("answer"))); break;
            }
        }
    }
 
    private void sendAbordMessage(Player player) {
        MessageUtil.sendInfoMessage(player, "You cancelled creating a new Question.");
    }
    
    private void sendQuestionCreatedMessage(Player player) {
        MessageUtil.sendInfoMessage(player, "You added a new question to the quiz.");
    }
}
