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
import com.mcmiddleearth.minigames.quizQuestion.NumberQuestion;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
class EditPrecisionPrompt extends NumericPrompt {

    private boolean keep = false;
    
    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, Number number) {
        if(keep) {
            PluginData.getMessageUtil().sendInfoMessage((Player)cc.getForWhom(), ChatColor.YELLOW+"Precision is kept.");
            cc.setSessionData("precision", ((NumberQuestion)EditQuestionConversationFactory.getQuestion(cc)).getPrecision());
        } else {
            PluginData.getMessageUtil().sendInfoMessage((Player)cc.getForWhom(), ChatColor.GREEN+"New precision is stored.");
            cc.setSessionData("precision", number.intValue());
        }
        return new EditQuestionCategoriesPrompt();
    }

    @Override
    public String getPromptText(ConversationContext cc) {
        return ChatColor.DARK_GREEN+"Tolerance for numeric answer: "+((NumberQuestion)EditQuestionConversationFactory
                                         .getQuestion(cc)).getPrecision();
    }
    
    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return "Invalid input. Enter a whole number.";
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
