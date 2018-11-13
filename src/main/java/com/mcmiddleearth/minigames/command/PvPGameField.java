package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.PvPGame;
import com.mcmiddleearth.pluginutil.NumericUtil;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Planetology
 */
public class PvPGameField extends AbstractGameCommand {

    public PvPGameField(String... permissionNodes) {
        super(0, true, permissionNodes);
        cmdGroup = CmdGroup.PVP;
        setShortDescription(": Defines a pvp game arena.");
        setUsageDescription(" sphere <radius>: With argument 'sphere' defines the sphere type of pvp arena with a radius of <radius>.");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game)
                && isCorrectGameType((Player) cs, game, GameType.PVP)) {
            if(game.isAnnounced()) {
                sendAlreadyAnnouncedErrorMessage(cs);
                return;
            }

            PvPGame pvpGame = (PvPGame) game;
            Selection selection = PluginData.getWorldEdit().getSelection((Player) cs);

            if (args.length < 1) {
                if (selection != null) {
                    if (pvpGame.getLocationManager().setArenaMax(selection.getMaximumPoint()) && pvpGame.getLocationManager().setArenaMin(selection.getMinimumPoint())) {
                        pvpGame.setCuboidArena(true);
                        sendFieldSetMessage(cs);
                    }
                } else {
                    sendNoSelectionMessage(cs);
                }
            } else if (args[0].equalsIgnoreCase("sphere")) {
                if (args.length == 1) {
                    sendNoRadiusMessage(cs);
                } else {
                    if (!NumericUtil.isInt(args[1])) {
                        sendNoIntegerRadiusMessage(cs);
                        return;
                    }

                    if (pvpGame.getLocationManager().setArenaCenter(((Player) cs).getLocation()) && pvpGame.getLocationManager().setArenaRadius(Integer.parseInt(args[1]))) {
                        sendFieldSetMessage(cs);
                    }
                }
            }
        }
    }

    private void sendFieldSetMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Pvp game area saved.");
    }

    private void sendNoSelectionMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You need to select an area for the pvp match arena.");
    }

    private void sendNoRadiusMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "A pvp match needs an indicated radius for a sphere arena.");
    }

    private void sendNoIntegerRadiusMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "A pvp match sphere arena radius needs to be an integer.");
    }
}
