/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.user;

import eu.darkcube.system.commandapi.v3.ICommandExecutor;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.UUID;

public class OfflineUser extends UserWithExtraData {

	private final Language language;
	private final String name;
	private final ICommandExecutor executor;

	public OfflineUser(UUID uuid) {
		super(uuid);
		boolean loaded = UserAPI.getInstance().isUserLoaded(uuid);
		eu.darkcube.system.userapi.User user = UserAPI.getInstance().getUser(uuid);
		this.language = user.getLanguage();
		if (loaded)
			UserAPI.getInstance().unloadUser(user);
		final UserCache.Entry entry = UserCache.cache().getEntry(uuid);
		this.name = entry.name;
		this.executor = ICommandExecutor.create(new OfflineSender());
	}

	@Override
	public Language getLanguage() {
		return language;
	}

	@Override
	public ICommandExecutor getCommandExecutor() {
		return executor;
	}

	@Override
	public boolean isOnline() {
		return getOnlinePlayer() != null;
	}

	@Override
	public Player getOnlinePlayer() {
		return Bukkit.getPlayer(getUUID());
	}

	private class OfflineSender implements CommandSender {

		@Override
		public String getName() {
			return name;
		}

		@Override
		public Server getServer() {
			return Bukkit.getServer();
		}

		@Override
		public void sendMessage(String var1) {}

		@Override
		public void sendMessage(String[] var1) {}

		@Override
		public PermissionAttachment addAttachment(Plugin var1) {
			return null;
		}

		@Override
		public PermissionAttachment addAttachment(Plugin var1, int var2) {
			return null;
		}

		@Override
		public PermissionAttachment addAttachment(Plugin var1, String var2, boolean var3) {
			return null;
		}

		@Override
		public PermissionAttachment addAttachment(Plugin var1, String var2, boolean var3,
				int var4) {
			return null;
		}

		@Override
		public Set<PermissionAttachmentInfo> getEffectivePermissions() {
			return null;
		}

		@Override
		public boolean hasPermission(String var1) {
			return false;
		}

		@Override
		public boolean hasPermission(Permission var1) {
			return false;
		}

		@Override
		public boolean isPermissionSet(String var1) {
			return false;
		}

		@Override
		public boolean isPermissionSet(Permission var1) {
			return false;
		}

		@Override
		public void recalculatePermissions() {

		}

		@Override
		public void removeAttachment(PermissionAttachment var1) {

		}

		@Override
		public boolean isOp() {
			return false;
		}

		@Override
		public void setOp(boolean var1) {

		}
	}
}
