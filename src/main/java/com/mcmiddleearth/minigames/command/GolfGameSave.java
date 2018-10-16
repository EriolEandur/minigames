package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.conversation.confirmation.Confirmationable;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.GolfGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

/**
 * @author Planetology
 */
public class GolfGameSave extends AbstractGameCommand implements Confirmationable {

    private GolfGame golfGame;

    private File file;

    private String description;

    public GolfGameSave(String... permissionNodes) {
        super(2, true, permissionNodes);
        cmdGroup = CmdGroup.GOLF;
        setShortDescription(": Saves a golf course to file.");
        setUsageDescription(" <filename> <description>: Saves the golf course locations with the assigned marker names and a <description> to file <filename>.");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game)
                && isCorrectGameType((Player) cs, game, GameType.GOLF)) {
            golfGame = (GolfGame) game;
            file = new File(PluginData.getGolfDir(), args[0] + ".json");
            description = args[1];
            for(int i = 2; i < args.length;i++) {
                description = description + " "+ args[i];
            }
            if(file.exists()) {
                PluginData.getConfirmationFactory().start((Player) cs,
                        "A golf course file with that name already exists. Overwrite it?", this);
            }
            else {
                confirmed((Player) cs);
            }
        }
    }

    public void confirmed(Player player) {
        try {
            golfGame.getLocationManager().saveCourse(file, description);
            sendCourseSavedMessage(player);
        } catch (IOException ex) {
            sendIOErrorMessage(player, ex.getMessage());
        }
    }

    @Override
    public void cancelled(Player player) {
        sendAbordMessage(player);
    }

    private void sendIOErrorMessage(Player player, String msg) {
        PluginData.getMessageUtil().sendErrorMessage(player, "There was an error: "+msg+" Nothing was saved.");
    }

    private void sendCourseSavedMessage(Player player) {
        PluginData.getMessageUtil().sendInfoMessage(player, "Golf course was saved to disk.");
    }

    private void sendAbordMessage(Player player) {
        PluginData.getMessageUtil().sendInfoMessage(player, "Saving golf course cancelled.");
    }
}
