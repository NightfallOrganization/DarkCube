/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi;

import eu.darkcube.system.userapi.data.*;
import eu.darkcube.system.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

class BukkitUser implements User {
	private static final PersistentDataType<Language> LANGUAGE =
			PersistentDataTypes.enumType(Language.class);
	final ReentrantLock lock = new ReentrantLock(false);
	private final UUID uuid;
	private final BasicMetaDataStorage metaDataStorage;
	private final BasicPersistentDataStorage persistentDataStorage;
	private volatile String name;
	private volatile Player player;
	private volatile boolean loaded = false;
	private volatile long lastAccess = System.currentTimeMillis();

	public BukkitUser(UUID uuid, String name) {
		this.player = Bukkit.getPlayer(uuid);
		this.uuid = uuid;
		this.name = name;
		this.metaDataStorage = new BasicMetaDataStorage();
		this.persistentDataStorage = new BasicPersistentDataStorage(this);
	}

	public void lock() {
		lastAccess = System.currentTimeMillis();
		lock.lock();
		lastAccess = System.currentTimeMillis();
	}

	public void unlock() {
		lock.unlock();
		lastAccess = System.currentTimeMillis();
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public BigInteger getCubes() {
		return this.getPersistentDataStorage()
				.get(new Key("UserAPI", "cubes"), PersistentDataTypes.BIGINTEGER,
						() -> BigInteger.valueOf(1000L));
	}

	@Override
	public void setCubes(BigInteger cubes) {
		this.getPersistentDataStorage()
				.set(new Key("UserAPI", "cubes"), PersistentDataTypes.BIGINTEGER, cubes);
	}

	@Override
	public Language getLanguage() {
		return this.getPersistentDataStorage()
				.get(new Key("UserAPI", "language"), LANGUAGE, () -> Language.DEFAULT);
	}

	@Override
	public void setLanguage(Language language) {
		getPersistentDataStorage().set(new Key("UserAPI", "language"), LANGUAGE, language);
	}

	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public String getName() {
		Player p = asPlayer();
		if (p != null) {
			return name = p.getName();
		}
		return name;
	}

	@Override
	public Player asPlayer() {
		if (player == null || !player.isOnline()) {
			player = Bukkit.getPlayer(uuid);
		}
		return player;
	}

	@Override
	public BasicMetaDataStorage getMetaDataStorage() {
		return metaDataStorage;
	}

	@Override
	public BasicPersistentDataStorage getPersistentDataStorage() {
		return persistentDataStorage;
	}

	void loaded(boolean value) {
		this.loaded = value;
	}

	long lastAccess() {
		return lastAccess;
	}

	public void lastAccess(long lastAccess) {
		this.lastAccess = lastAccess;
	}
}
