package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.GolfGame;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;

/**
 * @author Planetology
 */
public class GolfGameSet extends AbstractGameCommand {

    public GolfGameSet(String... permissionNodes) {
        super(2, true, permissionNodes);
        cmdGroup = CmdGroup.GOLF;
        setShortDescription(": Defines a golf game location.");
        setUsageDescription(" tee|hole [start|end|add]: With argument 'tee' or 'hole' defines a golf tee-pad or target.");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game)
                && isCorrectGameType((Player) cs, game, GameType.GOLF)) {
            if (game.isAnnounced()) {
                sendAlreadyAnnouncedErrorMessage(cs);
                return;
            }

            GolfGame golfGame = (GolfGame) game;
            Location loc = ((Player) cs).getLocation();

            if(args[0].equalsIgnoreCase("tee")) {
                if (args[1].equalsIgnoreCase("start")) {
                    if(golfGame.getLocationManager().setStartTeeLocation(loc)) {
                        sendStartTeeSetMessage(cs, golfGame.getLocationManager().getTeeCount());
                    }
                } else if (args[1].equalsIgnoreCase("end")) {
                    if (golfGame.getLocationManager().setEndTeeLocation(loc)) {
                        sendEndTeeSetMessage(cs, golfGame.getLocationManager().getTeeCount());
                    }
                } else if (args[1].equalsIgnoreCase("add")) {
                    if (golfGame.getLocationManager().addTeeLocation(loc)) {
                        sendTeeAddMessage(cs, golfGame.getLocationManager().getTeeCount());
                    }
                } else {
                    sendTeeNotValidMessage(cs);
                }
            } else if(args[0].equalsIgnoreCase("hole")) {
                if (args[1].equalsIgnoreCase("start")) {
                    if(golfGame.getLocationManager().setStartHoleLocation(loc)) {
                        sendStartHoleSetMessage(cs, golfGame.getLocationManager().getHoleCount());
                        setWool(cs);
                    }
                } else if (args[1].equalsIgnoreCase("end")) {
                    if (golfGame.getLocationManager().setEndHoleLocation(loc)) {
                        sendEndHoleSetMessage(cs, golfGame.getLocationManager().getHoleCount());
                        setWool(cs);
                    }
                } else if (args[1].equalsIgnoreCase("add")) {
                    if (golfGame.getLocationManager().addHoleLocation(loc)) {
                        sendHoleAddMessage(cs, golfGame.getLocationManager().getHoleCount());
                        setWool(cs);
                    }
                } else {
                    sendHoleNotValidMessage(cs);
                }
            } else {
                sendInvalidArgumentMessage(cs);
            }
        }
    }

    private void sendStartTeeSetMessage(CommandSender cs, int totalTees) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Start tee location saved. " + ChatColor.GREEN + totalTees + ChatColor.AQUA + " total tees.");
    }

    private void sendEndTeeSetMessage(CommandSender cs, int totalTees) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "End tee location saved. " + ChatColor.GREEN + totalTees + ChatColor.AQUA + " total tees.");
    }

    private void sendStartHoleSetMessage(CommandSender cs, int totalHoles) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Start hole location saved. " + ChatColor.GREEN + totalHoles + ChatColor.AQUA + " total holes.");
    }

    private void sendEndHoleSetMessage(CommandSender cs, int totalHoles) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "End hole location saved. " + ChatColor.GREEN + totalHoles + ChatColor.AQUA + " total tees.");
    }

    private void sendTeeAddMessage(CommandSender cs, int totalTees) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Tee location saved. " + ChatColor.GREEN + totalTees + ChatColor.AQUA + " total tees.");
    }

    private void sendHoleAddMessage(CommandSender cs, int totalHoles) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Hole location saved. " + ChatColor.GREEN + totalHoles + ChatColor.AQUA + " total holes.");
    }

    private void setWool(CommandSender cs) {
        ((Player) cs).getLocation().getBlock().setType(Material.WOOL);
        ((Player) cs).getLocation().getBlock().setData((byte) 1);
    }

    private void sendInvalidArgumentMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Invalid Argument. Usage: /game golfset tee|hole [|start|end|add].");
    }

    private void sendTeeNotValidMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You did not specify a valid tee location.");
    }

    private void sendHoleNotValidMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You did not specify a valid hole location.");
    }
}
