package com.mcmiddleearth.minigames.golf;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Planetology
 */
public class GolfHoleLocation {

    @Getter @Setter private Location location;
    @Getter private String name, pointName;
    @Getter private int par;

    public GolfHoleLocation(Location loc, String name, int par) {
        this.location = loc;
        this.name = name;
        this.par = par;

        try {
            setPoint(name,false);
        } catch (FileNotFoundException ex) {
            try {
                setPoint(name,false);
            } catch (FileNotFoundException ex1) {
                Logger.getLogger(GolfHoleLocation.class.getName()).log(Level.SEVERE, "Default location not found.", ex1);
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
