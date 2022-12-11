/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi;

import java.math.BigInteger;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import eu.darkcube.system.language.core.Language;
import eu.darkcube.system.userapi.data.BasicMetaDataStorage;
import eu.darkcube.system.userapi.data.BasicPersistentDataStorage;
import eu.darkcube.system.userapi.data.Key;
import eu.darkcube.system.userapi.data.PersistentDataType;
import eu.darkcube.system.userapi.data.PersistentDataTypes;

public class BukkitUser implements User {
	private static final PersistentDataType<Language> LANGUAGE =
			PersistentDataTypes.enumType(Language.class);
	private UUID uuid;
	private String name;
	private BasicMetaDataStorage metaDataStorage;
	private BasicPersistentDataStorage persistentDataStorage;
	private volatile long lastAccess = 0;
	private volatile boolean loaded = false;
	private Player player;
	private volatile BukkitUser user = null;
	boolean loading = true;
	final UserAPI userApi;

	public BukkitUser(UserAPI userApi, UUID uuid, String name) {
		this.userApi = userApi;
		this.player = Bukkit.getPlayer(uuid);
		this.uuid = uuid;
		this.name = name;
		this.metaDataStorage = new BasicMetaDataStorage();
		this.persistentDataStorage = new BasicPersistentDataStorage(this);
	}

	@Override
	public boolean isLoaded() {
		if (user != null && user != this)
			return user.isLoaded();
		return loaded;
	}

	@Override
	public BigInteger getCubes() {
		return this.getPersistentDataStorage()
				.get(new Key(PluginUserAPI.getInstance(), "cubes"), PersistentDataTypes.BIGINTEGER,
						() -> BigInteger.valueOf(1000L));
	}

	@Override
	public void setCubes(BigInteger cubes) {
		this.getPersistentDataStorage()
				.set(new Key(PluginUserAPI.getInstance(), "cubes"), PersistentDataTypes.BIGINTEGER,
						cubes);
	}

	@Override
	public Language getLanguage() {
		return this.getPersistentDataStorage()
				.get(new Key(PluginUserAPI.getInstance(), "language"), LANGUAGE,
						() -> Language.DEFAULT);
	}

	@Override
	public void setLanguage(Language language) {
		getPersistentDataStorage().set(new Key(PluginUserAPI.getInstance(), "language"), LANGUAGE,
				language);
	}

	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public String getName() {
		Player p = asPlayer();
		if (p != null) {
			return user().name = p.getName();
		}
		return user().name;
	}

	@Override
	public Player asPlayer() {
		BukkitUser u = user();
		synchronized (u) {
			if (u.player == null || !u.player.isOnline()) {
				u.player = Bukkit.getPlayer(u.uuid);
			}
			return u.player;
		}
	}

	@Override
	public BasicMetaDataStorage getMetaDataStorage() {
		return user().metaDataStorage;
	}

	@Override
	public BasicPersistentDataStorage getPersistentDataStorage() {
		return user().persistentDataStorage;
	}

	void player(Player player) {
		BukkitUser u = user();
		synchronized (u) {
			u.player = player;
		}
	}

	void loaded(boolean value) {
		if (user != null && user != this) {
			user.loaded(value);
		} else {
			this.loaded = value;
		}
	}

	private BukkitUser user() {
		if (!loading) {
			BukkitUser n = (BukkitUser) userApi.existingUser(uuid);
			if (n != this) {
				if (n == null) {
					persistentDataStorage.clear();
					metaDataStorage.data.clear();
					userApi.loadUser(this);
					lastAccess = System.currentTimeMillis();
					return this;
				}
				user = n;
				if (user != null) {
					BukkitUser ru = user.user();
					user = ru != null ? ru : user;
					user.lastAccess = System.currentTimeMillis();
					return user;
				}
			}
		}
		if (user == null)
			user = this;
		user.lastAccess = System.currentTimeMillis();
		return user;
	}

	long lastAccess() {
		return user().lastAccess;
	}

	Player player() {
		BukkitUser u = user();
		synchronized (u) {
			return u.player;
		}
	}

}
