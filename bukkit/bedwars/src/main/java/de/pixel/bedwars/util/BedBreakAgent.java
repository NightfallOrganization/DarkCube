package de.pixel.bedwars.util;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import de.pixel.bedwars.listener.IngameBlock;
import de.pixel.bedwars.util.BlockAgent.Limit;

public class BedBreakAgent {

	public static void execute(Location loc) {
		execute(scan(loc));
	}
	
	public static void execute(Set<Block> blocks) {
		for(Block b : blocks) {
			IngameBlock.placed.put(b, b.getState());
			b.setType(Material.AIR);
		}
	}
	
	public static Set<Block> scan(Location loc) {
		if(loc == null)
			return new HashSet<>();
		return scan(loc.getBlock());
	}
	
	public static Set<Block> scan(Block block) {
		BlockAgent b = new BlockAgent(block);
		b.setUseData(false);
		return b.scan(Limit.of(10));
	}
}
