package eu.darkcube.minigame.woolbattle.map;

import java.util.Collection;

public interface MapManager {

	Map getMap(String name);
	Map createMap(String name);
	Collection<? extends Map> getMaps();
	void deleteMap(Map map);
	void saveMaps();
	
}