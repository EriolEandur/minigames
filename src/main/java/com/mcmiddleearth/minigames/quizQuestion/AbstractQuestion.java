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

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Eriol_Eandur
 */
public abstract class AbstractQuestion {
    
    @Getter
    @Setter
    private String question;
    
    @Getter
    private final QuestionType type;
    
    @Getter
    private String categories = "";
    
    @Getter
    @Setter
    private boolean answered = false;
    
    @Getter
    @Setter
    private int id = 0;
    
    public AbstractQuestion(String question, QuestionType type, String categories) {
        if(categories !=null) {
            this.categories = categories;
        }
        this.question = question;
        this.type = type;
    }
    
    public abstract boolean isCorrectAnswer(String answer);
    
    public abstract String getCorrectAnswer();
    
    public void setCategories(String categories) {
        if(categories!=null) {
            this.categories=categories;
        }
    }
    
    public abstract String[] getDetails();
}
