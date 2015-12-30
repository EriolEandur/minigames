/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.game;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Eriol_Eandur
 */
public enum GameType{
    HIDE_AND_SEEK   ("Hide"),
    RACE            ("Race"),
    LORE_QUIZ       ("Quiz");
    
    private final String name;

    private GameType(String name) {
        this.name = name;
    }
    
    public static GameType getGameType(String name) {
        for(GameType type: GameType.values()) {
            if(type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        switch(this) {
            case HIDE_AND_SEEK: return "Hide and Seek";
            case RACE: return "Race";
            case LORE_QUIZ: return "Lore Quiz";
        }
        return "Illegal type";
    }
    
    public Class associatedClass() {
        try {
            switch(this) {
                case HIDE_AND_SEEK: return Class.forName("com.mcmiddleearth.minigames.game.HideAndSeekGame");
                case RACE: return Class.forName("com.mcmiddleearth.minigames.game.RaceGame");
                case LORE_QUIZ: return Class.forName("com.mcmiddleearth.minigames.game.QuizGame");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GameType.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
