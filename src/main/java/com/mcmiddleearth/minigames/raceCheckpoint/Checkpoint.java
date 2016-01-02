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

import com.mcmiddleearth.minigames.data.PluginData;
import com.mcmiddleearth.minigames.utils.BukkitUtil;
import com.mcmiddleearth.minigames.utils.FileUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

/**
 *
 * @author Eriol_Eandur
 */
public class Checkpoint {
    
    @Getter
    private Location location;
    
    private static final File restoreDir = new File(PluginData.getRaceDir(),"restoreData");
    
    @Getter
    private static final File markerDir = new File(PluginData.getRaceDir(),"markerData");
    
    private static final String restoreExt = "res";
    
    @Getter
    private static final String markerExt = "mkr";
    
    private String name;
    
    private String label1 = "";
    private String label2 = "";
    
    private List<BlockState> marker = new ArrayList<>();
    
    @Getter
    private List<Location> checkLocList = new ArrayList<>();
    
    @Getter
    private String markerName = defaultMarker;
    
    private static final String defaultMarker = "default";
    
    public Checkpoint(Location loc, String name, String markerName) {
        this.location = loc;
        this.name = name;
        if(markerName!=null) {
            this.markerName = markerName;
        }
        try {
            loadMarkerFromFile(markerName);
        } catch (FileNotFoundException ex) {
            try {
                loadMarkerFromFile(defaultMarker);
            } catch (FileNotFoundException ex1) {
                Logger.getLogger(Checkpoint.class.getName()).log(Level.SEVERE, "Default marker not found.", ex1);
            }
        }
        placeMarker();
    }

    public boolean isCheckLocation(Location loc) {
        for(Location search : checkLocList) {
            if(BukkitUtil.isSameBlock(search,loc)) {
                return true;
            }
        }
        return false;
    }
    
    public void setLabel(String race, String checkpoint) {
        label1 = race;
        label2 = checkpoint;
        refreshLabel();
    }
    
    private void refreshLabel() {
        for(BlockState state: marker) {
            if(state.getType().equals(Material.WALL_SIGN) 
                    || state.getType().equals(Material.SIGN_POST)) {
                Sign sign = (Sign) state.getBlock().getState();
                sign.setLine(1, label1);
                sign.setLine(2, label2);
                sign.update(true, false);
            }
        }
    }
    
    public void setMarker(String name) throws FileNotFoundException {
        if(name != null) {
            markerName = name;
        }
        else {
            markerName = defaultMarker;
        }
        removeMarker();
        loadMarkerFromFile(markerName);
        placeMarker();
    }
    
    public void setLocation(Location loc) {
        removeMarker();
        location = loc;
        placeMarker();
    }
    
    public void delete() {
        removeMarker();
    }
    
