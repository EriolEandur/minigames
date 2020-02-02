package com.mcmiddleearth.minigames.command;

//import com.boydti.fawe.FaweAPI;
import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.PvPGame;
import com.mcmiddleearth.pluginutil.WEUtil;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Planetology
 */
public class PvPGameField extends AbstractGameCommand {

    public PvPGameField(String... permissionNodes) {
        super(0, true, permissionNodes);
        cmdGroup = CmdGroup.PVP;
        setShortDescription("Defines a pvp game arena.");
        setUsageDescription("Defines a pvp game arena from selected world edit region.");
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
            Region region = WEUtil.getSelection((Player)cs);//FaweAPI.wrapPlayer(cs).getSelection();

            if (region != null) {
                BlockVector3 max = region.getMaximumPoint();
                BlockVector3 min = region.getMinimumPoint();

                if (pvpGame.getLocationManager().setArenaMax(new Location(((Player) cs).getWorld(), max.getX(), max.getY(), max.getZ()))
                        && pvpGame.getLocationManager().setArenaMin(new Location(((Player) cs).getWorld(), min.getX(), min.getY(), min.getZ()))) {
                    sendFieldSetMessage(cs);
                }
            } else {
                sendNoSelectionMessage(cs);
            }
        }
    }

    private void sendFieldSetMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Pvp game area saved.");
    }

    private void sendNoSelectionMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You need to select an area for the pvp match arena.");
    }
}
