/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.Permissions;
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
        addCommandHandler("create", new GameCreate(Permissions.MANAGER));
        addCommandHandler("end", new GameEnd(Permissions.MANAGER));
        addCommandHandler("leave", new GameLeave(Permissions.USER));
        addCommandHandler("join", new GameJoin(Permissions.USER));
        addCommandHandler("check", new GameCheck(Permissions.USER));
        addCommandHandler("info", new GameInfo(Permissions.USER));
        addCommandHandler("help", new GameHelp(Permissions.USER));
        addCommandHandler("kick", new GameKick(Permissions.MANAGER));
        addCommandHandler("ban", new GameBan(Permissions.MANAGER));
        addCommandHandler("unban", new GameUnban(Permissions.MANAGER));
        addCommandHandler("manager", new GameManager(Permissions.MANAGER));
        addCommandHandler("hide", new HaSGameHide(Permissions.MANAGER));
        addCommandHandler("seeker", new HaSGameSeeker(Permissions.MANAGER));
        addCommandHandler("question", new QuizGameQuestion(Permissions.USER));
        addCommandHandler("send", new QuizGameSend(Permissions.MANAGER));
        addCommandHandler("ready", new GameReady(Permissions.MANAGER));
        addCommandHandler("restart", new QuizGameRestart(Permissions.MANAGER));
        addCommandHandler("savequiz", new QuizGameSave(Permissions.MANAGER));
        addCommandHandler("loadquiz", new QuizGameLoad(Permissions.MANAGER));
        addCommandHandler("delete", new GameDelete(Permissions.STAFF));
        addCommandHandler("clear", new QuizGameClear(Permissions.MANAGER));
        addCommandHandler("files", new GameFiles(Permissions.MANAGER));
        addCommandHandler("set", new RaceGameSet(Permissions.MANAGER));
        addCommandHandler("remove", new RaceGameRemove(Permissions.MANAGER));
        addCommandHandler("savemarker", new RaceGameSaveMarker(Permissions.MANAGER));
        addCommandHandler("marker", new RaceGameMarker(Permissions.MANAGER));
        addCommandHandler("saverace", new RaceGameSave(Permissions.MANAGER));
        addCommandHandler("loadrace", new RaceGameLoad(Permissions.MANAGER));
        addCommandHandler("start", new RaceGameStart(Permissions.MANAGER));
        addCommandHandler("stop", new RaceGameStop(Permissions.MANAGER));
        addCommandHandler("warp", new GameWarp(Permissions.USER));
        addCommandHandler("show", new RaceGameShow(Permissions.MANAGER));
        addCommandHandler("allow", new GameAllow(Permissions.MANAGER));
        addCommandHandler("deny", new GameDeny(Permissions.MANAGER));
        addCommandHandler("invite", new GameInvite(Permissions.MANAGER));
        addCommandHandler("spectate", new GameSpectate(Permissions.USER));
        addCommandHandler("winner", new QuizGameWinner(Permissions.MANAGER));
        addCommandHandler("random", new QuizGameRandom(Permissions.MANAGER));
        addCommandHandler("loadquestions", new QuizGameQuestionsLoad(Permissions.MANAGER));
        addCommandHandler("submitquestion", new QuizGameQuestionsSubmit(Permissions.USER));
        addCommandHandler("reviewquestions", new QuizGameQuestionsReview(Permissions.STAFF));
        addCommandHandler("acceptquestions", new QuizGameQuestionsAccept(Permissions.STAFF));
        addCommandHandler("showcategories", new QuizGameShowCategories(Permissions.USER));
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