    private void placeMarker() {
        if(!restoreDir.exists()) {
            restoreDir.mkdir();
        }
        File restoreFile = new File(restoreDir, name+"."+restoreExt);
        if(restoreFile.exists()) {
            Logger.getLogger(Checkpoint.class.getName()).log(Level.SEVERE, "RestoreFile already exists.");
            return;
        }
        try {
            loadMarkerFromFile(markerName);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Checkpoint.class.getName()).log(Level.SEVERE, "placing Marker, marker file not found.", ex);
        }
        try(FileWriter fw = new FileWriter(restoreFile); 
            PrintWriter writer = new PrintWriter(fw)) {
                writer.println(location.getWorld().getName());
                for(BlockState blockState: marker) {
                    Block block = blockState.getBlock();
                    writer.println(block.getX()+" "+block.getY()+" "+block.getZ()+ " "
                                   + block.getType() + " " + block.getData());
                }
        } catch (IOException ex) {
            Logger.getLogger(Checkpoint.class.getName()).log(Level.SEVERE, "saving restore data", ex);
            return;
        }
        for(BlockState blockState : marker) {
            blockState.update(true, false);
        }
        refreshLabel();
    }
    
    private void removeMarker() {
        File restoreFile = new File(restoreDir, name+"."+restoreExt);
        if(!restoreFile.exists()) {
            Logger.getLogger(Checkpoint.class.getName()).log(Level.SEVERE, "RestoreFile is missing.");
            return;
        }
        for(BlockState state : marker) {
            if(state.getType().equals(Material.SIGN) 
                    || state.getType().equals(Material.SIGN_POST) 
                    || state.getType().equals(Material.WALL_SIGN)) {
                state.setType(Material.AIR);
                state.update(true, false);
            }
        }
        restoreBlocks(restoreFile);
    }
    
    private static void restoreBlocks(File restoreFile) {
        try(FileReader fw = new FileReader(restoreFile); 
            Scanner scanner = new Scanner(fw)) {
                World world = Bukkit.getWorld(scanner.nextLine());
                if(world==null) {
                    Logger.getLogger(Checkpoint.class.getName()).log(Level.SEVERE, "restoring blocks, world not found");
                    return;
                }
                while(scanner.hasNext()) {
                    int x = scanner.nextInt();
                    int y = scanner.nextInt();
                    int z = scanner.nextInt();
                    String str = scanner.next();
                    Material type = Material.getMaterial(str);
                    byte data = scanner.nextByte();
                    scanner.nextLine();
                    Block block = world.getBlockAt(x,y,z);
                    block.setType(type);
                    block.setData(data); 
                }
        } catch (IOException ex) {
            Logger.getLogger(Checkpoint.class.getName()).log(Level.SEVERE, "restoring blocks", ex);
            return;
        }
        restoreFile.delete();
    }
 
    private void loadMarkerFromFile(String markerName) throws FileNotFoundException {
        File file = new File(markerDir,markerName+"."+markerExt);
        if(!file.exists()) {
            throw new FileNotFoundException(markerName+".mkr file not found.");
        }
        try(FileReader fw = new FileReader(file); 
            Scanner scanner = new Scanner(fw)) {
                marker.clear();
                checkLocList.clear();
                while(scanner.hasNext()) {
                    int x = scanner.nextInt();
                    int y = scanner.nextInt();
                    int z = scanner.nextInt();
                    Material type = Material.getMaterial(scanner.next());
                    byte data = scanner.nextByte();
                    scanner.nextLine();
                    if(type.equals(Material.NETHERRACK)) {
                        Location checkLoc = location.clone();
                        checkLoc.setX(checkLoc.getX()+x);
                        checkLoc.setY(checkLoc.getY()+y);
                        checkLoc.setZ(checkLoc.getZ()+z);
                        checkLocList.add(checkLoc);
                    }
                    else {
                        Block block = location.getBlock().getRelative(x,y,z);
                        BlockState state = block.getState();
                        state.setType(type);
                        state.setRawData(data); 
                        marker.add(state);
                    }
                }
        } catch (IOException ex) {
            Logger.getLogger(Checkpoint.class.getName()).log(Level.SEVERE, "Loading marker file.", ex);
        }
    }
    
    public static void saveMarkerToFile(Location loc, String markerName, boolean overwrite) throws IOException  {
        if(!markerDir.exists()) {
            markerDir.mkdir();
        }
        File file = new File(markerDir, markerName+"."+markerExt);
        if(file.exists()) {
            if(!overwrite) {
                Logger.getLogger(Checkpoint.class.getName()).log(Level.SEVERE, "MarkerFile already exisits.");
                throw new FileNotFoundException(markerName+"."+markerExt+" already exists.");
            }
            else {
                file.delete();
            }
        }
        try(FileWriter fw = new FileWriter(file); 
            PrintWriter writer = new PrintWriter(fw)) {
                for(int i = -CheckpointManager.NEAR_DISTANCE;
                        i< CheckpointManager.NEAR_DISTANCE; i++) {
                    for(int j = -CheckpointManager.NEAR_DISTANCE;
                            j< CheckpointManager.NEAR_DISTANCE; j++) {
                        for(int k = -CheckpointManager.NEAR_DISTANCE;
                                k< CheckpointManager.NEAR_DISTANCE; k++) {
                            Block block = loc.getBlock().getRelative(i,j,k);
                            if(!block.isEmpty()) {
                                writer.println(i+" "+j+" "+k+" "
                                               +block.getType()+" "
                                               +block.getData());
                            }
                        }
                    }
                }
        } catch (IOException ex) {
            Logger.getLogger(Checkpoint.class.getName()).log(Level.SEVERE, "in saving marker file.", ex);
        }
    }
    
    public static void cleanup() {
        File[] files = restoreDir.listFiles(FileUtil.getFileExtFilter(restoreExt));
        for(File file: files) {
            restoreBlocks(file);
        }
    }
    
    public static boolean markerExists(String filename) {
        return new File(markerDir,filename+"."+markerExt).exists();
    }
    
    public void addIfNotInMarker(List<Location> list, Location loc) {
        for(Location search: checkLocList) {
            if(BukkitUtil.isSameBlock(loc,search) 
                || BukkitUtil.isSameBlock(loc, search.getBlock()
                                                     .getRelative(0,1,0).getLocation())) {
                return;
            }
        }
        for(BlockState search: marker) {
            if(BukkitUtil.isSameBlock(loc,search.getLocation())) {
                return;
            }
        }
        for(Location search: list) {
            if(BukkitUtil.isSameBlock(loc,search)) {
                return;
            }
        }
        list.add(loc);
    }
}

