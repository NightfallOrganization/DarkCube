/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener;

import org.bukkit.event.Event;

public abstract class Listener<T extends Event> implements org.bukkit.event.Listener {

	public abstract void handle(T e);

}
