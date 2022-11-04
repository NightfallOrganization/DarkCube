package de.dasbabypixel.prefixplugin;

import java.util.*;

import org.bukkit.event.*;
import org.bukkit.event.server.*;

public class ReloadSinglePrefixEvent extends ServerEvent {

	private static final HandlerList handlers = new HandlerList();
	
	private UUID uuid;
	private String newPrefix;
	private String newSuffix;
	
	public ReloadSinglePrefixEvent(UUID uuidOfReloadedPlayer, String newPrefix, String newSuffix) {
		this.uuid = uuidOfReloadedPlayer;
		this.newPrefix = newPrefix;
		this.newSuffix = newSuffix;
	}
	
	public UUID getUUIDOfReloadedPlayer() {
		return uuid;
	}
	
	public void setUUIDOfReloadedPlayer(UUID uuid) {
		this.uuid = uuid;
	}
	
	public String getNewPrefix() {
		return newPrefix;
	}
	
	public void setNewPrefix(String newPrefix) {
		this.newPrefix = newPrefix;
	}
	
	public String getNewSuffix() {
		return newSuffix;
	}
	
	public void setNewSuffix(String newSuffix) {
		this.newSuffix = newSuffix;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
