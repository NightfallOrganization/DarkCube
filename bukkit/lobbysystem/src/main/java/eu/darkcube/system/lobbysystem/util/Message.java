/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.lobbysystem.util;

import eu.darkcube.system.BaseMessage;

public enum Message implements BaseMessage {

	LOADED,
	SERVER_NOT_STARTED,
	INVENTORY_NAME_DAILY_REWARD,
	REWARD_COINS,
	REWARD_ALREADY_USED,
	PSERVER_ITEM_TITLE,
	CLICK_TO_JOIN,
	PSERVER_NOT_PUBLIC,
	CONNECTING_TO_PSERVER_AS_SOON_AS_ONLINE,
	PSERVEROWN_STATUS,
	STATE_OFFLINE,
	STATE_STARTING,
	STATE_RUNNING,
	STATE_STOPPING,
	STOP_OTHER_PSERVER_BEFORE_STARTING_ANOTHER,
	CONNECTOR_NPC_NOT_FOUND,
	CONNECTOR_NPC_REMOVED,
	CONNECTOR_NPC_CREATED,
	CONNECTOR_NPC_PERMISSION_ADDED,
	CONNECTOR_NPC_PERMISSION_REMOVED,
	CONNECTOR_NPC_PERMISSION_LIST,
	CONNECTOR_NPC_SERVER_STARTING,
	CONNECTOR_NPC_SERVER_ONLINE,
	CONNECTOR_NPC_SERVER_DESCRIPTION,
	STATE_LOBBY,
	STATE_INGAME,
	GAMESERVER_STATE;

	public static final String PREFIX_ITEM = "ITEM_";
	public static final String PREFIX_LORE = "LORE_";
	public static final String KEY_PREFIX = "LOBBY_";

	private final String key;

	Message() {
		key = name();
	}

	@Override
	public String getPrefixModifier() {
		return KEY_PREFIX;
	}

	@Override
	public String getKey() {
		return key;
	}

	// public static final String getMessage(String key, Language language,
	// String... replacements) {
	// try {
	// String msg = language.getBundle().getString(key);
	// if (msg.equals("[]")) {
	// return " ";
	// }
	// for (int i = 0; msg.contains("{}")
	// && i < replacements.length; i++) {
	// msg = msg.replaceFirst("\\{\\}", replacements[i]);
	// }
	// return ChatColor.translateAlternateColorCodes('&', msg);
	// } catch (Exception ex) {
	// StringBuilder builder = new StringBuilder();
	// builder.append(key);
	// if (replacements.length > 0) {
	// for (int i = 0; i + 1 < replacements.length; i++) {
	// builder.append(replacements[i]).append(',');
	// }
	// builder.append(replacements[replacements.length
	// - 1]).append(']');
	// }
	// return builder.toString();
	// }
	// }
}
