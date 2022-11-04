package de.dasbabypixel.prefixplugin;

import org.bukkit.event.*;
import org.bukkit.event.server.*;

public class ReloadPrefixPluginEvent extends ServerEvent {

	private static final HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
