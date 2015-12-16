/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.game;

/**
 *
 * @author Eriol_Eandur
 */
public enum GameType{
    HIDE_AND_SEEK   ("HideAndSeek"),
    RACE            ("Race"),
    LORE_QUIZ       ("LoreQuiz");
    
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
    
}
