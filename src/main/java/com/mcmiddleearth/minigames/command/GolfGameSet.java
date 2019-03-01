package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.GolfGame;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Planetology
 */
public class GolfGameSet extends AbstractGameCommand {

    protected GolfGameSet(String... permissionNodes) {
        super(2, true, permissionNodes);
        cmdGroup = CmdGroup.GOLF;
        setShortDescription(": Defines a golf game location.");
        setUsageDescription(" tee|hole [start|end|add|undo] <par>: With argument 'tee' or 'hole' defines a golf tee-pad or target, with a par value for a hole ");
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

            if (args[0].equalsIgnoreCase("tee")) {
                if (args[1].equalsIgnoreCase("start")) {
                    if(golfGame.getLocationManager().setStartTeeLocation(loc)) {
                        sendStartTeeSetMessage(cs, golfGame.getLocationManager().getTeeCount());
                        placeTeePad(cs);
                    }
                } else if (args[1].equalsIgnoreCase("end")) {
                    int teeSize = golfGame.getLocationManager().getTeeCount();

                    if (!(teeSize == 8 || teeSize == 17 || teeSize == 26 || teeSize == 13 || teeSize == 19)) {
                        sendInvalidEndLocationMessage(cs);
                        return;
                    }

                    if (golfGame.getLocationManager().setEndTeeLocation(loc)) {
                        sendEndTeeSetMessage(cs, golfGame.getLocationManager().getTeeCount());
                        placeTeePad(cs);
                    }
                } else if (args[1].equalsIgnoreCase("add")) {
                    if (golfGame.getLocationManager().getTeeStart() == null) {
                        sendNoStartTeeMessage(cs);
                        return;
                    }

                    if (golfGame.getLocationManager().addTeeLocation(loc)) {
                        sendTeeAddMessage(cs, golfGame.getLocationManager().getTeeCount());
                        placeTeePad(cs);
                    }
                } else if (args[1].equalsIgnoreCase("undo")) {
                    if (golfGame.getLocationManager().getGameTees().size() > 1) {
                        if (golfGame.getLocationManager().getGameTees().getLast().getLocation().getBlock().getType().equals(Material.STONE_BRICK_SLAB)) golfGame.getLocationManager().getGameTees().getLast().getLocation().getBlock().setType(Material.AIR);

                        golfGame.getLocationManager().getGameTees().removeLast();
                        golfGame.getLocationManager().getTees().removeLast();
                        golfGame.getLocationManager().teeCount --;
                        sendTeeUndoMessage(cs, golfGame.getLocationManager().getTeeCount());
                    } else {
                        sendInvalidUndoMessage(cs);
                    }
                } else {
                    sendTeeNotValidMessage(cs);
                }
            } else if (args[0].equalsIgnoreCase("hole")) {
                if (args[1].equalsIgnoreCase("start")) {
                    if (args.length > 2) {
                        if (isInt(args[2])) {
                            if (golfGame.getLocationManager().setStartHoleLocation(loc, Integer.parseInt(args[2]))) {
                                sendStartHoleSetMessage(cs, golfGame.getLocationManager().getHoleCount());
                                placeTarget(cs);
                            }
                        } else {
                            sendHoleNoParMessage(cs);
                        }
                    } else {
                        sendHoleNoParMessage(cs);
                    }
                } else if (args[1].equalsIgnoreCase("end")) {
                    if (args.length > 2) {
                        if (isInt(args[2])) {
                            int holeSize = golfGame.getLocationManager().getHoleCount();

                            if (!(holeSize == 8 || holeSize == 17 || holeSize == 26 || holeSize == 13 || holeSize == 19)) {
                                sendInvalidEndLocationMessage(cs);
                                return;
                            }

                            if (golfGame.getLocationManager().setEndHoleLocation(loc, Integer.parseInt(args[2]))) {
                                sendEndHoleSetMessage(cs, golfGame.getLocationManager().getHoleCount());
                                placeTarget(cs);
                            }
                        } else {
                            sendHoleNoParMessage(cs);
                        }
                    } else {
                        sendHoleNoParMessage(cs);
                    }
                } else if (args[1].equalsIgnoreCase("add")) {
                    if (args.length > 2) {
                        if (isInt(args[2])) {
                            if (golfGame.getLocationManager().getHoleStart() == null) {
                                sendNoStartHoleMessage(cs);
                                return;
                            }

                            if (golfGame.getLocationManager().addHoleLocation(loc, Integer.parseInt(args[2]))) {
                                sendHoleAddMessage(cs, golfGame.getLocationManager().getHoleCount());
                                placeTarget(cs);
                            }
                        } else {
                            sendHoleNoParMessage(cs);
                        }
                    } else {
                        sendHoleNoParMessage(cs);
                    }
                } else if (args[1].equalsIgnoreCase("undo")) {
                    if (golfGame.getLocationManager().getGameHoles().size() > 1) {
                        if (golfGame.getLocationManager().getGameHoles().getLast().getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.IRON_BARS)) golfGame.getLocationManager().getGameHoles().getLast().getLocation().getBlock().getRelative(BlockFace.DOWN).setType(Material.AIR);
                        golfGame.getLocationManager().getGameHoles().getLast().getLocation().getBlock().setType(Material.AIR);

                        golfGame.getLocationManager().getGameHoles().removeLast();
                        golfGame.getLocationManager().getHoles().removeLast();
                        golfGame.getLocationManager().holeCount --;
                        sendHoleUndoMessage(cs, golfGame.getLocationManager().getHoleCount());
                    } else {
                        sendInvalidUndoMessage(cs);
                    }
                } else {
                    sendHoleNotValidMessage(cs);
                }
            } else {
                sendInvalidArgumentMessage(cs);
            }
        }
    }

    private void placeTarget(CommandSender cs) {
        Block locationBlock = ((Player) cs).getLocation().getBlock();

        locationBlock.setType(Material.ORANGE_WOOL);
        locationBlock.getRelative(BlockFace.DOWN).setType(Material.IRON_BARS);
    }

    private void placeTeePad(CommandSender cs) {
        Block locationBlock = ((Player) cs).getLocation().getBlock();

        locationBlock.setType(Material.STONE_BRICK_SLAB);
    }

    private boolean isInt(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
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
        PluginData.getMessageUtil().sendInfoMessage(cs, "End hole location saved. " + ChatColor.GREEN + totalHoles + ChatColor.AQUA + " total holes.");
    }

    private void sendInvalidEndLocationMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Please set the end location as last. A tee/hole amount of 9, 18, 27, 14 or 20 is needed.");
    }

    private void sendTeeAddMessage(CommandSender cs, int totalTees) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Tee location saved. " + ChatColor.GREEN + totalTees + ChatColor.AQUA + " total tees.");
    }

    private void sendHoleAddMessage(CommandSender cs, int totalHoles) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Hole location saved. " + ChatColor.GREEN + totalHoles + ChatColor.AQUA + " total holes.");
    }

    private void sendHoleUndoMessage(CommandSender cs, int totalHoles) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Removed last hole location. " + ChatColor.GREEN + totalHoles + ChatColor.AQUA + " total holes.");
    }

    private void sendTeeUndoMessage(CommandSender cs, int totalTees) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "Removed last tee location. " + ChatColor.GREEN + totalTees + ChatColor.AQUA + " total tees.");
    }

    private void sendInvalidUndoMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You can't undo the start location, please set a different start location instead.");
    }

    private void sendHoleNoParMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Give a valid number for the par value of this hole.");
    }

    private void sendNoStartTeeMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Please first set the start tee location. Usage: /game golfset tee start");
    }

    private void sendNoStartHoleMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "Please first set the start hole location. Usage: /game golfset hole start [par amount].");
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
