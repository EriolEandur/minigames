package com.mcmiddleearth.minigames.golf;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.minigames.game.GolfGame;
import com.mcmiddleearth.pluginutil.JSONUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
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
 */
public class GolfLocationManager {

    private GolfGame game;

    @Getter private final LinkedList<GolfHoleLocation> holes = new LinkedList<>();
    @Getter private final LinkedList<GolfTeeLocation> tees = new LinkedList<>();
    @Getter private final LinkedList<GolfHoleLocation> gameHoles = new LinkedList<>();
    @Getter private final LinkedList<GolfTeeLocation> gameTees = new LinkedList<>();

    @Getter private GolfTeeLocation teeStart, teeEnd;
    @Getter private GolfHoleLocation holeStart, holeEnd;
    @Getter public int holeCount, teeCount;

    public GolfLocationManager(GolfGame game){
            this.game = game;
    }

    public boolean setStartTeeLocation(Location loc) {
        if(teeStart == null) {
            teeCount ++;
            teeStart = new GolfTeeLocation(loc, "teeStart");
            gameTees.add(teeStart);
        } else {
            if (teeStart.getLocation().getBlock().getType().equals(Material.STONE_BRICK_SLAB)) teeStart.getLocation().getBlock().setType(Material.AIR);
            teeStart.setLocation(loc);
        }
        return true;
    }

    public boolean setEndTeeLocation(Location loc) {
        if(teeEnd == null) {
            teeCount ++;
            teeEnd = new GolfTeeLocation(loc, "teeEnd");
            gameTees.add(teeEnd);
        } else {
            if (teeEnd.getLocation().getBlock().getType().equals(Material.STONE_BRICK_SLAB)) teeEnd.getLocation().getBlock().setType(Material.AIR);
            teeEnd.setLocation(loc);
        }
        return true;
    }

    public boolean setStartHoleLocation(Location loc, int par) {
        if(holeStart == null) {
            holeCount ++;
            holeStart = new GolfHoleLocation(loc, "holeStart", par);
            gameHoles.add(holeStart);
        } else {
            if (holeStart.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.IRON_BARS)) holeStart.getLocation().getBlock().getRelative(BlockFace.DOWN).setType(Material.AIR);
            holeStart.getLocation().getBlock().setType(Material.AIR);
            holeStart.setLocation(loc);
        }

        return true;
    }

    public boolean setEndHoleLocation(Location loc, int par) {
        if(holeEnd == null) {
            holeCount ++;
            holeEnd = new GolfHoleLocation(loc, "holeEnd", par);
            gameHoles.add(holeEnd);
        } else {
            if (holeEnd.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.IRON_BARS)) holeEnd.getLocation().getBlock().getRelative(BlockFace.DOWN).setType(Material.AIR);
            holeEnd.getLocation().getBlock().setType(Material.AIR);
            holeEnd.setLocation(loc);
        }

        return true;
    }

    public boolean addTeeLocation(Location loc) {
        tees.add(new GolfTeeLocation(loc, nextTeeName()));
        teeCount ++;
        gameTees.add(new GolfTeeLocation(loc, nextTeeName()));
        return true;
    }

    public boolean addHoleLocation(Location loc, int par) {
        holes.add(new GolfHoleLocation(loc, nextHoleName(), par));
        holeCount ++;
        gameHoles.add(new GolfHoleLocation(loc, nextHoleName(), par));
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

        JSONObject jTeeStart = jTeeLocation(teeStart);
        JSONObject jTeeEnd = jTeeLocation(teeEnd);
        JSONObject jHoleStart = jHoleLocation(holeStart, holeStart.getPar());
        JSONObject jHoleEnd = jHoleLocation(holeEnd, holeEnd.getPar());

        JSONArray jTeeArray = new JSONArray();
        JSONArray jHoleArray = new JSONArray();

        for (GolfTeeLocation tee : tees) {
            jTeeArray.add(jTeeLocation(tee));
        }

        for (GolfHoleLocation hole : holes) {
            jHoleArray.add(jHoleLocation(hole, hole.getPar()));
        }

        JSONObject jCourse = new JSONObject();
        jCourse.put("teeStart", jTeeStart);
        jCourse.put("teeEnd", jTeeEnd);
        jCourse.put("holeStart", jHoleStart);
        jCourse.put("holeEnd", jHoleEnd);
        jCourse.put("tees", jTeeArray);
        jCourse.put("holes", jHoleArray);

        jCourse.put("description", description);

        try (FileWriter fw = new FileWriter(file)) {
            jCourse.writeJSONString(fw);
        }
    }

    private JSONObject jTeeLocation(GolfTeeLocation loc) {
        JSONObject jLocation = new JSONObject();
        jLocation.put("location", JSONUtil.jLocation(loc.getLocation()));
        return jLocation;
    }

    private JSONObject jHoleLocation(GolfHoleLocation loc, int par) {
        JSONObject jLocation = new JSONObject();
        jLocation.put("location", JSONUtil.jLocation(loc.getLocation()));
        jLocation.put("par", par);
        return jLocation;
    }

    public void loadCourse(File file) throws FileNotFoundException, ParseException {
        try {
            StringBuilder input;

            try (Scanner reader = new Scanner(file)) {
                input = new StringBuilder();

                while(reader.hasNext()){
                    input.append(reader.nextLine());
                }
            }

            JSONObject jInput = (JSONObject) new JSONParser().parse(input.toString());

            teeStart = loadTeeLocation((JSONObject) jInput.get("teeStart"), "teeStart");
            holeStart = loadHoleLocation((JSONObject) jInput.get("holeStart"), "holeStart");
            gameTees.add(teeStart);
            gameHoles.add(holeStart);

            JSONArray jTees = (JSONArray) jInput.get("tees");

            for (Object jTee : jTees) {
                gameTees.add(loadTeeLocation((JSONObject)jTee, nextTeeName()));
                teeCount++;
            }

            JSONArray jHoles = (JSONArray) jInput.get("holes");

            for (Object jHole : jHoles) {
                gameHoles.add(loadHoleLocation((JSONObject)jHole, nextHoleName()));
                holeCount ++;
            }

            teeEnd = loadTeeLocation((JSONObject) jInput.get("teeEnd"), "teeEnd");
            holeEnd = loadHoleLocation((JSONObject) jInput.get("holeEnd"), "holeEnd");
            gameTees.add(teeEnd);
            gameHoles.add(holeEnd);

            teeCount = teeCount + 2;
            holeCount = holeCount + 2;
        } catch (FileNotFoundException | ParseException ex) {
            MiniGamesPlugin.getPluginInstance().getLogger().log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

    private GolfTeeLocation loadTeeLocation(JSONObject jLoc, String name) {
        Location loc = JSONUtil.getLocation(jLoc, "location");

        if (loc != null) {
            return new GolfTeeLocation(loc, name);
        } else {
            return null;
        }
    }

    private GolfHoleLocation loadHoleLocation(JSONObject jLoc, String name) {
        Location loc = JSONUtil.getLocation(jLoc, "location");
        Integer par = JSONUtil.getInteger(jLoc, "par");

        return new GolfHoleLocation(loc, name, par);
    }

    private String nextTeeName() {
        return "tee" + teeCount;
    }

    private String nextHoleName() {
        return "hole" + holeCount;
    }
}
