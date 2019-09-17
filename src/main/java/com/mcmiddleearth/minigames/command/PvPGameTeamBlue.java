package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.PvPGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Planetology
 */
public class PvPGameTeamBlue extends AbstractGameCommand {

    public PvPGameTeamBlue(String... permissionNodes) {
        super(1, true, permissionNodes);
        cmdGroup = CmdGroup.PVP;
        setShortDescription(": Adds a player to the blue team.");
        setUsageDescription(" <player>: Adds <player> to the blue team.");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isManager((Player) cs, game) && isCorrectGameType((Player) cs, game, GameType.PVP)) {
            if(!game.isAnnounced()) {
                sendNotAnnouncedErrorMessage(cs);
                return;
            }

            PvPGame pvpGame = (PvPGame) game;

            if(pvpGame.isStarted()) {
                sendAlreadyStartedMessage(cs);

                return;
            }

            if (!pvpGame.getPvpers().contains((args[0]))) {
                sendNoPvPerMessage(cs);

                return;
            }

            if (!pvpGame.getRedTeam().contains(cs.getName()) || !pvpGame.getBlueTeam().contains(cs.getName())) {
                pvpGame.getBlueTeam().add(args[0]);
                sendSetBlueMessage(cs, args[0]);
                sendJoinBlueMessage(Bukkit.getPlayer(args[0]));
            } else if(pvpGame.getBlueTeam().contains(cs.getName())) {
                sendAlreadyInTeamMessage(cs);
            } else if (pvpGame.getRedTeam().contains(cs.getName())) {
                pvpGame.getRedTeam().remove(args[0]);
                pvpGame.getBlueTeam().add(args[0]);
                sendSetBlueMessage(cs, args[0]);
                sendJoinBlueMessage(Bukkit.getPlayer(args[0]));
            }
        }
    }

    private void sendAlreadyInTeamMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This player is already in team red.");
    }

    private void sendNoPvPerMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This player is not in this game.");
    }

    private void sendSetBlueMessage(CommandSender cs, String name) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You have added " + name + " to team " +  ChatColor.BLUE + "blue" + ChatColor.AQUA + ".");
    }

    private void sendJoinBlueMessage(Player player) {
        PluginData.getMessageUtil().sendInfoMessage(player, "You joined team " + ChatColor.BLUE + "blue" + ChatColor.AQUA + ".");
    }

    private void sendAlreadyStartedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "The match already started.");
    }
}
