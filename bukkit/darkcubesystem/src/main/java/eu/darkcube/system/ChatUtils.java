package eu.darkcube.system;

import static eu.darkcube.system.Reflection.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.md_5.bungee.chat.ComponentSerializer;

public class ChatUtils {

	private static final Class<?> CL_CHATSERIALIZER = getVersionClass(MINECRAFT_PREFIX,
			"IChatBaseComponent$ChatSerializer");
	private static final Method M_a = getMethod(CL_CHATSERIALIZER, "a", String.class);
	private static final Class<?> CL_ICHATBASECOMPONENT = getVersionClass(MINECRAFT_PREFIX, "IChatBaseComponent");
	private static final Class<?> CL_PACKETPLAYOUTCHAT = getVersionClass(MINECRAFT_PREFIX, "PacketPlayOutChat");
	private static final Constructor<?> C_PACKETPLAYOUTCHAT = getConstructor(CL_PACKETPLAYOUTCHAT,
			CL_ICHATBASECOMPONENT, byte.class);

	public static final Object chat(String text) {
		return chat(new ChatEntry.Builder().text(text).build());
	}

	public static final Object chat(ChatEntry... entries) {
		return ChatEntry.buildArray(entries).getComponent();
	}

	public static class ChatEntry {

		private JsonObject json;

		private ChatEntry() {
			json = new JsonObject();
		}

		public static ChatBaseComponent buildArray(ChatEntry... entries) {
			JsonArray array = new JsonArray();
			for (ChatEntry entry : entries) {
				array.add(new Gson().fromJson(entry.toString(), JsonElement.class));
			}
//			return new ChatBaseComponent(ChatSerializer.a(array.toString()));
			return new ChatBaseComponent(invokeMethod(M_a, null, array.toString()));
		}

		public ChatBaseComponent build() {
//			return new ChatBaseComponent(ChatSerializer.a(toString()));
			return new ChatBaseComponent(invokeMethod(M_a, null, toString()));
		}

		public static ChatBaseComponent buildActionbar(ChatEntry... entries) {
			StringBuilder msg = new StringBuilder();
			JsonArray arr = new JsonArray();
			msg.append("\"");
			for (ChatEntry entry : entries) {
				JsonObject j = entry.json;
				arr.add(j);
				if (j.has("color")) {
					ChatColor color = ChatColor
							.valueOf(j.get("color").getAsJsonPrimitive().getAsString().toUpperCase());
					msg.append(color.toString());
				}
				if (j.has("obfuscated")) {
					if (j.get("obfuscated").getAsJsonPrimitive().getAsBoolean()) {
						msg.append("§k");
					}
				}
				if (j.has("strikethrough")) {
					if (j.get("strikethrough").getAsJsonPrimitive().getAsBoolean()) {
						msg.append("§m");
					}
				}
				if (j.has("underlined")) {
					if (j.get("underlined").getAsJsonPrimitive().getAsBoolean()) {
						msg.append("§n");
					}
				}
				if (j.has("bold")) {
					if (j.get("bold").getAsJsonPrimitive().getAsBoolean()) {
						msg.append("§l");
					}
				}
				if (j.has("italic")) {
					if (j.get("italic").getAsJsonPrimitive().getAsBoolean()) {
						msg.append("§o");
					}
				}
				if (j.has("text")) {
					msg.append(j.get("text").getAsJsonPrimitive().getAsString());
				}
				msg.append("§r");
			}
			msg.append("\"");

//			msg.append(arr.toString());
//			IChatBaseComponent comp = ChatSerializer.a(msg.toString());
//			PacketPlayOutChat c = new PacketPlayOutChat(null, (byte) 2);
			Object c = newInstance(C_PACKETPLAYOUTCHAT, null, (byte) 2);
			Field f_components = getField(c.getClass(), "components");
			try {
				f_components.set(c, ComponentSerializer.parse(msg.toString()));
			} catch (IllegalArgumentException ex) {
				ex.printStackTrace();
			} catch (IllegalAccessException ex) {
				ex.printStackTrace();
			}
//			c.components = ComponentSerializer.parse(msg.toString());
			return new ChatBaseComponent(null, con -> {
//				con.sendPacket(c);
				Plugin.sendPacket(con, c);
			});
		}

