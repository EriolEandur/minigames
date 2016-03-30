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
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;

/**
 *
 * @author Eriol_Eandur
 */
class ShowCategoryPrompt extends MessagePrompt {

    @Override
    protected Prompt getNextPrompt(ConversationContext cc) {
        if((Integer) cc.getSessionData("categoriesIndex")<PluginData.getQuestionCategories().size()) {
            return new ShowCategoryPrompt();
        } else {
            return new EnterQuestionCategoriesPrompt();
        }
    }

    @Override
    public String getPromptText(ConversationContext cc) {
        Integer index = (Integer) cc.getSessionData("categoriesIndex");
        if(index==null) {
            cc.setSessionData("categoriesIndex", 0);
            return ChatColor.DARK_GREEN+"Categories for quiz questions:";
        } else {
            index++;
            cc.setSessionData("categoriesIndex",index);
            return PluginData.getQuestionCategories().get(index-1);
        }
    }

}
