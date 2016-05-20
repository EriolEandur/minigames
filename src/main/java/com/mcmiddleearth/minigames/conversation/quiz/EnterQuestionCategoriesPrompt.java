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
import com.mcmiddleearth.minigames.utils.GameChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import static org.bukkit.conversations.Prompt.END_OF_CONVERSATION;
import org.bukkit.conversations.ValidatingPrompt;

/**
 *
 * @author Eriol_Eandur
 */
class EnterQuestionCategoriesPrompt extends ValidatingPrompt {

    @Override
    public String getPromptText(ConversationContext cc) {
        return ChatColor.DARK_GREEN+"Type in the letters of the categories this question will be in.";
    }

    @Override
    protected Prompt acceptValidatedInput(ConversationContext cc, String string) {
        cc.setSessionData("categories", string);
        return END_OF_CONVERSATION;
    }
    
    @Override
    protected String getFailedValidationText(ConversationContext context, String invalidInput) {
        return ChatColor.RED+"[Invalid input] Type in the letters of the question categories, for example: 'abet'";
    }
    
    @Override
    protected boolean isInputValid(ConversationContext cc, String string) {
       return PluginData.areValidCategories(string);
    }

    
}