		public JsonObject getJson() {
			return json;
		}

		static ChatEntry byJsonObject(JsonObject o) {
			ChatEntry centry = new ChatEntry();
			o.entrySet().forEach(entry -> {
				centry.json.add(entry.getKey(), entry.getValue());
			});
			return centry;
		}

		public static ChatEntry getByJson(String json) {
			return byJsonObject(new JsonParser().parse(json).getAsJsonObject());
		}

		@Override
		public String toString() {
			return json.toString();
		}

		public static class Builder {

			private static final Map<String, Boolean> DEFAULT_FORMATS;
			private static final Map<ChatColor, String> KEY_BY_FORMAT;
			private static final String[] DEFAULT_FORMAT_KEYS = new String[] {
					"bold", "italic", "strikethrough", "underlined", "obfuscated"
			};

			static {
				DEFAULT_FORMATS = new HashMap<>();
				for (String key : DEFAULT_FORMAT_KEYS)
					DEFAULT_FORMATS.put(key, Boolean.FALSE);
				KEY_BY_FORMAT = new HashMap<>();
				KEY_BY_FORMAT.put(ChatColor.BOLD, "bold");
				KEY_BY_FORMAT.put(ChatColor.ITALIC, "italic");
				KEY_BY_FORMAT.put(ChatColor.STRIKETHROUGH, "strikethrough");
				KEY_BY_FORMAT.put(ChatColor.UNDERLINE, "underlined");
				KEY_BY_FORMAT.put(ChatColor.MAGIC, "obfuscated");
			}

			private String text;
			private JsonObject clickEvent;
			private JsonObject hoverEvent;

			public Builder() {
			}

			public ChatEntry[] build() {
				if (text == null)
					return new ChatEntry[] {
							ChatEntry.getByJson("{\"text\":\"null\"}")
					};

				List<ChatEntry> entries = new ArrayList<>();

				Set<String> formats = new HashSet<>();
				StringBuilder current = new StringBuilder();
				JsonObject json = new JsonObject();
				ChatColor old = null;

				for (int i = 0; i < text.length(); i++) {
					char colorChar = text.charAt(i);
					if (colorChar == ChatColor.COLOR_CHAR && i - 1 != text.length()) {
						char afterColor = text.charAt(i + 1);
						ChatColor color = ChatColor.getByChar(afterColor);
						if (color != null) {
							i++;
							if (color.isFormat()) {
								String key = KEY_BY_FORMAT.get(color);
								formats.add(key);
							}
							build0(old, formats, json, current, entries);
							if (!color.isFormat())
								formats.clear();
							json = new JsonObject();
							current = new StringBuilder();
							if (!color.isFormat())
								old = color;
						} else {
							current.append(colorChar);
						}
					} else {
						current.append(colorChar);
					}
				}
				if (old != null) {
					build0(old, formats, json, current, entries);
				}
				return entries.toArray(new ChatEntry[entries.size()]);
			}

			private void build0(ChatColor old, Set<String> formats, JsonObject json, StringBuilder current,
					List<ChatEntry> entries) {
				if (old != null) {
					for (String format : formats)
						json.addProperty(format, true);
					if (current.toString() != null)
						json.addProperty("text", current.toString());
					if (clickEvent != null)
						json.add("clickEvent", clickEvent);
					if (hoverEvent != null)
						json.add("hoverEvent", hoverEvent);
					json.addProperty("color", old.name().toLowerCase());

					entries.add(ChatEntry.byJsonObject(json));
				}
			}

			public Builder textRaw(String text) {
				this.text = text;
				return this;
			}

			public Builder text(String text) {
				return textRaw(text + ChatColor.RESET);
			}

			public Builder click(String command) {
				return click("run_command", ChatColor.stripColor(command));
			}

			public Builder click(String action, String value) {
				clickEvent = new JsonObject();
				clickEvent.addProperty("action", action);
				clickEvent.addProperty("value", value);
				return this;
			}

			public Builder hover(String text) {
				return hover("show_text", text);
			}

			public Builder hover(String action, String value) {
				hoverEvent = new JsonObject();
				hoverEvent.addProperty("action", action);
				hoverEvent.addProperty("value", value);
				return this;
			}
		}

	}
}