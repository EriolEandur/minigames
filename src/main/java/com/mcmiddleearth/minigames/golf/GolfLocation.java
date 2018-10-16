package com.mcmiddleearth.minigames.golf;

import com.mcmiddleearth.minigames.raceCheckpoint.Checkpoint;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Planetology
 * @since 10/8/2018
 */
public class GolfLocation {

    @Getter @Setter private Location location;
    @Getter private String name, pointName;

    public GolfLocation(Location loc, String name) {
        this.location = loc;
        this.name = name;
        try {
            setPoint(name,false);
        } catch (FileNotFoundException ex) {
            try {
                setPoint(name,false);
            } catch (FileNotFoundException ex1) {
                Logger.getLogger(Checkpoint.class.getName()).log(Level.SEVERE, "Default marker not found.", ex1);
            }
        }
    }

    public final void setPoint(String name) throws FileNotFoundException {
        setPoint(name,true);
    }

    private void setPoint(String name, boolean removeOldPoint) throws FileNotFoundException {
        if(name != null) {
            pointName = name;
        }
    }
}
