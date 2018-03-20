/*
 * Copyright (C) 2011-2014 lishid.  All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation,  version 3.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.lishid.orebfuscator.obfuscation;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.lishid.orebfuscator.DeprecatedMethods;
import com.lishid.orebfuscator.Orebfuscator;
import com.lishid.orebfuscator.cache.ObfuscatedCachedChunk;
import com.lishid.orebfuscator.config.WorldConfig;
import com.lishid.orebfuscator.nms.IBlockInfo;
import com.lishid.orebfuscator.types.ChunkCoord;

public class BlockUpdate {
    public static boolean needsUpdate(Block block) {
        return !Orebfuscator.config.isBlockTransparent(DeprecatedMethods.getTypeId(block));
    }

    public static void update(Block block) {
        if (!needsUpdate(block)) {
            return;
        }

        update(Arrays.asList(new Block[]{block}));
    }
    
    public static void update(List<Block> blocks) {
        if (blocks.isEmpty()) {
            return;
        }

        World world = blocks.get(0).getWorld();
        WorldConfig worldConfig = Orebfuscator.configManager.getWorld(world);
        HashSet<IBlockInfo> updateBlocks = new HashSet<>();
    	HashSet<ChunkCoord> invalidChunks = new HashSet<>();
    	int updateRadius = Orebfuscator.config.getUpdateRadius();
        
        for (Block block : blocks) {
            if (needsUpdate(block)) {
            	IBlockInfo blockInfo = Orebfuscator.nms.getBlockInfo(world, block.getX(), block.getY(), block.getZ());

           		getAjacentBlocks(updateBlocks, world, worldConfig, blockInfo, updateRadius);
            	
                if((blockInfo.getX() & 0xf) == 0) {
                	invalidChunks.add(new ChunkCoord((blockInfo.getX() >> 4) - 1, blockInfo.getZ() >> 4)); 
                } else if(((blockInfo.getX() + 1) & 0xf) == 0) {
                	invalidChunks.add(new ChunkCoord((blockInfo.getX() >> 4) + 1, blockInfo.getZ() >> 4));
    	        } else if(((blockInfo.getZ()) & 0xf) == 0) {
    	        	invalidChunks.add(new ChunkCoord(blockInfo.getX() >> 4, (blockInfo.getZ() >> 4) - 1));
    		    } else if(((blockInfo.getZ() + 1) & 0xf) == 0) {
    		    	invalidChunks.add(new ChunkCoord(blockInfo.getX() >> 4, (blockInfo.getZ() >> 4) + 1));
    		    }
            }
        }

        sendUpdates(world, updateBlocks);
        
        invalidateCachedChunks(world, invalidChunks);
    }
    
    //This method is used in CastleGates plugin
    public static void updateByLocations(List<Location> locations, int updateRadius) {
        if (locations.isEmpty()) {
            return;
        }

        World world = locations.get(0).getWorld();
        WorldConfig worldConfig = Orebfuscator.configManager.getWorld(world);
        HashSet<IBlockInfo> updateBlocks = new HashSet<>();
    	HashSet<ChunkCoord> invalidChunks = new HashSet<>();
        
        for (Location location : locations) {
        	IBlockInfo blockInfo = Orebfuscator.nms.getBlockInfo(world, location.getBlockX(), location.getBlockY(), location.getBlockZ());

       		getAjacentBlocks(updateBlocks, world, worldConfig, blockInfo, updateRadius);
        	
            if((blockInfo.getX() & 0xf) == 0) {
            	invalidChunks.add(new ChunkCoord((blockInfo.getX() >> 4) - 1, blockInfo.getZ() >> 4)); 
            } else if(((blockInfo.getX() + 1) & 0xf) == 0) {
            	invalidChunks.add(new ChunkCoord((blockInfo.getX() >> 4) + 1, blockInfo.getZ() >> 4));
	        } else if(((blockInfo.getZ()) & 0xf) == 0) {
	        	invalidChunks.add(new ChunkCoord(blockInfo.getX() >> 4, (blockInfo.getZ() >> 4) - 1));
		    } else if(((blockInfo.getZ() + 1) & 0xf) == 0) {
		    	invalidChunks.add(new ChunkCoord(blockInfo.getX() >> 4, (blockInfo.getZ() >> 4) + 1));
		    }
        }

        sendUpdates(world, updateBlocks);
        
        invalidateCachedChunks(world, invalidChunks);
    }

    private static void sendUpdates(World world, Set<IBlockInfo> blocks) {
        //Orebfuscator.log("Notify block change for " + blocks.size() + " blocks");/*debug*/

        for (IBlockInfo blockInfo : blocks) {
            Orebfuscator.nms.notifyBlockChange(world, blockInfo);
        }
    }
    
    private static void invalidateCachedChunks(World world, Set<ChunkCoord> invalidChunks) {
    	if(invalidChunks.isEmpty() || !Orebfuscator.config.isUseCache()) return;
    	
        File cacheFolder = new File(world.getWorldFolder(), "cache");

        for(ChunkCoord chunk : invalidChunks) {
            ObfuscatedCachedChunk cache = new ObfuscatedCachedChunk(cacheFolder, chunk.x, chunk.z);
            cache.invalidate();
            
            //Orebfuscator.log("Chunk x = " + chunk.x + ", z = " + chunk.z + " is invalidated");/*debug*/
        }
    }

    private static void getAjacentBlocks(
    		HashSet<IBlockInfo> allBlocks,
    		World world,
    		WorldConfig worldConfig,
    		IBlockInfo blockInfo,
    		int countdown
    		)
    {
        if (blockInfo == null) return;
        
        int blockId = blockInfo.getTypeId();

        if ((worldConfig.isObfuscated(blockId) || worldConfig.isDarknessObfuscated(blockId))) {
            allBlocks.add(blockInfo);
        }

        if (countdown > 0) {
            countdown--;
            getAjacentBlocks(allBlocks, world, worldConfig, Orebfuscator.nms.getBlockInfo(world, blockInfo.getX() + 1, blockInfo.getY(), blockInfo.getZ()), countdown);
            getAjacentBlocks(allBlocks, world, worldConfig, Orebfuscator.nms.getBlockInfo(world, blockInfo.getX() - 1, blockInfo.getY(), blockInfo.getZ()), countdown);
            getAjacentBlocks(allBlocks, world, worldConfig, Orebfuscator.nms.getBlockInfo(world, blockInfo.getX(), blockInfo.getY() + 1, blockInfo.getZ()), countdown);
            getAjacentBlocks(allBlocks, world, worldConfig, Orebfuscator.nms.getBlockInfo(world, blockInfo.getX(), blockInfo.getY() - 1, blockInfo.getZ()), countdown);
            getAjacentBlocks(allBlocks, world, worldConfig, Orebfuscator.nms.getBlockInfo(world, blockInfo.getX(), blockInfo.getY(), blockInfo.getZ() + 1), countdown);
            getAjacentBlocks(allBlocks, world, worldConfig, Orebfuscator.nms.getBlockInfo(world, blockInfo.getX(), blockInfo.getY(), blockInfo.getZ() - 1), countdown);
        }
    }
}
