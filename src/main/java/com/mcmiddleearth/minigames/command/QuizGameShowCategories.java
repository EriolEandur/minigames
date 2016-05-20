/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.pluginutils.NumericUtil;
import com.mcmiddleearth.pluginutils.message.FancyMessage;
import com.mcmiddleearth.pluginutils.message.MessageType;
import java.util.ArrayList;
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
        cmdGroup = CmdGroup.LORE_QUIZ;
        setShortDescription(": Shows all available question categories.");
        setUsageDescription(": Shows all available question categories.");
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        List<FancyMessage> categories = new ArrayList<>();
        for(String line: PluginData.getQuestionCategories()) {
            categories.add(new FancyMessage(MessageType.WHITE,PluginData.getMessageUtil())
                                        .addSimple(""+PluginData.getMessageUtil().STRESSED+line.charAt(0)
                                            +ChatColor.WHITE+line.substring(1)));
        }
        FancyMessage header = new FancyMessage(MessageType.INFO,PluginData.getMessageUtil())
                                            .addSimple(""+ChatColor.DARK_GREEN+"Available categories for quiz questions. ");
        int page = 1;
        if(args.length>0 && NumericUtil.isInt(args[0])) {
            page=NumericUtil.getInt(args[0]);
        }
        PluginData.getMessageUtil().sendFancyListMessage((Player)cs, header, categories, "/game showcategories", page);
    }
}
