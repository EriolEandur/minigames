/*
 * Copyright (C) 2015 MCME
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
package com.mcmiddleearth.minigames.raceCheckpoint;

import com.mcmiddleearth.minigames.MiniGamesPlugin;
import com.mcmiddleearth.pluginutil.JSONUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.logging.Level;
import lombok.Getter;
import org.bukkit.Location;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Eriol_Eandur
 */
public class CheckpointManager {
    
    String gameName;
   
    public static final int FAR_DISTANCE = 21;
    
    public static final int NEAR_DISTANCE = 10;
 
    @Getter
    private Checkpoint start;
    
    @Getter
    private Checkpoint finish;
    
    @Getter
    private final LinkedList<Checkpoint> checkpoints = new LinkedList<>();
    
    private String checkpointMarker, startMarker, finishMarker;
    
    private int checkpointCount = 0;
    
    public CheckpointManager(String gameName) {
        this.gameName = gameName;
    }
    
    public void deleteCheckpoints() {
        if(start!=null) start.delete();
        if(finish!=null) finish.delete();
        for(Checkpoint check:checkpoints) {
            check.delete();
        }
    }
    
    public boolean setStartLocation(Location loc) {
        if(isLocationValid(loc, start)) {
            if(start==null) {
                start = new Checkpoint(loc, gameName+"Start", startMarker);
            }
            else {
                start.setLocation(loc);
            }
            setLables();
            return true;
        }
        return false;
    }
   
    public boolean setFinishLocation(Location loc) {
        if(isLocationValid(loc, finish)) {
            if(finish==null) {
                finish = new Checkpoint(loc, gameName+"Finish", finishMarker);
            }
            else {
                finish.setLocation(loc);
            }
            setLables();
            return true;
        }
        return false;
    }
    
    public boolean setCheckpointLocation(Location loc, int id) {
        if(isIdValid(id) && isLocationValid(loc, checkpoints.get(indexFromId(id)))) {
            checkpoints.get(indexFromId(id)).setLocation(loc);
            setLables();
            return true;
        }
        return false;
    }
   
    public boolean addCheckpointLocation(Location loc) {
        if(isLocationValid(loc, null)) {
            if(checkpoints.add(new Checkpoint(loc, nextCheckpointName(), checkpointMarker))) {
                setLables();
                return true;
            }
        }
        return false;
    }
    
    public boolean insertCheckpointLocation(Location loc, int id) {
        if(isIdValid(id) && isLocationValid(loc, checkpoints.get(indexFromId(id)))) {
            checkpoints.add(indexFromId(id), new Checkpoint(loc, nextCheckpointName(), checkpointMarker));
            setLables();
            return true;
        }
        return false;
    }
    
    public boolean removeCheckpointLocation(int id) {    
        if(isIdValid(id)) {
            checkpoints.get(indexFromId(id)).delete();
            checkpoints.remove(indexFromId(id));
            setLables();
            return true;
            }
        return false;
    }
   
    public boolean removeCheckpointLocation(Location loc) {    
        for(Checkpoint search: checkpoints) {
            if(isNear(search.getLocation(),loc)) {
                search.delete();
                checkpoints.remove(search);
                setLables();
                return true;
            }
        }
        return false;
    }
    
    private void setLables() {
        if(start!=null) start.setLabel(gameName, "START");
        if(finish!=null) finish.setLabel(gameName, "FINISH");
        for(Checkpoint check: checkpoints) {
            check.setLabel(gameName, idFromIndex(checkpoints.indexOf(check))+"");
        }
    }
   
    public void setStartMarker(String name) throws FileNotFoundException {
        if(!Checkpoint.markerExists(name)){
            throw new FileNotFoundException();
        }
        startMarker = name;
        if(start!=null) {
            start.setMarker(startMarker);
            setLables();
        }
    }
    
    public void setFinishMarker(String name) throws FileNotFoundException {
        if(!Checkpoint.markerExists(name)){
            throw new FileNotFoundException();
        }
        finishMarker = name;
        if(finish!=null) {
            finish.setMarker(finishMarker);
            setLables();
        }
    }

    public void setCheckpointMarker(String name) throws FileNotFoundException {
        if(!Checkpoint.markerExists(name)){
            throw new FileNotFoundException();
        }
        checkpointMarker = name;
        for(Checkpoint check:checkpoints) {
            check.setMarker(checkpointMarker);
            setLables();
        }
    }
    
