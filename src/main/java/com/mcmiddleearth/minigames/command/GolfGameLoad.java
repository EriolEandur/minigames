package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.GolfGame;
import com.mcmiddleearth.minigames.game.RaceGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Planetology
 * @since 10/8/2018
 */
public class GolfGameLoad extends AbstractGameCommand{

    public GolfGameLoad(String... permissionNodes) {
        super(0, true, permissionNodes);
        cmdGroup = CmdGroup.GOLF;
        setShortDescription(": Loads a golf course from data file.");
        setUsageDescription(" <filename>: Loads golf course locations from the file <filename>.");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game) && isCorrectGameType((Player) cs, game, GameType.GOLF)) {
            if(args.length==0) {
                MiniGamesPlugin.getPluginInstance().getCommand("game").getExecutor().onCommand(cs, null, "game", new String[]{"files","course"});
                return;
            }

            GolfGame golfGame = (GolfGame) game;
            File file = new File(PluginData.getGolfDir(), args[0] + ".json");

            try {
                golfGame.getLocationManager().loadCourse(file);
                sendCourseLoadedMessage(cs);
            } catch (FileNotFoundException ex) {
                sendFileNotFoundMessage(cs);
            } catch (ParseException ex) {
                sendInvalidFileMessage(cs);
            }
        }
    }

    private void sendCourseLoadedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Golf course loaded from file.");
    }

    private void sendFileNotFoundMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "File not found.");
    }

    private void sendInvalidFileMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "The file contains invalid data.");
    }
}
