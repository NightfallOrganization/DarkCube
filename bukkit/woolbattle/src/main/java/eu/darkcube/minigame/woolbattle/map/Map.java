package eu.darkcube.minigame.woolbattle.map;

import org.bukkit.Location;

import eu.darkcube.minigame.woolbattle.util.MaterialAndId;

public interface Map {

	boolean isEnabled();
	int getDeathHeight();
	MaterialAndId getIcon();
	
	void setDeathHeight(int height);
	void setIcon(MaterialAndId icon);
	
	void enable();
	void disable();
	void delete();
	
	void setSpawn(String name, Location loc);
	Location getSpawn(String name);
	
	String getName();
	
	String serialize();
}