    public boolean setMarker(String name, Location loc) throws FileNotFoundException {
        if(!Checkpoint.markerExists(name)){
            throw new FileNotFoundException();
        }
        for(Checkpoint check: checkpoints) {
            if(isNear(loc,check.getLocation())) {
                check.setMarker(name);
                setLables();
                return true;
            }
        }
        return false;
    }
    
    public boolean isLocationValid(Location newLoc, Checkpoint toBeReplaced) {
        if(start!=null && toBeReplaced!=start && !isFar(start.getLocation(), newLoc)) {
            return false;
        }
        if(finish!=null && toBeReplaced!=finish && !isFar(finish.getLocation(),newLoc)) {
            return false;
        }
        for(Checkpoint search:checkpoints) {
            if(search!=toBeReplaced && !isFar(search.getLocation(), newLoc)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isIdValid(int id) {
        return id>0 && id<checkpoints.size()+1;
    }
    
    private static int indexFromId(int id) {
        return id-1;
    }
    
    private static int idFromIndex(int index) {
        return index+1;
    }
    
    public int getId(Checkpoint check) {
        return idFromIndex(checkpoints.indexOf(check));
    }
    
    public Checkpoint getCheckpoint(int id) {
        return checkpoints.get(indexFromId(id));
    }
    
    private String nextCheckpointName() {
        checkpointCount++;
        return gameName+"Check"+checkpointCount;
    }
    
    private static boolean isNear(Location loc1, Location loc2) {
        return loc1.getWorld().equals(loc2.getWorld()) && loc1.distance(loc2)<NEAR_DISTANCE;
    }
    
    private static boolean isFar(Location loc1, Location loc2) {
        return !loc1.getWorld().equals(loc2.getWorld()) || loc1.distance(loc2)>FAR_DISTANCE;
    }
    
    public void saveRace(File file, String description) throws IOException {
        if(start==null || finish ==null) {
            throw new IOException("No start/finish.");
        }
        JSONObject jStart = jCheckpoint(start);
        JSONObject jFinish = jCheckpoint(finish);
        JSONArray jCheckpointArray = new JSONArray();
        for (Checkpoint check : checkpoints) {
            jCheckpointArray.add(jCheckpoint(check));
        }
        JSONObject jRace = new JSONObject();
        jRace.put("description", description);
        jRace.put("start", jStart);
        jRace.put("finish", jFinish);
        jRace.put("checkpoints", jCheckpointArray);
        try(FileWriter fw = new FileWriter(file)) {
            jRace.writeJSONString(fw);
        }
    }
    
    public JSONObject jCheckpoint(Checkpoint check) {
        JSONObject jCheckpoint = new JSONObject();
        jCheckpoint.put("marker",check.getMarkerName());
        jCheckpoint.put("location",JSONUtil.jLocation(check.getLocation()));
        return jCheckpoint;
    }
    
    public void loadRace(File file) throws FileNotFoundException, ParseException{
        try {
            String input;
            try (Scanner reader = new Scanner(file)) {
                input = "";
                while(reader.hasNext()){
                    input = input+reader.nextLine();
                }
            }
            JSONObject jInput = (JSONObject) new JSONParser().parse(input);
            
            deleteCheckpoints();
            checkpointCount = 0;
            
            start = loadCheckpoint((JSONObject)jInput.get("start"), gameName+"Start");
            finish = loadCheckpoint((JSONObject)jInput.get("finish"), gameName+"Finish");
            JSONArray jCheckpoints = (JSONArray) jInput.get("checkpoints");
            for (Object jCheck : jCheckpoints) {
                checkpoints.add(loadCheckpoint((JSONObject)jCheck,gameName+nextCheckpointName()));
            }
            setLables();
        } catch (FileNotFoundException | ParseException ex) {
            MiniGamesPlugin.getPluginInstance().getLogger().log(Level.SEVERE, null, ex);
            throw ex;
        }
    }
    
    private Checkpoint loadCheckpoint(JSONObject jCheck, String name) {
        Location loc = JSONUtil.getLocation(jCheck, "location");
        if(loc!=null) {
            return new Checkpoint(loc, name, (String) jCheck.get("marker"));
        }
        else {
            return null;
        }
    }
    
}
