package com.mcmiddleearth.minigames.golf;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.game.GolfGame;
import com.mcmiddleearth.pluginutil.JSONUtil;
import lombok.Getter;
import org.bukkit.Location;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * @author Planetology
 * @since 10/8/2018
 */
public class LocationManager {

    private GolfGame game;

    @Getter private final LinkedList<GolfLocation> holes = new LinkedList<>();
    @Getter private final LinkedList<GolfLocation> tees = new LinkedList<>();

    @Getter private GolfLocation teeStart, holeStart, teeEnd, holeEnd;
    @Getter private int holeCount, teeCount;

    public LocationManager(GolfGame game) {
        this.game = game;
    }

    public boolean setStartTeeLocation(Location loc) {
        if(teeStart == null) {
            teeCount++;
            teeStart = new GolfLocation(loc, "teeStart");
        }
        else {
            teeStart.setLocation(loc);
        }
        return true;
    }

    public boolean setEndTeeLocation(Location loc) {
        if(teeEnd == null) {
            teeCount++;
            teeEnd = new GolfLocation(loc, "teeEnd");
        }
        else {
            teeEnd.setLocation(loc);
        }
        return true;
    }

    public boolean setStartHoleLocation(Location loc) {
        if(holeStart == null) {
            holeCount ++;
            holeStart = new GolfLocation(loc, "holeStart");
        }
        else {
            holeStart.setLocation(loc);
        }

        return true;
    }

    public boolean setEndHoleLocation(Location loc) {
        if(holeEnd == null) {
            holeCount ++;
            holeEnd = new GolfLocation(loc, "holeEnd");
        }
        else {
            holeEnd.setLocation(loc);
        }

        return true;
    }

    public boolean addTeeLocation(Location loc) {
        tees.add(new GolfLocation(loc, nextTeeName()));
        return true;
    }

    public boolean addHoleLocation(Location loc) {
        holes.add(new GolfLocation(loc, nextHoleName()));
        return true;
    }

    public void saveCourse(File file, String description) throws IOException {
        if(!game.hasTeeStart() || !game.hasTeeEnd() || !game.hasHoleStart() || !game.hasHoleEnd()) {
            throw new IOException("No start tee or hole/end tee or hole.");
        }

        if(!game.hasEnoughTees()) {
            throw new IOException("No tee amount of 9, 18, 27, 14 or 20.");
        }

        if(!game.hasEnoughHoles()) {
            throw new IOException("No hole amount of 9, 18, 27, 14 or 20.");
        }

        JSONObject jTeeStart = jLocation(teeStart);
        JSONObject jTeeEnd = jLocation(teeEnd);
        JSONObject jHoleStart = jLocation(holeStart);
        JSONObject jHoleEnd = jLocation(holeEnd);

        JSONArray jTeeArray = new JSONArray();
        JSONArray jHoleArray = new JSONArray();

        for (GolfLocation tee : tees) {
            jTeeArray.add(jLocation(tee));
        }

        for (GolfLocation hole : holes) {
            jHoleArray.add(jLocation(hole));
        }

        JSONObject jCourse = new JSONObject();
        jCourse.put("teeStart", jTeeStart);
        jCourse.put("teeEnd", jTeeEnd);
        jCourse.put("holeStart", jHoleStart);
        jCourse.put("holeEnd", jHoleEnd);
        jCourse.put("tees", jTeeArray);
        jCourse.put("holes", jHoleArray);

        jCourse.put("description", description);
        try(FileWriter fw = new FileWriter(file)) {
            jCourse.writeJSONString(fw);
        }
    }

    private JSONObject jLocation(GolfLocation loc) {
        JSONObject jLocation = new JSONObject();
        jLocation.put("location", JSONUtil.jLocation(loc.getLocation()));
        return jLocation;
    }

    public void loadCourse(File file) throws FileNotFoundException, ParseException {
        try {
            String input;
            try (Scanner reader = new Scanner(file)) {
                input = "";
                while(reader.hasNext()){
                    input = input+reader.nextLine();
                }
            }

            JSONObject jInput = (JSONObject) new JSONParser().parse(input);

            teeStart = loadLocation((JSONObject)jInput.get("teeStart"), "teeStart");
            teeEnd = loadLocation((JSONObject)jInput.get("teeEnd"), "teeEnd");
            holeStart = loadLocation((JSONObject)jInput.get("holeStart"), "holeStart");
            holeEnd = loadLocation((JSONObject)jInput.get("holeEnd"), "holeEnd");

            teeCount = teeCount + 2;
            holeCount = holeCount + 2;

            JSONArray jTees = (JSONArray) jInput.get("tees");

            for (Object jTee : jTees) {
                tees.add(loadLocation((JSONObject)jTee, nextTeeName()));
            }

            JSONArray jHoles = (JSONArray) jInput.get("holes");
            for (Object jHole : jHoles) {
                holes.add(loadLocation((JSONObject)jHole, nextHoleName()));
            }

        } catch (FileNotFoundException | ParseException ex) {
            MiniGamesPlugin.getPluginInstance().getLogger().log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    private GolfLocation loadLocation(JSONObject jLoc, String name) {
        Location loc = JSONUtil.getLocation(jLoc, "location");
        if(loc!=null) {
            return new GolfLocation(loc, name);
        }
        else {
            return null;
        }
    }

    private String nextTeeName() {
        teeCount++;
        return "tee"+teeCount;
    }

    private String nextHoleName() {
        holeCount++;
        return "hole"+holeCount;
    }
}
