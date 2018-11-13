/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.Permissions;
import com.mcmiddleearth.minigames.data.PluginData;
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
        addCommandHandler("acceptquestions", new QuizGameQuestionsAccept(Permissions.STAFF));
        addCommandHandler("allow", new GameAllow(Permissions.MANAGER));
        addCommandHandler("ban", new GameBan(Permissions.MANAGER));
        addCommandHandler("check", new GameCheck(Permissions.USER));
        addCommandHandler("clear", new QuizGameClear(Permissions.MANAGER));
        addCommandHandler("create", new GameCreate(Permissions.MANAGER));
        addCommandHandler("delete", new GameDelete(Permissions.STAFF));
        addCommandHandler("deny", new GameDeny(Permissions.MANAGER));
        addCommandHandler("end", new GameEnd(Permissions.MANAGER));
        addCommandHandler("files", new GameFiles(Permissions.MANAGER));
        addCommandHandler("help", new GameHelp(Permissions.USER));
        addCommandHandler("hide", new HaSGameHide(Permissions.MANAGER));
        addCommandHandler("info", new GameInfo(Permissions.USER));
        addCommandHandler("invite", new GameInvite(Permissions.MANAGER));
        addCommandHandler("join", new GameJoin(Permissions.USER));
        addCommandHandler("kick", new GameKick(Permissions.MANAGER));
        addCommandHandler("leave", new GameLeave(Permissions.USER));
        addCommandHandler("loadquiz", new QuizGameLoad(Permissions.MANAGER));
        addCommandHandler("loadrace", new RaceGameLoad(Permissions.MANAGER));
        addCommandHandler("loadquestions", new QuizGameQuestionsLoad(Permissions.MANAGER));
        addCommandHandler("manager", new GameManager(Permissions.MANAGER));
        addCommandHandler("marker", new RaceGameMarker(Permissions.MANAGER));
        addCommandHandler("question", new QuizGameQuestion(Permissions.USER));
        addCommandHandler("random", new QuizGameRandom(Permissions.MANAGER));
        addCommandHandler("ready", new GameReady(Permissions.MANAGER));
        addCommandHandler("remove", new RaceGameRemove(Permissions.MANAGER));
        addCommandHandler("restart", new QuizGameRestart(Permissions.MANAGER));
        addCommandHandler("reviewquestions", new QuizGameQuestionsReview(Permissions.STAFF));
        addCommandHandler("savemarker", new RaceGameSaveMarker(Permissions.MANAGER));
        addCommandHandler("savequiz", new QuizGameSave(Permissions.MANAGER));
        addCommandHandler("saverace", new RaceGameSave(Permissions.MANAGER));
        addCommandHandler("seeker", new HaSGameSeeker(Permissions.MANAGER));
        addCommandHandler("send", new QuizGameSend(Permissions.MANAGER));
        addCommandHandler("raceset", new RaceGameSet(Permissions.MANAGER));
        addCommandHandler("show", new RaceGameShow(Permissions.MANAGER));
        addCommandHandler("showcategories", new QuizGameShowCategories(Permissions.USER));
        addCommandHandler("spectate", new GameSpectate(Permissions.USER));
        addCommandHandler("stat", new QuizGameStatus(Permissions.MANAGER));
        addCommandHandler("start", new GameStart(Permissions.MANAGER));
        addCommandHandler("stop", new RaceGameStop(Permissions.MANAGER));
        addCommandHandler("submitquestion", new QuizGameQuestionsSubmit(Permissions.USER));
        addCommandHandler("unban", new GameUnban(Permissions.MANAGER));
        addCommandHandler("warp", new GameWarp(Permissions.USER));
        addCommandHandler("winner", new QuizGameWinner(Permissions.MANAGER));
        addCommandHandler("golfset", new GolfGameSet(Permissions.MANAGER));
        addCommandHandler("savegolf", new GolfGameSave(Permissions.MANAGER));
        addCommandHandler("loadgolf", new GolfGameLoad(Permissions.MANAGER));
        addCommandHandler("pvpset", new PvPGameSet(Permissions.MANAGER));
        addCommandHandler("field", new PvPGameField(Permissions.MANAGER));
        addCommandHandler("savepvp", new PvPGameSave(Permissions.MANAGER));
        addCommandHandler("loadpvp", new PvPGameLoad(Permissions.MANAGER));
        addCommandHandler("respawn", new PvPGameRespawn(Permissions.MANAGER));
        addCommandHandler("loadout", new PvPGameLoadout(Permissions.MANAGER));
        addCommandHandler("saveloadout", new PvPGameSaveLoadout(Permissions.MANAGER));
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
        PluginData.getMessageUtil().sendErrorMessage(cs, "You're missing subcommand name for this command.");
    }
    
    private void sendSubcommandNotFoundErrorMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Subcommand not found.");
    }
    
    private void addCommandHandler(String name, AbstractCommand handler) {
        commands.put(name, handler);
    }
}
