/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi;

import eu.darkcube.system.commandapi.v3.ICommandExecutor;
import eu.darkcube.system.libs.net.kyori.adventure.audience.ForwardingAudience;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.MetaDataStorage;
import eu.darkcube.system.util.data.PersistentDataStorage;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.UUID;

public interface User extends ForwardingAudience, ICommandExecutor {

	void lock();

	void unlock();

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
	 * @param language the language
	 */
	void setLanguage(Language language);

	/**
	 * @return the user's cubes
	 */
	BigInteger getCubes();

	/**
	 * Sets the user's cubes
	 *
	 * @param cubes the cubes
	 */
	void setCubes(BigInteger cubes);

	/**
	 * @return the user's {@link MetaDataStorage}. This only stores data until the user is unloaded,
	 * then all data is forgotten. This will NOT synchronize over all servers
	 */
	MetaDataStorage getMetaDataStorage();

	/**
	 * @return the user's {@link PersistentDataStorage}. This will also synchronize over all servers
	 */
	PersistentDataStorage getPersistentDataStorage();

	boolean isLoaded();

}
