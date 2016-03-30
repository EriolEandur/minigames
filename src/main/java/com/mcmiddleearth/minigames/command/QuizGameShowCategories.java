/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.utils.MessageUtil;
import com.mcmiddleearth.minigames.utils.NumericUtil;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Eriol_Eandur
 */
public class QuizGameShowCategories extends AbstractGameCommand{
    
    public QuizGameShowCategories(String... permissionNodes) {
        super(0, true, permissionNodes);
        setShortDescription(": Shows all available question categories.");
        setUsageDescription(" Shows all available question categories.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        List<LinkedHashMap<String,String>> categories = new ArrayList<>();
        for(String line: PluginData.getQuestionCategories()) {
            String output = ""+MessageUtil.STRESSED+line.charAt(0)
                           +ChatColor.WHITE+line.substring(1);
            LinkedHashMap<String,String> entry = new LinkedHashMap<>();
            entry.put(output, null);
            categories.add(entry);
        }
        LinkedHashMap<String,String> header = new LinkedHashMap<>();
        header.put(ChatColor.DARK_GREEN+"Available categories for quiz questions. ", null);
        int page = 1;
        if(args.length>0 && NumericUtil.isInt(args[0])) {
            page=NumericUtil.getInt(args[0]);
        }
        MessageUtil.sendClickableListMessage((Player)cs, header, categories, "/game showcategories", page);
    }
}
