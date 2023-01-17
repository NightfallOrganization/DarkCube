/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi;

import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.userapi.data.UserPersistentDataStorage;
import eu.darkcube.system.util.AdventureSupport;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.BasicMetaDataStorage;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

public class BukkitUser implements User {
	private static final PersistentDataType<Language> LANGUAGE =
			PersistentDataTypes.enumType(Language.class);
	private final ReentrantLock lock = new ReentrantLock(false);
	private final UUID uuid;
	private final BasicMetaDataStorage metaDataStorage;
	private final UserPersistentDataStorage persistentDataStorage;
	private volatile String name;
	private volatile Player player;
	private volatile boolean loaded = false;
	private volatile long lastAccess = System.currentTimeMillis();

	public BukkitUser(UUID uuid, String name) {
		this.player = Bukkit.getPlayer(uuid);
		this.uuid = uuid;
		this.name = name;
		this.metaDataStorage = new BasicMetaDataStorage();
		this.persistentDataStorage = new UserPersistentDataStorage(this);
	}

	@Override
	public @NotNull Iterable<? extends Audience> audiences() {
		Player player = asPlayer();
		if (player != null) {
			return Collections.singleton(AdventureSupport.audienceProvider().player(player));
		}
		return Collections.emptyList();
	}

	public void lock() {
		lastAccess = System.currentTimeMillis();
		lock.lock();
	}

	public void unlock() {
		lock.unlock();
		lastAccess = System.currentTimeMillis();
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
	public Language getLanguage() {
		return this.getPersistentDataStorage()
				.get(new Key("UserAPI", "language"), LANGUAGE, () -> Language.DEFAULT);
	}

	@Override
	public void setLanguage(Language language) {
		getPersistentDataStorage().set(new Key("UserAPI", "language"), LANGUAGE, language);
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
	public BasicMetaDataStorage getMetaDataStorage() {
		return metaDataStorage;
	}

	@Override
	public UserPersistentDataStorage getPersistentDataStorage() {
		return persistentDataStorage;
	}

	@Override
	public boolean isLoaded() {
		return loaded;
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
