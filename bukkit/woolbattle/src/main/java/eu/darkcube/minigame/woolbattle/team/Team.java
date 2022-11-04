package eu.darkcube.minigame.woolbattle.team;

import java.util.Collection;
import java.util.UUID;

import org.bukkit.Location;

import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.user.User;

public interface Team extends Comparable<Team> {

	UUID getUniqueId();

	boolean isSpectator();

	boolean canPlay();

	String getName(User user);

	String getPrefix();
	
	TeamType getType();

	Collection<? extends User> getUsers();

	boolean contains(UUID user);

	void setLifes(int lifes);

	int getLifes();

	void setSpawn(Map map, Location location);

	void setSpawn(Location location);

	Location getSpawn(Map map);

	Location getSpawn();

}
