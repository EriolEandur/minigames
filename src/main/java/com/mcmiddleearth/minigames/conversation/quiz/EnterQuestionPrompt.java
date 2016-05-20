/*
 * Copyright (C) 2015 Eriol_Eandur
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

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.quizQuestion.ChoiceQuestion;
import com.mcmiddleearth.minigames.quizQuestion.QuestionType;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

/**
 *
 * @author Eriol_Eandur
 */
class EnterQuestionPrompt extends StringPrompt {

    public EnterQuestionPrompt() {
    }

    @Override
    public String getPromptText(ConversationContext cc) {
        return PluginData.getMessageUtil().getPREFIX()+"Enter the question.";
    }

    @Override
    public Prompt acceptInput(ConversationContext cc, String string) {
        cc.setSessionData("question", string);
        switch((QuestionType)cc.getSessionData("questionType")) {
            case FREE: 
                return new EnterFreePrompt();
            case NUMBER:
                return new EnterNumericPrompt();
            default: 
                cc.setSessionData("choices", new String[ChoiceQuestion.answerCount]);
                cc.setSessionData("answerIndex", 0);
                return new EnterChoicesPrompt();
        }
    }

    
}
