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

import com.mcmiddleearth.minigames.quizQuestion.ChoiceQuestion;
import com.mcmiddleearth.minigames.quizQuestion.QuestionType;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

/**
 *
 * @author Eriol_Eandur
 */
class EditChoicesPrompt extends StringPrompt {

    @Override
    public String getPromptText(ConversationContext cc) {
        int answerIndex = (int) cc.getSessionData("answerIndex");
        return ChatColor.DARK_GREEN+"[Choice "
               +ChoiceQuestion.getAnswerCharacter(answerIndex) + "] "
               +((ChoiceQuestion)EditQuestionConversationFactory.getQuestion(cc)).getAnswers()[answerIndex];
    }

    @Override
    public Prompt acceptInput(ConversationContext cc, String string) {
        int answerIndex = (int) cc.getSessionData("answerIndex");
        if(string.equalsIgnoreCase("!keep")) {
            ((String[])cc.getSessionData("choices"))[answerIndex] 
                    = ((ChoiceQuestion)EditQuestionConversationFactory.getQuestion(cc)).getAnswers()[answerIndex];
        } else {
            ((String[])cc.getSessionData("choices"))[answerIndex] = string; 
        }
        answerIndex++;
        cc.setSessionData("answerIndex", answerIndex);
        if(answerIndex<ChoiceQuestion.answerCount) {
            return new EditChoicesPrompt();
        } else {
            switch(((QuestionType)cc.getSessionData("questionType"))) {
                case MULTI:
                    return new EditMultipleChoiceAnswersPrompt();
                default:
                    return new EditSingleChoiceAnswersPrompt();
            }
        }
    }

}
