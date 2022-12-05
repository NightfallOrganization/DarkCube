package eu.darkcube.system.userapi;

import java.math.BigInteger;
import java.util.UUID;
import org.bukkit.entity.Player;
import eu.darkcube.system.language.core.Language;
import eu.darkcube.system.userapi.data.MetaDataStorage;
import eu.darkcube.system.userapi.data.PersistentDataStorage;

public interface User {

	/**
	 * @return this user's minecraft {@link UUID}
	 */
	UUID getUniqueId();

	/**
	 * @return the last known name for this user
	 */
	String getName();

	/**
	 * @return the user's bukkit player object
	 */
	Player asPlayer();

	/**
	 * @return the user's language
	 */
	Language getLanguage();

	/**
	 * Sets the user's language
	 * 
	 * @param language
	 */
	void setLanguage(Language language);

	/**
	 * @return the user's cubes
	 */
	BigInteger getCubes();

	/**
	 * Sets the user's cubes
	 * 
	 * @param cubes
	 */
	void setCubes(BigInteger cubes);

	/**
	 * @return the user's {@link MetaDataStorage}. This only stores data until the user is unloaded,
	 *         then all data is forgotten. This will NOT synchronize over all servers
	 */
	MetaDataStorage getMetaDataStorage();

	/**
	 * @return the user's {@link PersistentDataStorage}. This will also synchronize over all servers
	 */
	PersistentDataStorage getPersistentDataStorage();

	boolean isLoaded();

}
