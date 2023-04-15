/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package de.dasbabypixel.prefixplugin;

import org.bukkit.entity.Player;

import java.util.UUID;

public class PrefixPlugin {

	private static PrefixPlugin plugin = new PrefixPlugin(PrefixPluginBukkit.instance());
	private PrefixPluginBukkit main;

	private PrefixPlugin(PrefixPluginBukkit main) {
		this.main = main;
	}

	public static PrefixPlugin getApi() {
		return plugin;
	}

	public String getName(Player p) {
		return main.getScoreboardManager().replacePlaceHolders(p, "%prefix%%name%%suffix%");
	}

	public void setSuffix(UUID uuid, String suffix) {
		main.getScoreboardManager().setSuffix(uuid, suffix);
	}

	public void setPrefix(UUID uuid, String prefix) {
		main.getScoreboardManager().setPrefix(uuid, prefix);
	}

	public String getPrefix(UUID uuid) {
		return main.getScoreboardManager().getPrefix(uuid);
	}

	public String getSuffix(UUID uuid) {
		return main.getScoreboardManager().getSuffix(uuid);
	}
}
