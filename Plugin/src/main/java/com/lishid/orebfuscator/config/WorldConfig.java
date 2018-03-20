/*
 * @author Aleksey Terzi
 *
 */

package com.lishid.orebfuscator.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class WorldConfig {
	private String name;
    private Boolean enabled;
    private Boolean darknessHideBlocks;
    private Boolean antiTexturePackAndFreecam;
    private Boolean bypassObfuscationForSignsWithText;
    private Integer airGeneratorMaxChance;    
    private boolean[] obfuscateBlocks;
    private boolean[] obfuscateAndProximityBlocks;
    private boolean[] darknessBlocks;
    private Integer[] randomBlocks;
    private Integer[] randomBlocks2;
    private Integer mode1BlockId;
    private int[] paletteBlocks;
    private ProximityHiderConfig proximityHiderConfig;
    private boolean initialized;
    
    public WorldConfig() {
    	this.proximityHiderConfig = new ProximityHiderConfig();
    }
    
    public void setDefaults() {
		this.enabled = true;
		this.darknessHideBlocks = false;
		this.antiTexturePackAndFreecam = true;
		this.bypassObfuscationForSignsWithText = false;
		this.airGeneratorMaxChance = 43;
		this.obfuscateBlocks = new boolean[256];
		
		this.darknessBlocks = new boolean[256];
		this.darknessBlocks[52] = true;
		this.darknessBlocks[54] = true;
		
		this.randomBlocks = new Integer[0];
		this.randomBlocks2 = this.randomBlocks;
		
	    this.mode1BlockId = 1;
	    this.paletteBlocks = null;

	    this.proximityHiderConfig.setDefaults();
    }
    
    public void init(WorldConfig baseWorld) {
    	if(this.initialized) {
    		return;
    	}
    	
    	if(baseWorld != null) {
	    	if(this.enabled == null) {
	    		this.enabled = baseWorld.enabled;
	    	}
	        
	    	if(this.darknessHideBlocks == null) {
	    		this.darknessHideBlocks = baseWorld.darknessHideBlocks;
	    	}
	    	
	    	if(this.antiTexturePackAndFreecam == null) {
	    		this.antiTexturePackAndFreecam = baseWorld.antiTexturePackAndFreecam;
	    	}
	    	
	    	if(this.bypassObfuscationForSignsWithText == null) {
	    		this.bypassObfuscationForSignsWithText = baseWorld.bypassObfuscationForSignsWithText;
	    	}

	    	if(this.airGeneratorMaxChance == null) {
	    		this.airGeneratorMaxChance = baseWorld.airGeneratorMaxChance;
	    	}
	    	
	    	if(this.obfuscateBlocks == null) {
	    		this.obfuscateBlocks = baseWorld.obfuscateBlocks != null ? baseWorld.obfuscateBlocks.clone(): null;
	    	}
	    	
	    	if(this.darknessBlocks == null) {
	    		this.darknessBlocks = baseWorld.darknessBlocks != null ? baseWorld.darknessBlocks.clone(): null;
	    	}
	    	
	    	if(this.randomBlocks == null) {
		        this.randomBlocks = baseWorld.randomBlocks != null ? baseWorld.randomBlocks.clone(): null;
		        this.randomBlocks2 = baseWorld.randomBlocks2 != null ? baseWorld.randomBlocks2.clone(): null;
	    	}
	    	
	    	if(this.mode1BlockId == null) {
	    		this.mode1BlockId = baseWorld.mode1BlockId;
	    	}
	    	
	  		this.proximityHiderConfig.init(baseWorld.proximityHiderConfig);
	        setObfuscateAndProximityBlocks();
    	}
        
        setPaletteBlocks();
        
        this.initialized = true;
    }
    
    public boolean isInitialized() {
    	return this.initialized;
    }

    public String getName() {
    	return this.name;
	}

	public void setName(String value) {
    	this.name = value;
	}
    
    public Boolean isEnabled() {
    	return this.enabled;
    }
    
    public void setEnabled(Boolean value) {
    	this.enabled = value;
    }
    
    public Boolean isDarknessHideBlocks() {
    	return this.darknessHideBlocks;
    }
    
    public void setDarknessHideBlocks(Boolean value) {
    	this.darknessHideBlocks = value;
    }

    public Boolean isAntiTexturePackAndFreecam() {
    	return this.antiTexturePackAndFreecam;
    }
    
    public void setAntiTexturePackAndFreecam(Boolean value) {
    	this.antiTexturePackAndFreecam = value;
    }

    public Boolean isBypassObfuscationForSignsWithText() {
    	return this.bypassObfuscationForSignsWithText;
    }
    
    public void setBypassObfuscationForSignsWithText(Boolean value) {
    	this.bypassObfuscationForSignsWithText = value;
    }

    public Integer getAirGeneratorMaxChance() {
    	return this.airGeneratorMaxChance;
    }
    
    public void setAirGeneratorMaxChance(Integer value) {
    	this.airGeneratorMaxChance = value;
    }
    
    public boolean[] getObfuscateBlocks() {
    	return this.obfuscateBlocks;
    }
    
    public void setObfuscateBlocks(boolean[] values) {
    	this.obfuscateBlocks = values;
    }
    
    public Integer[] getObfuscateBlockIds() {
    	if(this.obfuscateBlocks == null) {
    		return null;
    	}
    	
    	List<Integer> result = new ArrayList<>();
    	
    	for(int i = 0; i < this.obfuscateBlocks.length; i++) {
    		if(this.obfuscateBlocks[i]) {
    			result.add(i);
    		}
    	}
    	
    	return result.toArray(new Integer[0]);
    }
    
    private void setObfuscateAndProximityBlocks() {
    	this.obfuscateAndProximityBlocks = new boolean[256];
    	
    	boolean isProximityHiderEnabled = this.proximityHiderConfig != null && this.proximityHiderConfig.isEnabled();
    	int[] proximityHiderBlocks = isProximityHiderEnabled ? this.proximityHiderConfig.getProximityHiderBlockMatrix(): null;
    	
    	for(int i = 0; i < this.obfuscateAndProximityBlocks.length; i++) {
    		this.obfuscateAndProximityBlocks[i] =
    				this.obfuscateBlocks[i]
    				|| isProximityHiderEnabled && proximityHiderBlocks[i] != 0
    				;
    	}
    }
    
    public boolean[] getObfuscateAndProximityBlocks() {
    	return this.obfuscateAndProximityBlocks;
    }
    
    public boolean[] getDarknessBlocks() {
    	return this.darknessBlocks;
    }
    
    public void setDarknessBlocks(boolean[] values) {
    	this.darknessBlocks = values;
    }

    public Integer[] getDarknessBlockIds() {
    	if(this.darknessBlocks == null) {
    		return null;
    	}
    	
    	List<Integer> result = new ArrayList<>();
    	
    	for(int i = 0; i < this.darknessBlocks.length; i++) {
    		if(this.darknessBlocks[i]) {
    			result.add(i);
    		}
    	}
    	
    	return result.toArray(new Integer[0]);
    }

    public Integer[] getRandomBlocks() {
    	return this.randomBlocks;
    }

    public void setRandomBlocks(Integer[] values) {
    	this.randomBlocks = values;
    	this.randomBlocks2 = values;
    }
    
    public void shuffleRandomBlocks() {
        synchronized (this.randomBlocks) {
            Collections.shuffle(Arrays.asList(this.randomBlocks));
            Collections.shuffle(Arrays.asList(this.randomBlocks2));
        }
    }
    
    public Integer getMode1BlockId() {
    	return this.mode1BlockId;
    }

    public void setMode1BlockId(Integer value) {
    	this.mode1BlockId = value;
    }

    public int[] getPaletteBlocks() {
    	return this.paletteBlocks;
    }
    
    private void setPaletteBlocks() {
    	if(this.randomBlocks == null) {
    		return;
    	}
    	
    	HashSet<Integer> map = new HashSet<>();
    	
    	map.add(0);
    	map.add(this.mode1BlockId);
    	
    	if(this.proximityHiderConfig.isUseSpecialBlock()) {
    		map.add(this.proximityHiderConfig.getSpecialBlockID());
    	}
    	
    	for(Integer id : this.randomBlocks) {
    		if(id != null) {
    			map.add(id);
    		}
    	}
    	
    	int[] paletteBlocks = new int[map.size()];
    	int index = 0;
    	
    	for(Integer id : map) {
    		paletteBlocks[index++] = id;
    	}
    	
    	this.paletteBlocks = paletteBlocks;
    }

    public ProximityHiderConfig getProximityHiderConfig() {
    	return this.proximityHiderConfig;
    }
    
    // Helper methods
    
    public boolean isObfuscated(int id) {
        if (id < 0)
            id += 256;
        
        return this.obfuscateAndProximityBlocks[id];
    }

    public boolean isDarknessObfuscated(int id) {
        if (id < 0)
            id += 256;

        return this.darknessBlocks[id];
    }

    public int getRandomBlock(int index, boolean alternate) {
        return (int)(alternate ? this.randomBlocks2[index] : this.randomBlocks[index]);
    }
}
