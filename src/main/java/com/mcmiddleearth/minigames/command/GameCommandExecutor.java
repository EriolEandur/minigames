/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.utils.MessageUtil;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Eriol_Eandur
 */
public class GameCommandExecutor implements CommandExecutor {

    @Getter
    private final Map <String, AbstractCommand> commands = new LinkedHashMap <>();
    
    public GameCommandExecutor() {
        addCommandHandler("create", new GameCreate("minigames.manager"));
        addCommandHandler("end", new GameEnd("minigames.manager"));
        addCommandHandler("leave", new GameLeave("minigames.user"));
        addCommandHandler("join", new GameJoin("minigames.user"));
        addCommandHandler("check", new GameCheck("minigames.user"));
        addCommandHandler("info", new GameInfo("minigames.user"));
        addCommandHandler("help", new GameHelp("minigames.user"));
        addCommandHandler("kick", new GameKick("minigames.manager"));
        addCommandHandler("ban", new GameBan("minigames.manager"));
        addCommandHandler("unban", new GameUnban("minigames.manager"));
        addCommandHandler("manager", new GameManager("minigames.manager"));
        addCommandHandler("start", new HaSGameStart("minigames.manager"));
        addCommandHandler("seeker", new HaSGameSeeker("minigames.manager"));
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if(!string.equalsIgnoreCase("game")) {
            return false;
        }
        if(strings == null || strings.length == 0) {
            sendNoSubcommandErrorMessage(cs);
            return true;
        }
        if(commands.containsKey(strings[0].toLowerCase())) {
            commands.get(strings[0].toLowerCase()).handle(cs, Arrays.copyOfRange(strings, 1, strings.length));
        } else {
            sendSubcommandNotFoundErrorMessage(cs);
        }
        return true;
    }
    
    private void sendNoSubcommandErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "You're missing subcommand name for this command.");
    }
    
    private void sendSubcommandNotFoundErrorMessage(CommandSender cs) {
        MessageUtil.sendErrorMessage(cs, "Subcommand not found.");
    }
    
    private void addCommandHandler(String name, AbstractCommand handler) {
        commands.put(name, handler);
    }
}
