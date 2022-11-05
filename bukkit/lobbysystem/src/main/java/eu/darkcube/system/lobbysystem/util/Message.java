package eu.darkcube.system.lobbysystem.util;

import org.bukkit.ChatColor;

import eu.darkcube.system.language.core.Language;
import eu.darkcube.system.lobbysystem.user.User;

public enum Message {

	LOADED,
	SERVER_NOT_STARTED,
	INVENTORY_NAME_DAILY_REWARD,
	REWARD_COINS,
	REWARD_ALREADY_USED,
	PSERVER_ITEM_TITLE,
	CLICK_TO_JOIN,
	PSERVER_NOT_PUBLIC,
	CONNECTING_TO_PSERVER_AS_SOON_AS_ONLINE,

	;

	public static final String PREFIX_ITEM = "ITEM_";
	public static final String PREFIX_LORE = "LORE_";
	public static final String KEY_PREFIX = "LOBBY_";

	private final String key;

	private Message() {
		key = name();
	}

	public String getKey() {
		return key;
	}

	public String getMessage(Language language, Object... replacements) {
//		return language.getMessage(KEY_PREFIX + key, replacements);
		return getMessage(key, language, replacements);
//		return getMessage(key, language, replacements);
	}

	public String getServerMessage(Object... replacements) {
		return getMessage(Language.ENGLISH, replacements);
	}

	public String getMessage(User user, Object... replacements) {
		return getMessage(user.getLanguage(), replacements);
	}

	public static String getMessage(String key, Language language,
					Object... replacements) {
		return ChatColor.translateAlternateColorCodes('&', language.getMessage(KEY_PREFIX
						+ key, replacements));
	}

//	public static final String getMessage(String key, Language language,
//					String... replacements) {
//		try {
//			String msg = language.getBundle().getString(key);
//			if (msg.equals("[]")) {
//				return " ";
//			}
//			for (int i = 0; msg.contains("{}")
//							&& i < replacements.length; i++) {
//				msg = msg.replaceFirst("\\{\\}", replacements[i]);
//			}
//			return ChatColor.translateAlternateColorCodes('&', msg);
//		} catch (Exception ex) {
//			StringBuilder builder = new StringBuilder();
//			builder.append(key);
//			if (replacements.length > 0) {
//				for (int i = 0; i + 1 < replacements.length; i++) {
//					builder.append(replacements[i]).append(',');
//				}
//				builder.append(replacements[replacements.length
//								- 1]).append(']');
//			}
//			return builder.toString();
//		}
//	}
}
