/*
 * Copyright (C) 2016 MCME
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

import lombok.Getter;

/**
 *
 * @author Eriol_Eandur
 */
public enum BlockRotation {
    STRAIGHT   (0),
    HALF_LEFT  (-45),
    LEFT       (-90),
    SHARP_LEFT (-135),
    HALF_RIGHT  (45),
    RIGHT       (90),
    SHARP_RIGHT (135),
    TURN_AROUND (180);
    
    @Getter
    private final int yaw;

    private BlockRotation(int yaw) {
        this.yaw = yaw;
    }
    
    public static BlockRotation getBlockRotation(double yaw) {
        while(yaw < -180) {
            yaw += 360;
        }
        while(yaw > 180) {
            yaw -= 360;
        }
        if(yaw<-157.5 || yaw>157.5) {
            return TURN_AROUND;
        } 
        else if(yaw < -112.5) {
            return SHARP_LEFT;
        } 
        else if(yaw > 112.5) {
            return SHARP_RIGHT;
        }
        else if(yaw < -67.5) {
            return LEFT;
        } 
        else if(yaw > 67.5) {
            return RIGHT;
        }
        else if(yaw < -22.5) {
            return HALF_LEFT;
        } 
        else if(yaw > 22.5) {
            return HALF_RIGHT;
        }
        else {
            return STRAIGHT;
        }
    }
    
    public boolean isDiagonal() {
        switch(this) {
            case HALF_LEFT:
            case HALF_RIGHT:
            case SHARP_LEFT:
            case SHARP_RIGHT:
                return true;
        }
        return false;
    }
    
    public BlockRotation subtractRotation(BlockRotation rotation) {
        return getBlockRotation(yaw - rotation.yaw);
    }
    
    @Override
    public String toString() {
        return ""+yaw;
    }
}
