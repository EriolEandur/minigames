package com.mcmiddleearth.minigames.command;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.GameType;
import com.mcmiddleearth.minigames.game.PvPGame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Planetology
 */
public class PvPGameRespawn extends AbstractGameCommand {

    public PvPGameRespawn(String... permissionNodes) {
        super(0, true, permissionNodes);
        cmdGroup = CmdGroup.PVP;
        setShortDescription(": Teleports you to your team spawn.");
        setUsageDescription(": Teleports you to your team spawn, but adds 1 kill to the enemy.");
    }

    @Override
    protected void execute(CommandSender cs, String... args) {
        AbstractGame game = getGame((Player) cs);
        if(game != null && isCorrectGameType((Player) cs, game, GameType.PVP)) {
            PvPGame pvpGame = (PvPGame) game;
            Player killed = (Player) cs;

            if (!pvpGame.isStarted()) {
                sendNoBattleMessage(cs);
                return;
            } else if(pvpGame.isReady() && !pvpGame.isStarted()) {
                sendNotStartedMessage(cs);
                return;
            }

            if(pvpGame.getRedTeam().contains(killed.getName()) || pvpGame.getBlueTeam().contains(killed.getName())) {
                killed.setHealth(0);

                sendRespawnedMessage(killed);
            }
        }
    }

    private void sendRespawnedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendInfoMessage(cs, "You have been teleported to your team spawn, but gave 1 kill to your enemy.");
    }

    private void sendNoBattleMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "You can't respawn at the moment.");
    }

    private void sendNotStartedMessage(CommandSender cs) {
        PluginData.getMessageUtil().sendErrorMessage(cs, "The battle hasn't begun yet.");
    }
}
