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

import com.mcmiddleearth.pluginutils.message.MessageUtil;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Eriol_Eandur
 */
public class FreeQuestion extends AbstractQuestion{
    
    @Getter
    @Setter
    private String answer;
    
    public FreeQuestion(String question, String answer, String categories){
        super(question, QuestionType.FREE, categories);
        this.answer = answer;
    }

    @Override
    public boolean isCorrectAnswer(String answer) {
        return answer.equalsIgnoreCase(this.answer);
    }
    
    @Override
    public String getCorrectAnswer() {
        return answer;
    }
    
    @Override
    public String[] getDetails() {
        return new String[]{MessageUtil.HIGHLIGHT+"[Type]"+MessageUtil.HIGHLIGHT_STRESSED+" FREE answer question",
                            MessageUtil.HIGHLIGHT+"[Question] "+MessageUtil.HIGHLIGHT_STRESSED+getQuestion(),
                            MessageUtil.HIGHLIGHT+"[Answer] "+MessageUtil.HIGHLIGHT_STRESSED+answer};
    }
}
