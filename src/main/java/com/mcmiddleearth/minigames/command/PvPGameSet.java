package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.PvPGame;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Planetology
 */
public class PvPGameSet extends AbstractGameCommand {

    public PvPGameSet(String... permissionNodes) {
        super(1, true, permissionNodes);
        cmdGroup = CmdGroup.PVP;
        setShortDescription(": Defines a pvp game location.");
        setUsageDescription(" red|blue: With argument 'red' or 'blue' defines the spawn of the red or blue team.");
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
            Location loc = ((Player) cs).getLocation();
            if(args[0].equalsIgnoreCase("red")) {
                if(pvpGame.getLocationManager().setRedSpawn(loc)) {
                    sendRedSetMessage(cs);
                }
            } else if(args[0].equalsIgnoreCase("blue")) {
                if(pvpGame.getLocationManager().setBlueSpawn(loc)) {
                    sendBlueSetMessage(cs);
                }
            } else {
                sendInvalidArgumentMessage(cs);
            }
        }
    }

    private void sendRedSetMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Team red spawn location saved.");
    }

    private void sendBlueSetMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Team blue spawn location saved.");
    }

    private void sendInvalidArgumentMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Invalid Argument. Usage: /game pvpset red|blue.");
    }
}
