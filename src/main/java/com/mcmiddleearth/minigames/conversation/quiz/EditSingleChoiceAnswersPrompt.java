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

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.quizQuestion.ChoiceQuestion;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
class EditSingleChoiceAnswersPrompt extends EditMultipleChoiceAnswersPrompt {

    @Override
    public String getPromptText(ConversationContext cc) {
        return super.getPromptText(cc);
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String string) {
        if(string.equalsIgnoreCase("!keep")) {
            PluginData.getMessageUtil().sendInfoMessage((Player)cc.getForWhom(), ChatColor.YELLOW+"Correct choice is kept.");
            cc.setSessionData("answer", EditQuestionConversationFactory.getQuestion(cc).getCorrectAnswer());
        } else {
            cc.setSessionData("answer", string);
        }
        return new EditQuestionCategoriesPrompt();
    }
    
    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return ChatColor.RED+"[Invalid input] Type in the letter of the correct answer only.";
    }
    
    @Override
    protected boolean isInputValid(ConversationContext cc, String string) {
       string = string.trim();
       return string.equalsIgnoreCase("!keep") || (string.length()==1 && ChoiceQuestion.isLetterValid(string.charAt(0)));
    }

    
}
