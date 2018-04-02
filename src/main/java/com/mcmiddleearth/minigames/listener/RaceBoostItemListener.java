/*
 * Copyright (C) 2018 MCME
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.minigames.listener;

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.game.AbstractGame;
import com.mcmiddleearth.minigames.game.RaceGame;
import com.mcmiddleearth.minigames.raceBoostItem.BoostItemManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

/**
 *
 * @author Eriol_Eandur
 */
public class RaceBoostItemListener implements Listener {

    @EventHandler
    public void playerPickupItem(EntityPickupItemEvent event) {
        for(AbstractGame game: PluginData.getGames()) {
            if(game instanceof RaceGame) {
                BoostItemManager boostManager = ((RaceGame) game).getBoostItemManager();
                if(boostManager.isBoostItem(event.getItem())) {
                    event.setCancelled(true);
                    if(event.getEntity() instanceof Player) {
                        Player player = (Player) event.getEntity();
                        if(game.isInGame(player)) {
                            boostManager.handlePickUp(player, event.getItem());
                        }
                    }
                }
            }
        }
    }

}
