package eu.darkcube.minigame.woolbattle.user;

import java.util.Collection;
import java.util.UUID;

public interface UserWrapper {

	User load(UUID uuid);
	User getUser(UUID uuid);
	boolean isLoaded(UUID uuid);
	boolean unload(User user);
	Collection<? extends User> getUsers();
	
}
