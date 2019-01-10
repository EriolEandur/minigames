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
package com.mcmiddleearth.minigames.utils;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

/**
 *
 * @author Eriol_Eandur
 */
public class WorldGuardUtil {
    
    public static void createPVPArea(CuboidSelection selection) {
        removePVPArea(selection);
        ProtectedCuboidRegion region = new ProtectedCuboidRegion("pvpArea",
                                                     BukkitUtil.toVector(selection.getMinimumPoint().getBlock()),
                                                     BukkitUtil.toVector(selection.getMaximumPoint().getBlock()));
        region.setPriority(10);
        region.setFlag(DefaultFlag.PVP,StateFlag.State.ALLOW);
        WGBukkit.getRegionManager(selection.getWorld())
                .addRegion(region);
        
    }
    
    public static void removePVPArea(CuboidSelection selection) {
        if(selection.getWorld()!=null) {
            WGBukkit.getRegionManager(selection.getWorld()).removeRegion("pvpArea");
        }
    }
}
