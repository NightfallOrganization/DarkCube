/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import eu.darkcube.system.DarkCubeSystem;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.AdventureSupport;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class BukkitCommandExecutor implements ILanguagedCommandExecutor, ForwardingAudience {

	private CommandSender sender;
	private Audience audience;

	public BukkitCommandExecutor(CommandSender sender) {
		this.sender = sender;
		this.audience = AdventureSupport.audienceProvider().sender(sender);
		Bukkit.getPluginManager().callEvent(new BukkitCommandExecutorConfigureEvent(this));
	}

	@Override
	public @NotNull Iterable<? extends Audience> audiences() {
		return Collections.singleton(audience);
	}

	@Override
	public Language getLanguage() {
		if (sender instanceof Player) {
			return UserAPI.getInstance().getUser((Player) sender).getLanguage();
		}
		return Language.DEFAULT;
	}

	@Override
	public void setLanguage(Language language) {
		if (sender instanceof Player) {
			UserAPI.getInstance().getUser((Player) sender).setLanguage(language);
		}
		DarkCubeSystem.getInstance().getLogger().warning("Can't set language of the console!");
	}

	@Override
	public String getCommandPrefix() {
		return sender instanceof Player ? "/" : ILanguagedCommandExecutor.super.getCommandPrefix();
	}
}
