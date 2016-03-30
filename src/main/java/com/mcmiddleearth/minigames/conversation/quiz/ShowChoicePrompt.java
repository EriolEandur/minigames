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
import com.mcmiddleearth.minigames.quizQuestion.SingleChoiceQuestion;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;

/**
 *
 * @author Eriol_Eandur
 */
class ShowChoicePrompt extends MessagePrompt {

    @Override
    protected Prompt getNextPrompt(ConversationContext cc) {
        ChoiceQuestion question = (ChoiceQuestion)cc.getSessionData("question");
        if((Integer) cc.getSessionData("ChoiceIndex")<((String[]) cc.getSessionData("Choices")).length) {
            return new ShowChoicePrompt();
        }
        else if(question instanceof SingleChoiceQuestion) {
            return new EnterSingleChoiceAnswersPrompt();
        }
        else {
            return new EnterMultipleChoiceAnswersPrompt();
        }
    }

    @Override
    public String getPromptText(ConversationContext cc) {
        ChoiceQuestion question = ((ChoiceQuestion)cc.getSessionData("question"));
        int choiceIndex = (Integer) cc.getSessionData("ChoiceIndex");
        cc.setSessionData("ChoiceIndex", choiceIndex+1);
        return ChatColor.GOLD+"["+ChoiceQuestion.getAnswerCharacter(choiceIndex)+"] "+
               ChatColor.AQUA+((String[])cc.getSessionData("Choices"))[choiceIndex].substring(1);
    }

}
