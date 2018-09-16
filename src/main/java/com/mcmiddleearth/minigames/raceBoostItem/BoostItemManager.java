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
package com.mcmiddleearth.minigames.raceBoostItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionBrewer;

/**
 *
 * @author Eriol_Eandur
 */
public class BoostItemManager {
    
    private List<BoostItem> placedBoosts = new ArrayList<>();
    
    private Map<UUID,BoostEffect> playerEffects = new HashMap<>();

    /**
     * Checks if an Item at the map represents a boost item of this BoostItemManager
     * @param item The Item that will be checked
     * @return 
     */
    public boolean isBoostItem(Item item) {
        //TODO
      boolean isBoost = false;
         
        
       return isBoost;
    }

    /**
     * Handles the interaction of a player with a boost item depending on the type of the item and
     * if the player already has a boost effect.
     * @param player A player who is in the game that is associated to this BoostItemManager
     * @param item Item that might get picked up by the player.
     */
    public void handlePickUp(Player player, Item item) {
        //TODO
        
        
        
    }
      
}
   

