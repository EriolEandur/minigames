/*
 * Copyright (C) 2016 MCME
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
package com.mcmiddleearth.minigames.command;

import lombok.Getter;

/**
 *
 * @author Eriol_Eandur
 */
public enum CmdGroup {
    
    ALL             (""),
    GENERAL         ("General"),
    HIDE_AND_SEEK   ("Hide"),
    RACE            ("Race"),
    LORE_QUIZ       ("Quiz"),
    GOLF            ("Golf"),
    PVP             ("PvP");

    @Getter
    private final String name;

    private CmdGroup(String name) {
        this.name = name;
    }
    
    public static CmdGroup getCmdGroup(String name) {
        for(CmdGroup type: CmdGroup.values()) {
            if(type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return CmdGroup.ALL;
    }
    
    public boolean isCommandInGroup(AbstractCommand command) {
        return (this.equals(command.getCmdGroup()) || this.equals(CmdGroup.ALL));
    }

    
}
