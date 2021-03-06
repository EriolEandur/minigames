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
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
class EditQuestionPrompt extends StringPrompt {

    public EditQuestionPrompt() {
    }

    @Override
    public String getPromptText(ConversationContext cc) {
        return ChatColor.DARK_GREEN+"[Question] "+EditQuestionConversationFactory.getQuestion(cc).getQuestion();
    }

    @Override
    public Prompt acceptInput(ConversationContext cc, String string) {
        if(string.equalsIgnoreCase("!keep")) {
            PluginData.getMessageUtil().sendInfoMessage((Player)cc.getForWhom(), ChatColor.YELLOW+"Question text is kept.");
            cc.setSessionData("question", EditQuestionConversationFactory.getQuestion(cc).getQuestion());
        } else {
            PluginData.getMessageUtil().sendInfoMessage((Player)cc.getForWhom(), ChatColor.GREEN+"New question text is stored.");
            cc.setSessionData("question", string);
        }
        switch((QuestionType)cc.getSessionData("questionType")) {
            case FREE: 
                return new EditFreePrompt();
            case NUMBER:
                return new EditNumericPrompt();
            default: 
                cc.setSessionData("choices", new String[ChoiceQuestion.answerCount]);
                cc.setSessionData("answerIndex", 0);
                return new EditChoicesPrompt();
        }
    }

    
}
