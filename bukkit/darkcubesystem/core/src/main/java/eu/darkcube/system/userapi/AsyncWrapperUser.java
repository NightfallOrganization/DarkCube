/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi;

import eu.darkcube.system.DarkCubeSystem;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.userapi.data.UserPersistentDataStorage;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.BasicMetaDataStorage;
import org.bukkit.entity.Player;

import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

class AsyncWrapperUser implements User {
	private final AtomicReference<BukkitUser> reference = new AtomicReference<>();
	private final UUID uuid;

	AsyncWrapperUser(BukkitUser user) {
		this.uuid = user.getUniqueId();
		this.reference.set(user);
	}

	@Override
	public @NotNull Iterable<? extends Audience> audiences() {
		return user(BukkitUser::audiences);
	}

	private void user(Consumer<BukkitUser> consumer) {
		user(u -> {
			consumer.accept(u);
			return u;
		});
	}

	private <T> T user(Function<BukkitUser, T> function) {
		BukkitUserAPI api = BukkitUserAPI.getInstance();
		BukkitUser user = reference.get();
		api.lock.readLock().lock();
		BukkitUser cur = api.users.get(uuid);
		if (cur != user) {
			if (cur != null) {
				user = cur;
			} else {
				api.lock.readLock().unlock();
				api.lock.writeLock().lock();
				cur = api.users.get(uuid);
				if (cur == null) {
					DarkCubeSystem.getInstance().getLogger().warning(
							"[UserAPI] User was loaded by accessing object. This most likely "
									+ "indicates a memory leak! Use User#isLoaded before you use a "
									+ "User object if the user is not online!");
					new Exception().printStackTrace();
					user = api.loadUser(uuid);
				} else {
					user = cur;
				}
				api.lock.readLock().lock();
				api.lock.writeLock().unlock();
				reference.set(user);
			}
		}
		user.lock();
		T t = function.apply(user);
		user.unlock();
		api.lock.readLock().unlock();
		return t;
	}

	public void lock() {
		user(BukkitUser::lock);
	}

	public void unlock() {
		user(BukkitUser::unlock);
	}

	@Override
	public UUID getUniqueId() {
		return uuid;
	}

	@Override
	public String getName() {
		return user(User::getName);
	}

	@Override
	public Player asPlayer() {
		return user(User::asPlayer);
	}

	@Override
	public Language getLanguage() {
		return user(User::getLanguage);
	}

	@Override
	public void setLanguage(Language language) {
		user((Consumer<BukkitUser>) u -> u.setLanguage(language));
	}

	@Override
	public BigInteger getCubes() {
		return user(User::getCubes);
	}

	@Override
	public void setCubes(BigInteger cubes) {
		user((Consumer<BukkitUser>) u -> u.setCubes(cubes));
	}

	@Override
	public BasicMetaDataStorage getMetaDataStorage() {
		return user(BukkitUser::getMetaDataStorage);
	}

	@Override
	public UserPersistentDataStorage getPersistentDataStorage() {
		return user(BukkitUser::getPersistentDataStorage);
	}

	@Override
	public boolean isLoaded() {
		return UserAPI.getInstance().isUserLoaded(uuid);
	}

	public void loaded(boolean loaded) {
		user((Consumer<BukkitUser>) u -> u.loaded(loaded));
	}
}
