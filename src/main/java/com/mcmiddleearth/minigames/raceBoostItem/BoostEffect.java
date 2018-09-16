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

import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Eriol_Eandur
 */
public enum BoostEffect {
    
   SPEED (PotionEffectType.SPEED),
   JUMP (PotionEffectType.JUMP),
   SLOW (PotionEffectType.SLOW),
   CONFUSION (PotionEffectType.CONFUSION),
   BLINDNESS (PotionEffectType.BLINDNESS),
   INVISIBILITY (PotionEffectType.INVISIBILITY),
   LEVITATION (PotionEffectType.LEVITATION),
   GLOWING (PotionEffectType.GLOWING);
   
   private final Random random;
   private PotionEffectType type;
   
   BoostEffect(PotionEffectType type) {
       this.random = ThreadLocalRandom.current();
       this.type = type;
   }
   
   public PotionEffectType getType() {
       return type;
   }
   
   public BoostEffect getRandomBoostEffect() {
       return values()[random.nextInt(values().length)];
   }
   
   
   
   
}
