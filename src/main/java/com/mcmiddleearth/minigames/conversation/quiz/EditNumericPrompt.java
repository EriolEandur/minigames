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

import com.mcmiddleearth.minigames.quizQuestion.NumberQuestion;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;

/**
 *
 * @author Eriol_Eandur
 */
class EditNumericPrompt extends NumericPrompt {

    private boolean keep = false; 
    
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, Number number) {
        if(keep) {
            cc.setSessionData("answer", ((NumberQuestion)EditQuestionConversationFactory.getQuestion(cc)).getAnswer());
        } else {
            cc.setSessionData("answer", number.intValue());
        }
        return new EditPrecisionPrompt();
    }

    @Override
    public String getPromptText(ConversationContext cc) {
        cc.setSessionData("input", true);
        return ChatColor.DARK_GREEN+"[Numeric Answer] "+((NumberQuestion)EditQuestionConversationFactory
                                         .getQuestion(cc)).getCorrectAnswer();
    }
    
    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return ChatColor.RED+"[Invalid input] Type in chat a whole number.";
    }
    
    @Override
    protected boolean isInputValid(ConversationContext cc, String input) {
        if(input.equalsIgnoreCase("!keep")) {
            keep = true;
            return true;
        }
        try {
            Integer.parseInt(input);
        }
        catch(NumberFormatException e) {
            return false;
        }
        return true;
    }


}
