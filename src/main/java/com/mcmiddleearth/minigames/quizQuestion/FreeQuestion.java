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
import org.bukkit.ChatColor;

/**
 *
 * @author Eriol_Eandur
 */
public class FreeQuestion extends AbstractQuestion{
    
    @Getter
    @Setter
    private String answer;
    
    private static char[][] charReplacement =  new char[][]{{'a','\u00E0','\u00E1','\u00E2','\u00E3','\u00E4','\u00E5'},
                                                              {'e','\u00E8','\u00E9','\u00EA','\u00EB','\u0113','\u011B'},
                                                              {'i','\u00EC','\u00ED','\u00EE','\u00EF','\u0129','\u012B'},
                                                              {'o','\u00F2','\u00F3','\u00F4','\u00F5','\u00F6','\u014D'},
                                                              {'u','\u00F9','\u00FA','\u00FB','\u00FC','\u0169','\u016B'},
                                                              {'n','\u00F1'}};

    public FreeQuestion(String question, String answer, String categories){
        super(question, QuestionType.FREE, categories);
        this.answer = answer;
    }

    @Override
    public boolean isCorrectAnswer(String str) {
        str = replaceSpecialChar(str);
        return str.equals(replaceSpecialChar(answer));
        //return answer.equalsIgnoreCase(this.answer);
    }
    
    private String replaceSpecialChar(String str) {
        str = str.toLowerCase();
        for (char[] letterReplace : charReplacement) {
            for (int j = 1; j < letterReplace.length; j++) {
                str = str.replace(letterReplace[j], letterReplace[0]);
            }
        }
        return str;
    }
    
    @Override
    public String getCorrectAnswer() {
        return answer;
    }
    
    @Override
    public String[] getDetails() {
        return new String[]{PluginData.getMessageUtil().HIGHLIGHT+"[Type]"+PluginData.getMessageUtil().HIGHLIGHT_STRESSED+" FREE answer question",
                            PluginData.getMessageUtil().HIGHLIGHT+"[Question] "+PluginData.getMessageUtil().HIGHLIGHT_STRESSED+getQuestion(),
                            PluginData.getMessageUtil().STRESSED+"[Answer] "+answer};
    }
}
