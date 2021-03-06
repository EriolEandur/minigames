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
package com.mcmiddleearth.minigames.quizQuestion;

import com.mcmiddleearth.minigames.data.PluginData;
import org.bukkit.ChatColor;

/**
 *
 * @author Eriol_Eandur
 */
public class SingleChoiceQuestion extends ChoiceQuestion{
    
    public SingleChoiceQuestion(String question, String[] answers, String correctAnswer,
                                String categories) {
        super(question, QuestionType.SINGLE, answers, correctAnswer, categories);
    }
    
    public boolean isCorrectAnswer(char answer) {
        return isCorrectAnswer(new Character[]{answer}); //invalid json in question list messages
    }
   
    @Override
    public String[] getDetails() {
        return new String[]{PluginData.getMessageUtil().HIGHLIGHT+"[Type]"+PluginData.getMessageUtil().HIGHLIGHT_STRESSED+" SINGLE choice question",
                            PluginData.getMessageUtil().HIGHLIGHT+"[Question] "+PluginData.getMessageUtil().HIGHLIGHT_STRESSED+getQuestion(),
            (correctAnswers[0]?PluginData.getMessageUtil().STRESSED:PluginData.getMessageUtil().HIGHLIGHT)+"[A] "+answers[0],
            (correctAnswers[1]?PluginData.getMessageUtil().STRESSED:PluginData.getMessageUtil().HIGHLIGHT)+"[B] "+answers[1],
            (correctAnswers[2]?PluginData.getMessageUtil().STRESSED:PluginData.getMessageUtil().HIGHLIGHT)+"[C] "+answers[2],
            (correctAnswers[3]?PluginData.getMessageUtil().STRESSED:PluginData.getMessageUtil().HIGHLIGHT)+"[D] "+answers[3]};
    }

}
