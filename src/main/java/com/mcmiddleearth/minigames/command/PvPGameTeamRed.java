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
public class PvPGameTeamRed extends AbstractGameCommand {

    public PvPGameTeamRed(String... permissionNodes) {
        super(1, true, permissionNodes);
        cmdGroup = CmdGroup.PVP;
        setShortDescription(": Adds a player to the red team.");
        setUsageDescription(" <player>: Adds <player> to the red team.");
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
                pvpGame.getRedTeam().add(args[0]);
                sendSetRedMessage(cs, args[0]);
                sendJoinRedMessage(Bukkit.getPlayer(args[0]));
            } else if(pvpGame.getRedTeam().contains(cs.getName())) {
                sendAlreadyInTeamMessage(cs);
            } else if (pvpGame.getBlueTeam().contains(cs.getName())) {
                pvpGame.getBlueTeam().remove(args[0]);
                pvpGame.getRedTeam().add(args[0]);
                sendSetRedMessage(cs, args[0]);
                sendJoinRedMessage(Bukkit.getPlayer(args[0]));
            }
        }
    }

    private void sendAlreadyInTeamMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This player is already in team red.");
    }

    private void sendNoPvPerMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "This player is not in this game.");
    }

    private void sendSetRedMessage(CommandSender cs, String name) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You have added " + name + " to team " +  ChatColor.RED + "red" + ChatColor.AQUA + ".");
    }

    private void sendJoinRedMessage(Player player) {
        PluginData.getMessageUtil().sendInfoMessage(player, "You have been added to team " + ChatColor.RED + "red" + ChatColor.AQUA + ".");
    }

    private void sendAlreadyStartedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "The match already started.");
    }
}
