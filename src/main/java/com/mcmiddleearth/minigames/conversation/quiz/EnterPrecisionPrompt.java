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
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;

/**
 *
 * @author Eriol_Eandur
 */
class EnterPrecisionPrompt extends NumericPrompt {

    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, Number number) {
        cc.setSessionData("precision", number.intValue()+"");
        if((boolean) cc.getSessionData("createQuestion")) {
            return new ShowCategoryPrompt();
        } else {
            return END_OF_CONVERSATION;
        }
    }

    @Override
    public String getPromptText(ConversationContext cc) {
        return PluginData.getMessageUtil().getPREFIX()+"Enter the tolerance for an answer to be called correct.";
    }
    
    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return "Invalid input. Enter a whole number.";
    }
    
    @Override
    protected boolean isInputValid(ConversationContext cc, String input) {
        try {
            Integer.parseInt(input);
        }
        catch(NumberFormatException e) {
            return false;
        }
        return true;
    }


}
