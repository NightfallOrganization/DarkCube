/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;

import java.util.*;

public class ChatUtils {

	public static ChatBaseComponent chat(String text) {
		return ChatUtils.chat(new ChatEntry.Builder().text(text).build());
	}

	public static ChatBaseComponent chat(ChatEntry... entries) {
		return ChatEntry.build(entries);
	}

	public static class ChatEntry {

		private JsonObject json;

		private ChatEntry() {
			this.json = new JsonObject();
		}

		public static ChatBaseComponent build(ChatEntry... entries) {
			return new ChatBaseComponent(entries, ChatBaseComponent.Display.CHAT);
		}

		public ChatBaseComponent build() {
			return build(this);
		}

		public static ChatBaseComponent buildActionbar(ChatEntry... entries) {
			return new ChatBaseComponent(entries, ChatBaseComponent.Display.ACTIONBAR);
		}

		public static ChatBaseComponent buildTitle(ChatEntry[] entries, ChatBaseComponent.TitleType type, int in, int stay, int out) {
			return new ChatBaseComponent(entries, ChatBaseComponent.Display.TITLE, type, in, stay,
					out);
		}

		public JsonObject getJson() {
			return this.json;
		}

		static ChatEntry byJsonObject(JsonObject o) {
			ChatEntry centry = new ChatEntry();
			o.entrySet().forEach(entry -> centry.json.add(entry.getKey(), entry.getValue()));
			return centry;
		}

		public static ChatEntry getByJson(String json) {
			return ChatEntry.byJsonObject(new Gson().fromJson(json, JsonObject.class));
		}

		@Override
		public String toString() {
			return this.json.toString();
		}

		public static class Builder {

			private static final Map<ChatColor, String> KEY_BY_FORMAT;

			static {
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
					return new ChatEntry[] {ChatEntry.getByJson("{\"text\":\"null\"}")};

				List<ChatEntry> entries = new ArrayList<>();

				Set<String> formats = new HashSet<>();
				StringBuilder current = new StringBuilder();
				JsonObject json = new JsonObject();
				ChatColor old = null;

				for (int i = 0; i < this.text.length(); i++) {
					char colorChar = this.text.charAt(i);
					if (colorChar == ChatColor.COLOR_CHAR) {
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
				return entries.toArray(new ChatEntry[0]);
			}

			private void build0(ChatColor old, Set<String> formats, JsonObject json,
					StringBuilder current, List<ChatEntry> entries) {
				if (old != null) {
					for (String format : formats)
						json.addProperty(format, true);
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
