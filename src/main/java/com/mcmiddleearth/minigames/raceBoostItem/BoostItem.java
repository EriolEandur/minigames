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
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import com.mcmiddleearth.minigames.raceCheckpoint.CheckpointManager;
import com.mcmiddleearth.minigames.raceCheckpoint.Checkpoint;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Eriol_Eandur
 */
public class BoostItem {
    //TODO
    
     private List<BoostItem> placedBoosts = new ArrayList<>();
     
     private Map<UUID,BoostEffect> playerEffects = new HashMap<>();
     
    public <PotionEffectType> BoostItem()
    {
         final Player p;
         final int duration = 200;
         int amplifier = 0;
         BoostEffect random = null;
         random = random.getRandomBoostEffect();
         

         
         if(null != random) 
         /*Colors:
         SPEED = YELLOW
         JUMP = BLUE
         SLOW = GREEN
         CONFUSION = RED
         BLINDNESS = BLACK
         INVISIBILITY = AQUA
         LEVITATION = ORANGE
         GLOWING = SLIVER*/
         switch (random) {
             case SPEED:
                 BoostEffect.SPEED.apply(p, duration, amplifier, Color.YELLOW);
                 break;
             case JUMP:
                 BoostEffect.JUMP.apply(p, duration, amplifier, Color.BLUE);
                 break;
             case SLOW:
                 BoostEffect.SLOW.apply(p, duration, amplifier, Color.GREEN);
                 break;
             case CONFUSION:
                 BoostEffect.CONFUSION.apply(p, duration, amplifier, Color.RED);
                 break;
             case BLINDNESS:
                 BoostEffect.BLINDNESS.apply(p, duration, amplifier, Color.BLACK);
                 break;
             case INVISIBILITY:
                 BoostEffect.INVISIBILITY.apply(p, duration, amplifier, Color.AQUA);
                 break;
             case LEVITATION:
                 BoostEffect.LEVITATION.apply(p, duration, amplifier, Color.ORANGE);
                 break;
             case GLOWING:
                 BoostEffect.GLOWING.apply(p, duration, amplifier, Color.SILVER);
                 break;
             default:
                 break;
         }
          
         //Will here create the Boost Item and place it on the map 
          CheckpointManager checkpointManager = racegame.getCheckpointManager();
            for(Checkpoint checkpoint: checkpointManager.getCheckpoints()) {
             Location checkpointLocation = checkpoint.getLocation();
                Item entity = checkpointLocation.getWorld().dropItemNaturally(checkpointLocation, new ItemStack(mat));
            }
           
         
          
                 
         
         
         
         
         
         
      
    }
    
  
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
