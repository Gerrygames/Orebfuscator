/*
 * @author Aleksey Terzi
 *
 */

package com.lishid.orebfuscator.config;


public class ProximityHiderConfig {
	public static class BlockSetting implements Cloneable {
		public int blockId;
		public int y;
		public boolean obfuscateAboveY;
		
		public BlockSetting clone() throws CloneNotSupportedException {
			BlockSetting clone = new BlockSetting();
			clone.blockId = this.blockId;
			clone.y = this.y;
			clone.obfuscateAboveY = this.obfuscateAboveY;
			
			return clone;
		}
	}
	
	private static final Integer[] defaultProximityHiderBlockIds = new Integer[]{ 23, 52, 54, 56, 58, 61, 62, 116, 129, 130, 145, 146 };
	
    private Boolean enabled;
    private Integer distance;
    private int distanceSquared;
    private Integer specialBlockID;
    private Integer y;
    private Boolean useSpecialBlock;
    private Boolean obfuscateAboveY;
    private Boolean useFastGazeCheck;
    private Integer[] proximityHiderBlockIds;
    private BlockSetting[] proximityHiderBlockSettings;
    private int[] proximityHiderBlockMatrix;
    
    public void setDefaults() {
        this.enabled = true;
        this.distance = 8;
        this.distanceSquared = this.distance * this.distance;
        this.specialBlockID = 1;
        this.y = 255;
        this.useSpecialBlock = true;
        this.obfuscateAboveY = false;
        this.useFastGazeCheck = true;
        this.proximityHiderBlockIds = defaultProximityHiderBlockIds;
    }
    
    public void init(ProximityHiderConfig baseCfg) {
    	if(this.enabled == null) {
    		this.enabled = baseCfg.enabled;
    	}
    	
    	if(this.distance == null) {
    		this.distance = baseCfg.distance;
    		this.distanceSquared = baseCfg.distanceSquared;
    	}
    	
    	if(this.specialBlockID == null) {
    		this.specialBlockID = baseCfg.specialBlockID;
    	}
    	
    	if(this.y == null) {
    		this.y = baseCfg.y;
    	}
    	
        if(this.useSpecialBlock == null) {
        	this.useSpecialBlock = baseCfg.useSpecialBlock;
        }
        
        if(this.obfuscateAboveY == null) {
        	this.obfuscateAboveY = baseCfg.obfuscateAboveY;
        }
        
        if(this.proximityHiderBlockIds == null && baseCfg.proximityHiderBlockIds != null) {
        	this.proximityHiderBlockIds = baseCfg.proximityHiderBlockIds.clone();
        }
        
        if(this.proximityHiderBlockSettings == null && baseCfg.proximityHiderBlockSettings != null) {
        	this.proximityHiderBlockSettings = baseCfg.proximityHiderBlockSettings.clone();
        }
        
        if (this.useFastGazeCheck == null) {
        	this.useFastGazeCheck = baseCfg.useFastGazeCheck;
        }
        
        setProximityHiderBlockMatrix();
    }
    
    public Boolean isEnabled() {
    	return this.enabled;
    }
    
    public void setEnabled(Boolean value) {
    	this.enabled = value;
    }
    
    public Integer getDistance() {
    	return this.distance;
    }
    
    public void setDistance(Integer value) {
    	this.distance = value;
    	this.distanceSquared = this.distance != null ? this.distance * this.distance: 0;
    }
    
    public int getDistanceSquared() {
    	return this.distanceSquared;
    }

    public Integer getSpecialBlockID() {
    	return this.specialBlockID;
    }
    
    public void setSpecialBlockID(Integer value) {
    	this.specialBlockID = value;
    }
    
    public Integer getY() {
    	return this.y;
    }
    
    public void setY(Integer value) {
    	this.y = value;
    }
    
    public Boolean isUseSpecialBlock() {
    	return this.useSpecialBlock;
    }
    
    public void setUseSpecialBlock(Boolean value) {
    	this.useSpecialBlock = value;
    }
    
    public Boolean isObfuscateAboveY() {
    	return this.obfuscateAboveY;
    }
    
    public void setObfuscateAboveY(Boolean value) {
    	this.obfuscateAboveY = value;
    }
    
    public void setProximityHiderBlockIds(Integer[] value) {
    	this.proximityHiderBlockIds = value;
    }
    
    public Integer[] getProximityHiderBlockIds() {
    	return this.proximityHiderBlockIds;
    }
    
    public BlockSetting[] getProximityHiderBlockSettings() {
    	return this.proximityHiderBlockSettings;
    }
    
    public void setProximityHiderBlockSettings(BlockSetting[] value) {
    	this.proximityHiderBlockSettings = value;
    }
    
    public int[] getProximityHiderBlockMatrix() {
    	return this.proximityHiderBlockMatrix;
    }
    
    private void setProximityHiderBlockMatrix() {
    	this.proximityHiderBlockMatrix = new int[256];
    	
    	if(this.proximityHiderBlockIds != null) {
    		for(int blockId : this.proximityHiderBlockIds) {
    			this.proximityHiderBlockMatrix[blockId] = this.obfuscateAboveY ? -this.y: this.y;
    		}
    	}
    	
    	if(this.proximityHiderBlockSettings != null) {
    		for(BlockSetting block : this.proximityHiderBlockSettings) {
    			this.proximityHiderBlockMatrix[block.blockId] = block.obfuscateAboveY ? -block.y: block.y;
    		}
    	}
    }
    
    public Boolean isUseFastGazeCheck() {
    	return this.useFastGazeCheck;
    }
    
    public void setUseFastGazeCheck(Boolean value) {
    	this.useFastGazeCheck = value;
    }

    // Help methods
    
    public boolean isProximityObfuscated(int y, int id) {
        if (id < 0)
            id += 256;
        
        int proximityY = this.proximityHiderBlockMatrix[id];
        
        if(proximityY == 0) {
        	return false;
        }
        
        if(proximityY > 0) {
        	return y <= proximityY;
        }
        
        return y >= (proximityY & 0x7FFFFFFF);
    }
}
