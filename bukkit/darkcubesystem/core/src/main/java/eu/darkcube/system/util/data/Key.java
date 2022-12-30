/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data;

import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class Key {

	private final String plugin;
	private final String key;

	public Key(Plugin plugin, String key) {
		this.plugin = plugin.getName();
		this.key = key;
	}

	public Key(String plugin, String key) {
		this.plugin = plugin;
		this.key = key;
	}

	public static Key fromString(String string) {
		String[] a = string.split(":", 2);
		return new Key(a[0], a[1]);
	}

	public String getKey() {
		return this.key;
	}

	public String getPlugin() {
		return this.plugin;
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, plugin);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Key other = (Key) obj;
		return Objects.equals(this.key, other.key) && Objects.equals(this.plugin, other.plugin);
	}

	@Override
	public String toString() {
		return plugin + ":" + key;
	}

}
