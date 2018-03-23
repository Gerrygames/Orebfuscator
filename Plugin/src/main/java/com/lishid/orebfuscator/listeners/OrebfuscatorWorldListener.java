package com.lishid.orebfuscator.listeners;

import com.lishid.orebfuscator.Orebfuscator;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

public class OrebfuscatorWorldListener implements Listener {

	public OrebfuscatorWorldListener() {
		Bukkit.getWorlds().forEach(Orebfuscator.nms::patchPlayerChunkMap);
	}

	@EventHandler
	public void onWorldInit(WorldInitEvent e) {
		Orebfuscator.nms.patchPlayerChunkMap(e.getWorld());
	}
}
