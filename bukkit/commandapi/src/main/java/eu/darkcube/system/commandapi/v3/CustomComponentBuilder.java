/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CustomComponentBuilder {
	private TextComponent current;
	private final List<TextComponent> parts = new ArrayList<>();

	public CustomComponentBuilder(CustomComponentBuilder original) {
		this.current = new TextComponent(original.current);
		for (TextComponent baseComponent : original.parts) {
			this.parts.add((TextComponent) baseComponent.duplicate());
		}
	}

	public static TextComponent[] cast(BaseComponent[] components) {
		TextComponent[] arr = new TextComponent[components.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = (TextComponent) components[i];
		}
		return arr;
	}

	public static Consumer<CustomComponentBuilder> applyPrefixModifier(Consumer<CustomComponentBuilder> prefixModifier,
			TextComponent[] components) {
		return b -> {
			prefixModifier.accept(b);
			ChatColor color = null;
			Boolean bold = null;
			Boolean italic = null;
			Boolean underlined = null;
			Boolean strikethrough = null;
			Boolean obfuscated = null;
			String insertion = null;
			for (int i = b.getParts().size() - 1; i >= 0; i--) {
				if (color != null && bold != null && italic != null && underlined != null && strikethrough != null
						&& obfuscated != null && insertion != null) {
					break;
				}
				TextComponent component = b.getParts().get(i);
				if (color == null && component.getColorRaw() != null) {
					color = component.getColorRaw();
				}
				if (bold == null && component.isBoldRaw() != null) {
					bold = component.isBoldRaw();
				}
				if (italic == null && component.isItalicRaw() != null) {
					italic = component.isItalicRaw();
				}
				if (underlined == null && component.isUnderlinedRaw() != null) {
					underlined = component.isUnderlinedRaw();
				}
				if (strikethrough == null && component.isStrikethroughRaw() != null) {
					strikethrough = component.isStrikethroughRaw();
				}
				if (obfuscated == null && component.isObfuscatedRaw() != null) {
					obfuscated = component.isObfuscatedRaw();
				}
				if (insertion == null && component.getInsertion() != null) {
					insertion = component.getInsertion();
				}
			}
			int i = 0;
			while ((color != null || bold != null || italic != null || underlined != null || strikethrough != null
					|| obfuscated != null || insertion != null) && i < components.length) {
				TextComponent component = components[i++];
				if (color != null) {
					if (component.getColorRaw() != null)
						color = null;
					else
						component.setColor(color);
				}
				if (bold != null) {
					if (component.isBoldRaw() != null)
						bold = null;
					else
						component.setBold(bold);
				}
				if (italic != null) {
					if (component.isItalicRaw() != null)
						italic = null;
					else
						component.setItalic(italic);
				}
				if (underlined != null) {
					if (component.isUnderlinedRaw() != null)
						underlined = null;
					else
						component.setUnderlined(underlined);
				}
				if (strikethrough != null) {
					if (component.isStrikethroughRaw() != null)
						strikethrough = null;
					else
						component.setStrikethrough(strikethrough);
				}
				if (obfuscated != null) {
					if (component.isObfuscatedRaw() != null)
						obfuscated = null;
					else
						component.setObfuscated(obfuscated);
				}
				if (insertion != null) {
					if (component.getInsertion() != null)
						insertion = null;
					else
						component.setInsertion(insertion);
				}
			}
			b.append(components);
		};
	}

	public CustomComponentBuilder(TextComponent... components) {
		this.append(components);
	}

	public CustomComponentBuilder append(TextComponent... components) {
		for (TextComponent component : components) {
			component = (TextComponent) component.duplicate();
			TextComponent previous = this.current;
			this.current = component;

			if (previous != null) {
				this.parts.add(previous);
			}
		}
		return this;
	}

	public TextComponent getCurrent() {
		return current;
	}

	public List<TextComponent> getParts() {
		List<TextComponent> l = new ArrayList<>(parts);
		if (current != null)
			l.add(current);
		return l;
	}

	public void setCurrent(TextComponent current) {
		this.current = current;
	}

	public CustomComponentBuilder(String text) {
		this.current = new TextComponent(text);
	}

	public CustomComponentBuilder append(String text) {
		return this.append(text, FormatRetention.ALL);
	}

	public CustomComponentBuilder append(String text, FormatRetention retention) {
		if (this.current != null) {
			this.parts.add(this.current);
		}
		this.current = this.current != null ? new TextComponent(this.current) : new TextComponent();
		this.current.setText(text);
		this.retain(retention);
		return this;
	}

	public CustomComponentBuilder color(ChatColor color) {
		this.current.setColor(color);
		return this;
	}

	public CustomComponentBuilder bold(boolean bold) {
		this.current.setBold(Boolean.valueOf(bold));
		return this;
	}

	public CustomComponentBuilder italic(boolean italic) {
		this.current.setItalic(Boolean.valueOf(italic));
		return this;
	}

	public CustomComponentBuilder underlined(boolean underlined) {
		this.current.setUnderlined(Boolean.valueOf(underlined));
		return this;
	}

	public CustomComponentBuilder strikethrough(boolean strikethrough) {
		this.current.setStrikethrough(Boolean.valueOf(strikethrough));
		return this;
	}

	public CustomComponentBuilder obfuscated(boolean obfuscated) {
		this.current.setObfuscated(Boolean.valueOf(obfuscated));
		return this;
	}

	public CustomComponentBuilder insertion(String insertion) {
		this.current.setInsertion(insertion);
		return this;
	}

	public CustomComponentBuilder event(ClickEvent clickEvent) {
		this.current.setClickEvent(clickEvent);
		return this;
	}

	public CustomComponentBuilder event(HoverEvent hoverEvent) {
		this.current.setHoverEvent(hoverEvent);
		return this;
	}

	public CustomComponentBuilder reset() {
		return this.retain(FormatRetention.NONE);
	}

	public CustomComponentBuilder retain(FormatRetention retention) {
		BaseComponent previous = this.current;
		switch (retention) {
		case NONE: {
			this.current = new TextComponent(this.current.getText());
			break;
		}
		case ALL: {
			break;
		}
		case EVENTS: {
			this.current = new TextComponent(this.current.getText());
			this.current.setInsertion(previous.getInsertion());
			this.current.setClickEvent(previous.getClickEvent());
			this.current.setHoverEvent(previous.getHoverEvent());
			break;
		}
		case FORMATTING: {
			this.current.setClickEvent(null);
			this.current.setHoverEvent(null);
		}
		}
		return this;
	}

	public TextComponent[] create() {
		if (this.current != null)
			this.parts.add(this.current);
		return this.parts.toArray(new TextComponent[this.parts.size()]);
	}

	public static enum FormatRetention {
		NONE,
		FORMATTING,
		EVENTS,
		ALL;

		private FormatRetention() {
		}
	}
}
