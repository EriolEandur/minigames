package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.conversation.confirmation.Confirmationable;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.PvPGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

/**
 * @author Planetology
 */
public class PvPGameSave extends AbstractGameCommand implements Confirmationable {

    private PvPGame pvpGame;
    private File file;
    private String description;

    public PvPGameSave(String... permissionNodes) {
        super(2, true, permissionNodes);
        cmdGroup = CmdGroup.PVP;
        setShortDescription(": Saves a pvp match to file.");
        setUsageDescription(" <filename> <description>: Saves the pvp match locations with the assigned point names and a <description> to file <filename>.");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);

        if(game != null && isManager((Player) cs, game)
                && isCorrectGameType((Player) cs, game, GameType.PVP)) {
            pvpGame = (PvPGame) game;
            file = new File(PluginData.getPvpDirectory(), args[0] + ".json");
            description = args[1];

            for(int i = 2; i < args.length;i++) {
                description = description + " "+ args[i];
            }

            if(file.exists()) {
                PluginData.getConfirmationFactory().start((Player) cs,
                        "A pvp match file with that name already exists. Overwrite it?", this);
            } else {
                confirmed((Player) cs);
            }
        }
    }

    public void confirmed(Player player) {
        try {
            pvpGame.getLocationManager().saveArena(file, description);
            sendPvPSavedMessage(player);
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

    private void sendPvPSavedMessage(Player player) {
        PluginData.getMessageUtil().sendInfoMessage(player, "PvP match was saved to disk.");
    }

    private void sendAbordMessage(Player player) {
        PluginData.getMessageUtil().sendInfoMessage(player, "Saving pvp match cancelled.");
    }
}
