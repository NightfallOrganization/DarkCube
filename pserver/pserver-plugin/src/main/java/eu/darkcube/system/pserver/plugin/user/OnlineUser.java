package eu.darkcube.system.pserver.plugin.user;

import org.bukkit.entity.*;

import eu.darkcube.system.commandapi.v3.*;
import eu.darkcube.system.language.core.*;

public class OnlineUser extends UserWithExtraData {

	private final Player player;
	private final Language language;
	private final ICommandExecutor executor;

	public OnlineUser(final Player player) {
		super(player.getUniqueId());
		this.player = player;
		this.executor = new BukkitCommandExecutor(player);
		this.language = Language.getLanguage(player.getUniqueId());
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
