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
import com.mcmiddleearth.minigames.utils.EntityUtil;
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
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;

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
    private static final String restoreEntityExt = "ent";
    
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
            if(loc.distance(search)<2) {//BukkitUtil.isSameBlock(search,loc)) {
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
        //List<Entity> entities = new ArrayList<>();
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
        saveEntities(new File(restoreDir, name+"_frame."+restoreEntityExt), ItemFrame.class);
        saveEntities(new File(restoreDir, name+"_painting."+restoreEntityExt), Painting.class);
        saveEntities(new File(restoreDir, name+"_armor."+restoreEntityExt), ArmorStand.class);
        for(BlockState blockState : marker) {
            if(!blockState.getType().equals(Material.SIGN) 
                    && !blockState.getType().equals(Material.SIGN_POST) 
                    && !blockState.getType().equals(Material.WALL_SIGN)
                    && !blockState.getType().equals(Material.TORCH)) {
                blockState.update(true, false);
            }
        }
        for(BlockState blockState : marker) {
            if(blockState.getType().equals(Material.SIGN) 
                    || blockState.getType().equals(Material.SIGN_POST) 
                    || blockState.getType().equals(Material.WALL_SIGN)
                    || blockState.getType().equals(Material.TORCH)) {
                blockState.update(true, false);
            }
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
                    || state.getType().equals(Material.WALL_SIGN)
                    || state.getType().equals(Material.TORCH)) {
                state.setType(Material.AIR);
                state.update(true, false);
            }
        }
        restoreBlocks(restoreFile);
        restoreFile = new File(restoreDir, name+"."+restoreEntityExt);
        if(!restoreFile.exists()) {
            Logger.getLogger(Checkpoint.class.getName()).log(Level.SEVERE, "RestoreFile for entities is missing.");
            return;
        }
        restoreEntities(new File(restoreDir, name+"_painting."+restoreEntityExt));
        restoreEntities(new File(restoreDir, name+"_frame."+restoreEntityExt));
        restoreEntities(new File(restoreDir, name+"_armor."+restoreEntityExt));
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
    
    private static void restoreEntities(File restoreFile) {
        try {
            EntityUtil.restore(restoreFile, new ArrayList<Entity>());
        } catch (IOException | InvalidConfigurationException ex) {
            Logger.getLogger(Checkpoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void addNewEntity(List<Entity> entityList, Entity newEntity) {
        for(Entity search: entityList) {
            if(newEntity.getUniqueId().equals(search.getUniqueId())) {
                return;
            }
        }
        entityList.add(newEntity);
    }
    
    private void saveEntities(File restoreFile, Class entityClass) {
        List<Entity> entityList = new ArrayList<>();
        List<Entity> nearEntities = new ArrayList<>();
        entityList.addAll(location.getWorld().getEntitiesByClass(entityClass));
        for(Entity entity: entityList) {
            for(BlockState markerBlock: marker) {
                if(entity.getLocation().distance(markerBlock.getLocation())<2.1) {
                    addNewEntity(nearEntities,entity);
                }
            }
        }
        try {
            EntityUtil.store(restoreFile, nearEntities);
            for(Entity entity: nearEntities) {
                entity.remove();
            }
        } catch (IOException ex) {
            Logger.getLogger(Checkpoint.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
 
    private void loadMarkerFromFile(String markerName) throws FileNotFoundException {
        File file = new File(markerDir,markerName+"."+markerExt);
        if(!file.exists()) {
            marker.clear();
            checkLocList.clear();
            checkLocList.add(location);
            throw new FileNotFoundException(markerName+".mkr file not found.");
        }
        try {
            FileReader fw = new FileReader(file);
            Scanner scanner = new Scanner(fw);
                marker.clear();
                checkLocList.clear();
                double yaw = readYaw(scanner);
                BlockRotation rotation = getRotation(yaw);
                if(rotation.isDiagonal()) {
                    file = new File(markerDir,markerName+"_d."+markerExt);
                    if(file.exists()) {
                        marker.clear();
                        scanner.close();
                        fw.close();
                        fw = new FileReader(file); 
                        scanner = new Scanner(fw);
                        double diagonalYaw = readYaw(scanner);
                        rotation = getRotation(diagonalYaw);
                    }
                }
                if(rotation.isDiagonal()) {
                    rotation = rotation.subtractRotation(BlockRotation.HALF_RIGHT);
                }
                while(scanner.hasNext()) {
                    int x = scanner.nextInt();
                    int y = scanner.nextInt();
                    int z = scanner.nextInt();
                    Material type = Material.getMaterial(scanner.next());
                    byte data = scanner.nextByte();
                    scanner.nextLine();
                    if(type.equals(Material.NETHERRACK)) {
                        switch(rotation) {
                            case RIGHT: 
                                addCheckIfAir(-z,y,x);
                                break;
                            case TURN_AROUND:
                                addCheckIfAir(-x,y,-z);
                                break;
                            case LEFT:
                                addCheckIfAir(z,y,-x);
                                break;
                            default:
                                addCheckIfAir(x,y,z);
                                break;
                        }
                    }
                    else if(!type.equals(Material.PRISMARINE)){
                        Block block;
                        switch(rotation) {
                            case RIGHT: 
                                block = location.getBlock().getRelative(-z,y,x);
                                break;
                            case TURN_AROUND:
                                block = location.getBlock().getRelative(-x,y,-z);
                                break;
                            case LEFT:
                                block = location.getBlock().getRelative(z,y,-x);
                                break;
                            default:
                                block = location.getBlock().getRelative(x,y,z);
                                break;
                        }
                        if(type.equals(Material.WALL_SIGN)) {
                            data = adaptData(data, rotation, new byte[]{3,4,2,5});
                        }
                        else if(type.equals(Material.TORCH)) {
                            data = adaptData(data, rotation, new byte[]{3,2,4,1});
                        }
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
    
    private void addCheckIfAir(double x, double y, double z) {
        Location loc = new Location(location.getWorld(), location.getX()+x,
                                                 location.getY()+y,
                                                 location.getZ()+z,
                                                 location.getYaw(),
                                                 location.getPitch());
        if(loc.getBlock().isEmpty()) {
            checkLocList.add(loc);
        }
    }
    
    private static double readYaw(Scanner scanner) {
        if(!scanner.hasNextInt()) {
            scanner.next();
            String str = scanner.nextLine();
            return Double.parseDouble(str);
        }
        return 0;
    }
    
    private byte adaptData(byte data, BlockRotation rotation, byte[] dataValues) {
        int dataIndex=-1;
        for(int i=0; i<4 ;i++) {
            if(dataValues[i]==data) {
            dataIndex = i;
            break;
            }
        }
        if(dataIndex == -1) {
            return data;
        }
        switch(rotation) {
            case RIGHT: 
                dataIndex++;
                break;
            case TURN_AROUND:
                dataIndex+=2;
                break;
            case LEFT:
                dataIndex+=3;
                break;
            default:
        }
        if(dataIndex>3) {
            dataIndex-=4;
        }
        return dataValues[dataIndex];
    }
    
    private BlockRotation getRotation(double savedYaw) {
        double rotation = location.getYaw()-savedYaw;
        return BlockRotation.getBlockRotation(rotation);
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
                writer.println("YAW "+loc.getYaw());
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
        if(restoreDir.exists()) {
            File[] files = restoreDir.listFiles(FileUtil.getFileExtFilter(restoreExt));
            for(File file: files) {
                restoreBlocks(file);
            }
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

