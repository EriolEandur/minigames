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
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Eriol_Eandur
 */
public class NumberQuestion extends AbstractQuestion{
    
    @Getter
    @Setter
    private int answer;
    
    @Getter
    @Setter
    private int precision;
    
    public NumberQuestion(String question, int answer, int precision, String categories) {
        super(question, QuestionType.NUMBER, categories);
        this.answer = answer;
        this.precision = precision;
    }

    public boolean isCorrectAnswer(int answer) {
        return this.answer-precision<=answer && this.answer+precision>=answer;
    }
    
    @Override
    public boolean isCorrectAnswer(String answer) {
        try {
            return isCorrectAnswer(Integer.parseInt(answer));
        }
        catch(NumberFormatException e) {
            return false;
        }
    }
    
    @Override
    public String getCorrectAnswer() {
        return answer+"";
    }    
    
    @Override
    public String[] getDetails() {
        return new String[]{PluginData.getMessageUtil().HIGHLIGHT+"[Type]"+PluginData.getMessageUtil().HIGHLIGHT_STRESSED+" NUMBER answer question",
                            PluginData.getMessageUtil().HIGHLIGHT+"[Question] "+PluginData.getMessageUtil().HIGHLIGHT_STRESSED+getQuestion(),
                            PluginData.getMessageUtil().STRESSED+"[Answer] "+answer,
                            PluginData.getMessageUtil().STRESSED+"[Precision] "+precision};
    }
}
