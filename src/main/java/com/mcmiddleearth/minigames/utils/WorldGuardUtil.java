package com.mcmiddleearth.minigames.utils;

import com.boydti.fawe.FaweAPI;
import com.mcmiddleearth.minigames.game.PvPGame;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;

/**
 * @author Planetology
 */
public class WorldGuardUtil {

    public static void createPVPArea(PvPGame game) {
        removePVPArea(game);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(FaweAPI.wrapPlayer(game.getManager().getPlayer()).getWorld());

        Location max = game.getLocationManager().getArenaMax().getLocation();
        Location min = game.getLocationManager().getArenaMin().getLocation();

        ProtectedRegion region = new ProtectedCuboidRegion("pvpArea", new BlockVector(max.getX(), max.getY(), max.getZ()), new BlockVector(min.getX(), min.getY(), min.getZ()));

        region.setPriority(10);
        region.setFlag(Flags.PVP, StateFlag.State.ALLOW);
        region.setFlag(Flags.EXIT, StateFlag.State.DENY);

        regions.addRegion(region);
    }

    public static void removePVPArea(PvPGame game) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(FaweAPI.wrapPlayer(game.getManager().getPlayer()).getWorld());

        regions.removeRegion("pvpArea");
    }
}
