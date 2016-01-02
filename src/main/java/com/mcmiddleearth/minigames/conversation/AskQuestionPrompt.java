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
import com.mcmiddleearth.minigames.quizQuestion.AbstractQuestion;
import com.mcmiddleearth.minigames.quizQuestion.ChoiceQuestion;
import com.mcmiddleearth.minigames.quizQuestion.FreeQuestion;
import com.mcmiddleearth.minigames.quizQuestion.NumberQuestion;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;

/**
 *
 * @author Eriol_Eandur
 */
public class AskQuestionPrompt extends MessagePrompt{

    @Override
    protected Prompt getNextPrompt(ConversationContext cc) {
        AbstractQuestion question = (AbstractQuestion) cc.getSessionData("question");
        if(question instanceof FreeQuestion) {
            return new EnterFreePrompt(); 
        } else if(question instanceof NumberQuestion) {
            return new EnterNumericPrompt(); 
        } else if(question instanceof ChoiceQuestion) {
            cc.setSessionData("ChoiceIndex", 0);
            if(((QuizGame) cc.getSessionData("game")).isRandomChoices()) {
                cc.setSessionData("Choices", ((ChoiceQuestion)question).getInRandomOrder());
            } 
            else {
                cc.setSessionData("Choices", ((ChoiceQuestion)question).getInProperOrder());
            }
            return new ShowChoicePrompt(); 
        }
        return null;
    }

    @Override
    public String getPromptText(ConversationContext cc) {
        return "[Question] "+((AbstractQuestion)cc.getSessionData("question")).getQuestion();
    }
    
}
