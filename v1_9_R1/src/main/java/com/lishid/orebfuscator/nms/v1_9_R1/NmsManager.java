/*
 * @author lishid
 * @author Aleksey Terzi
 *
 */

package com.lishid.orebfuscator.nms.v1_9_R1;

import net.minecraft.server.v1_9_R1.Block;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.Chunk;
import net.minecraft.server.v1_9_R1.ChunkProviderServer;
import net.minecraft.server.v1_9_R1.IBlockData;
import net.minecraft.server.v1_9_R1.IChatBaseComponent;
import net.minecraft.server.v1_9_R1.Packet;
import net.minecraft.server.v1_9_R1.TileEntity;
import net.minecraft.server.v1_9_R1.WorldServer;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;

import com.lishid.orebfuscator.nms.IBlockInfo;
import com.lishid.orebfuscator.nms.IChunkCache;
import com.lishid.orebfuscator.nms.INBT;
import com.lishid.orebfuscator.nms.INmsManager;
import com.lishid.orebfuscator.types.BlockCoord;
import com.lishid.orebfuscator.types.BlockState;

public class NmsManager implements INmsManager {
	private int maxLoadedCacheFiles;
	
	public void setMaxLoadedCacheFiles(int value) {
		this.maxLoadedCacheFiles = value;
	}
	
	public INBT createNBT() {
		return new NBT();
	}
	
	public IChunkCache createChunkCache() {
		return new ChunkCache(this.maxLoadedCacheFiles);
	}
	
    public void updateBlockTileEntity(BlockCoord blockCoord, Player player) {
        CraftWorld world = (CraftWorld)player.getWorld();
        TileEntity tileEntity = world.getTileEntityAt(blockCoord.x, blockCoord.y, blockCoord.z);
        
        if (tileEntity == null) {
            return;
        }
        
        Packet<?> packet = tileEntity.getUpdatePacket();
        
        if (packet != null) {
            CraftPlayer player2 = (CraftPlayer)player;
            player2.getHandle().playerConnection.sendPacket(packet);
        }
    }

    public void notifyBlockChange(World world, IBlockInfo blockInfo) {
    	BlockPosition blockPosition = new BlockPosition(blockInfo.getX(), blockInfo.getY(), blockInfo.getZ());
    	IBlockData blockData = ((BlockInfo)blockInfo).getBlockData();
    	
        ((CraftWorld)world).getHandle().notify(blockPosition, blockData, blockData, 0);
    }
    
    public int getBlockLightLevel(World world, int x, int y, int z) {
		return ((CraftWorld)world).getHandle().getLightLevel(new BlockPosition(x, y, z));
    }
    
	public IBlockInfo getBlockInfo(World world, int x, int y, int z) {
		IBlockData blockData = getBlockData(world, x, y, z, false);
		
		return blockData != null
				? new BlockInfo(x, y, z, blockData)
				: null;
	}
	
	public BlockState getBlockState(World world, int x, int y, int z) {
		IBlockData blockData = getBlockData(world, x, y, z, false);
		
		if(blockData == null) return null;
		
		Block block = blockData.getBlock();
		
		BlockState blockState = new BlockState();
		blockState.id = Block.getId(block);
		blockState.meta = block.toLegacyData(blockData);
		
		return blockState;
	}
	
	public int getBlockId(World world, int x, int y, int z) {
		IBlockData blockData = getBlockData(world, x, y, z, false);
		
		return blockData != null ? Block.getId(blockData.getBlock()): -1;
	}

	public int loadChunkAndGetBlockId(World world, int x, int y, int z) {
		IBlockData blockData = getBlockData(world, x, y, z, true);
		return blockData != null ? Block.getId(blockData.getBlock()): -1;
	}
	
	public String getTextFromChatComponent(String json) {
		IChatBaseComponent component = IChatBaseComponent.ChatSerializer.a(json);
		return CraftChatMessage.fromComponent(component);
	}

	private IBlockData getBlockData(World world, int x, int y, int z, boolean loadChunk) {
		int chunkX = x >> 4;
		int chunkZ = z >> 4;

		WorldServer worldServer = ((CraftWorld)world).getHandle();
		ChunkProviderServer chunkProviderServer = worldServer.getChunkProviderServer();

		if(!loadChunk && !chunkProviderServer.isChunkLoaded(chunkX, chunkZ)) return null;

		Chunk chunk = chunkProviderServer.getOrLoadChunkAt(chunkX, chunkZ);

		return chunk != null ? chunk.getBlockData(new BlockPosition(x, y, z)) : null;
	}
}