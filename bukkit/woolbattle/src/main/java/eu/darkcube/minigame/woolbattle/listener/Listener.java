package eu.darkcube.minigame.woolbattle.listener;

import org.bukkit.event.Event;

public abstract class Listener<T extends Event> implements org.bukkit.event.Listener {

	public abstract void handle(T e);

}