/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import org.bukkit.command.CommandSender;

/**
 *
 * @author Eriol_Eandur
 */
public class QuizGameSend extends AbstractCommand{
    
    public QuizGameSend(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": ");
        setUsageDescription(": ");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
    }
    
 }
