package com.mcmiddleearth.minigames.golf;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * @author Planetology
 */
public class GolfPlayer {

    @Getter @Setter private Player golfer;
    @Getter @Setter private int shots, points;
    @Getter @Setter private Location arrowLocation;
    @Getter @Setter private Material arrowBlockMaterial;
    @Getter @Setter private boolean shot;

    public GolfPlayer(Player player) {
        golfer = player;
        shots = 0;
        points = 0;
    }
}
