/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.shop;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import de.pixel.bedwars.Main;

public abstract class ListenerShop extends Shop {

	private Listener listener;

	public ListenerShop(Listener listener) {
		this.listener = listener;
	}

	@Override
	public void registerOpenListener() {
		Bukkit.getPluginManager().registerEvents(listener, Main.getInstance());
	}

	@Override
	public void unregisterOpenListener() {
		HandlerList.unregisterAll(listener);
	}
}
