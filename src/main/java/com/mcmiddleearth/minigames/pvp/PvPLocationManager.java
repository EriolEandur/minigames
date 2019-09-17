package com.mcmiddleearth.minigames.pvp;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.game.PvPGame;
import com.mcmiddleearth.minigames.utils.WorldGuardUtil;
import com.mcmiddleearth.pluginutil.JSONUtil;
import lombok.Getter;
import org.bukkit.Location;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * @author Planetology
 */
public class PvPLocationManager {

    private PvPGame game;

    @Getter private PvpLocation redSpawn, blueSpawn, arenaMax, arenaMin;

    public PvPLocationManager(PvPGame game) {
        this.game = game;
    }

    public boolean setRedSpawn(Location loc) {
        if(redSpawn == null) {
            redSpawn = new PvpLocation(loc, "redSpawn");
        } else {
            redSpawn.setLocation(loc);
        }
        return true;
    }

    public boolean setBlueSpawn(Location loc) {
        if(blueSpawn == null) {
            blueSpawn = new PvpLocation(loc, "blueSpawn");
        }
        else {
            blueSpawn.setLocation(loc);
        }
        return true;
    }

    public boolean setArenaMax(Location loc) {
        if(arenaMax == null) {
            arenaMax = new PvpLocation(loc, "arenaMax");
        } else {
            arenaMax.setLocation(loc);
        }
        return true;
    }

    public boolean setArenaMin(Location loc) {
        if(arenaMin == null) {
            arenaMin = new PvpLocation(loc, "arenaMin");
        } else {
            arenaMin.setLocation(loc);
        }
        return true;
    }

    public void saveArena(File file, String description) throws IOException {
        if(!game.hasRedSpawn() || !game.hasBlueSpawn()) {
            throw new IOException("No spawn for the red and blue team.");
        }

        if (!game.hasArenaMax() || !game.hasArenaMin()) {
            throw new IOException("No region for the pvp arena.");
        }

        JSONObject jRedSpawn, jBlueSpawn, jArenaMax = null, jArenaMin = null;

        jRedSpawn = jLocation(redSpawn);
        jBlueSpawn = jLocation(blueSpawn);

        if (game.hasArenaMax() || game.hasArenaMin()) {
            jArenaMax = jLocation(arenaMax);
            jArenaMin = jLocation(arenaMin);
        }

        JSONObject jArena = new JSONObject();

        jArena.put("redSpawn", jRedSpawn);
        jArena.put("blueSpawn", jBlueSpawn);

        if (jArenaMax != null || jArenaMin != null) {
            jArena.put("arenaMax", jArenaMax);
            jArena.put("arenaMin", jArenaMin);
        }

        jArena.put("description", description);

        try (FileWriter fw = new FileWriter(file)) {
            jArena.writeJSONString(fw);
        }
    }

    public void loadArena(File file) throws FileNotFoundException, ParseException {
        try {
            String input;
            try (Scanner reader = new Scanner(file)) {
                input = "";
                while(reader.hasNext()){
                    input = input+reader.nextLine();
                }
            }

            JSONObject jInput = (JSONObject) new JSONParser().parse(input);

            redSpawn = loadLocation((JSONObject) jInput.get("redSpawn"), "redSpawn");
            blueSpawn = loadLocation((JSONObject) jInput.get("blueSpawn"), "blueSpawn");

            if (jInput.get("arenaMax") != null || jInput.get("arenaMin") != null) {
                arenaMax = loadLocation((JSONObject) jInput.get("arenaMax"), "arenaMax");
                arenaMin = loadLocation((JSONObject) jInput.get("arenaMin"), "arenaMin");
            }
        } catch (FileNotFoundException | ParseException ex) {
            MiniGamesPlugin.getPluginInstance().getLogger().log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    private JSONObject jLocation(PvpLocation loc) {
        JSONObject jLocation = new JSONObject();
        jLocation.put("location", JSONUtil.jLocation(loc.getLocation()));
        return jLocation;
    }

    private PvpLocation loadLocation(JSONObject jLoc, String name) {
        Location loc = JSONUtil.getLocation(jLoc, "location");
        if(loc!=null) {
            return new PvpLocation(loc, name);
        }
        else {
            return null;
        }
    }
}
