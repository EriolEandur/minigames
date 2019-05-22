package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.PvPGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Planetology
 */
public class PvPGameLoadout extends AbstractGameCommand{

    public PvPGameLoadout(String... permissionNodes) {
        super(0, true, permissionNodes);
        cmdGroup = CmdGroup.PVP;
        setShortDescription(": Sets a loadout for a pvp match.");
        setUsageDescription(" <filename>: Loads loadout from the file <filename>.");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game) && isCorrectGameType((Player) cs, game, GameType.PVP)) {
            if(args.length==0) {
                MiniGamesPlugin.getPluginInstance().getCommand("game").getExecutor().onCommand(cs, null, "game", new String[]{"files","course"});
                return;
            }

            PvPGame pvpGame = (PvPGame) game;
            File file = new File(PluginData.getLoadoutDirectory(), args[0] + ".json");

            try {
                pvpGame.getLoadoutManager().loadLoadout(file);
                sendLoadoutLoadedMessage(cs);
            } catch (FileNotFoundException ex) {
                sendFileNotFoundMessage(cs);
            } catch (ParseException ex) {
                sendInvalidFileMessage(cs);
            }
        }
    }

    private void sendLoadoutLoadedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Loadout for this pvp match loaded from file.");
    }

    private void sendFileNotFoundMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "File not found.");
    }

    private void sendInvalidFileMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "The file contains invalid data.");
    }
}
