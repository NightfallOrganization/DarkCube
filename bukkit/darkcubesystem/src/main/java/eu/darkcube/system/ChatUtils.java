/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system;

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

import eu.darkcube.system.ReflectionUtils.PackageType;
import net.md_5.bungee.chat.ComponentSerializer;

public class ChatUtils {

	private static final Class<?> CLASS_CHATSERIALIZER = PackageType.MINECRAFT_SERVER
			.getClass("IChatBaseComponent$ChatSerializer");

	private static final Method METHOD_a = ReflectionUtils.getMethod(ChatUtils.CLASS_CHATSERIALIZER, "a", String.class);

	private static final Class<?> CLASS_ICHATBASECOMPONENT = ReflectionUtils.getClass("IChatBaseComponent",
			PackageType.MINECRAFT_SERVER);

	private static final Class<?> CLASS_PACKETPLAYOUTCHAT = ReflectionUtils.getClass("PacketPlayOutChat",
			PackageType.MINECRAFT_SERVER);

//	private static final Constructor<?> CONSTRUCTOR_PACKETPLAYOUTCHAT = getConstructor(ChatUtils.CL_PACKETPLAYOUTCHAT,
//			ChatUtils.CL_ICHATBASECOMPONENT, byte.class);
	private static final Constructor<?> CONSTRUCTOR_PACKETPLAYOUTCHAT = ReflectionUtils
			.getConstructor(ChatUtils.CLASS_PACKETPLAYOUTCHAT, ChatUtils.CLASS_ICHATBASECOMPONENT, byte.class);

	public static final ChatBaseComponent chat(String text) {
		return ChatUtils.chat(new ChatEntry.Builder().text(text).build());
	}

	public static final ChatBaseComponent chat(ChatEntry... entries) {
		return ChatEntry.buildArray(entries);
	}

	public static class ChatEntry {

		private JsonObject json;

		private ChatEntry() {
			this.json = new JsonObject();
		}

		public static ChatBaseComponent buildArray(ChatEntry... entries) {
			JsonArray array = new JsonArray();
			for (ChatEntry entry : entries) {
				array.add(new Gson().fromJson(entry.toString(), JsonElement.class));
			}
//			return new ChatBaseComponent(ChatSerializer.a(array.toString()));
			return new ChatBaseComponent(ReflectionUtils.invokeMethod(null, ChatUtils.METHOD_a, array.toString()));
		}

		public ChatBaseComponent build() {
//			return new ChatBaseComponent(ChatSerializer.a(toString()));
			return new ChatBaseComponent(ReflectionUtils.invokeMethod(null, ChatUtils.METHOD_a, this.toString()));
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
			Object c = ReflectionUtils.instantiateObject(ChatUtils.CONSTRUCTOR_PACKETPLAYOUTCHAT, null, (byte) 2);
			Field f_components = ReflectionUtils.getField(c.getClass(), true, "components");
			ReflectionUtils.setValue(c, f_components, ComponentSerializer.parse(msg.toString()));
//			try {
//				f_components.set(c, ComponentSerializer.parse(msg.toString()));
//			} catch (IllegalArgumentException ex) {
//				ex.printStackTrace();
//			} catch (IllegalAccessException ex) {
//				ex.printStackTrace();
//			}
//			c.components = ComponentSerializer.parse(msg.toString());
			return new ChatBaseComponent(null, con -> {
//				con.sendPacket(c);
				Plugin.sendPacket(con, c);
			});
		}

		public JsonObject getJson() {
			return this.json;
		}

		static ChatEntry byJsonObject(JsonObject o) {
			ChatEntry centry = new ChatEntry();
			o.entrySet().forEach(entry -> {
				centry.json.add(entry.getKey(), entry.getValue());
			});
			return centry;
		}

		public static ChatEntry getByJson(String json) {
			return ChatEntry.byJsonObject(new JsonParser().parse(json).getAsJsonObject());
		}

		@Override
		public String toString() {
			return this.json.toString();
		}

		public static class Builder {

			private static final Map<String, Boolean> DEFAULT_FORMATS;

			private static final Map<ChatColor, String> KEY_BY_FORMAT;

			private static final String[] DEFAULT_FORMAT_KEYS = new String[] {
					"bold", "italic", "strikethrough", "underlined", "obfuscated"
			};

			static {
				DEFAULT_FORMATS = new HashMap<>();
				for (String key : Builder.DEFAULT_FORMAT_KEYS)
					Builder.DEFAULT_FORMATS.put(key, Boolean.FALSE);
				KEY_BY_FORMAT = new HashMap<>();
				Builder.KEY_BY_FORMAT.put(ChatColor.BOLD, "bold");
				Builder.KEY_BY_FORMAT.put(ChatColor.ITALIC, "italic");
				Builder.KEY_BY_FORMAT.put(ChatColor.STRIKETHROUGH, "strikethrough");
				Builder.KEY_BY_FORMAT.put(ChatColor.UNDERLINE, "underlined");
				Builder.KEY_BY_FORMAT.put(ChatColor.MAGIC, "obfuscated");
			}

			private String text;

			private JsonObject clickEvent;

			private JsonObject hoverEvent;

			public Builder() {
			}

			public ChatEntry[] build() {
				if (this.text == null)
					return new ChatEntry[] {
							ChatEntry.getByJson("{\"text\":\"null\"}")
					};

				List<ChatEntry> entries = new ArrayList<>();

				Set<String> formats = new HashSet<>();
				StringBuilder current = new StringBuilder();
				JsonObject json = new JsonObject();
				ChatColor old = null;

				for (int i = 0; i < this.text.length(); i++) {
					char colorChar = this.text.charAt(i);
					if (colorChar == ChatColor.COLOR_CHAR && i - 1 != this.text.length()) {
						char afterColor = this.text.charAt(i + 1);
						ChatColor color = ChatColor.getByChar(afterColor);
						if (color != null) {
							i++;
							if (color.isFormat()) {
								String key = Builder.KEY_BY_FORMAT.get(color);
								formats.add(key);
							}
							this.build0(old, formats, json, current, entries);
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
					this.build0(old, formats, json, current, entries);
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
					if (this.clickEvent != null)
						json.add("clickEvent", this.clickEvent);
					if (this.hoverEvent != null)
						json.add("hoverEvent", this.hoverEvent);
					json.addProperty("color", old.name().toLowerCase());

					entries.add(ChatEntry.byJsonObject(json));
				}
			}

			public Builder textRaw(String text) {
				this.text = text;
				return this;
			}

			public Builder text(String text) {
				return this.textRaw(text + ChatColor.RESET);
			}

			public Builder click(String command) {
				return this.click("run_command", ChatColor.stripColor(command));
			}

			public Builder click(String action, String value) {
				this.clickEvent = new JsonObject();
				this.clickEvent.addProperty("action", action);
				this.clickEvent.addProperty("value", value);
				return this;
			}

			public Builder hover(String text) {
				return this.hover("show_text", text);
			}

			public Builder hover(String action, String value) {
				this.hoverEvent = new JsonObject();
				this.hoverEvent.addProperty("action", action);
				this.hoverEvent.addProperty("value", value);
				return this;
			}

		}

	}

}
