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
import org.bukkit.entity.Player;

public class OnlineUser extends UserWithExtraData {

	private final Player player;
	private final Language language;
	private final ICommandExecutor executor;

	public OnlineUser(final Player player) {
		super(player.getUniqueId());
		this.player = player;
		this.executor = ICommandExecutor.create(player);
		this.language = UserAPI.getInstance().getUser(player).getLanguage();
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
	public Player getOnlinePlayer() {
		return player;
	}

	@Override
	public boolean isOnline() {
		return player.isOnline();
	}
}
