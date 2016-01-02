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

import com.mcmiddleearth.minigames.quizQuestion.ChoiceQuestion;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.conversations.ConversationContext;

/**
 *
 * @author Eriol_Eandur
 */
class EnterSingleChoicesPrompt extends EnterMultipleChoicesPrompt {

    @Override
    public String getPromptText(ConversationContext cc) {
        cc.setSessionData("input", true);
        return "[Hint] Type in chat the letter of the correct answer.";
    }

    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return ChatColor.RED+"[Invalid input] Type in the letter of the correct answer only.";
    }
    
    @Override
    protected boolean isInputValid(ConversationContext cc, String string) {
       string = string.trim();
       return string.length()==1 && ChoiceQuestion.isLetterValid(string.charAt(0));
    }

    
}
