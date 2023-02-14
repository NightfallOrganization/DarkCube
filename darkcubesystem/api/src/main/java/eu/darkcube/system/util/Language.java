/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.util;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.ComponentLike;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import org.bukkit.ChatColor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.*;
import java.util.function.Function;

public enum Language {

	GERMAN(Locale.GERMAN), ENGLISH(Locale.ENGLISH);

	public static final Language DEFAULT = Language.GERMAN;

	private final Locale locale;
	private final Bundle bundle;

	Language(Locale locale) {
		this.locale = locale;
		this.bundle = new Bundle();
	}

	public static void validateEntries(String[] entrySet, Function<String, String> keyModifier) {
		for (Language language : Language.values()) {
			language.validate(entrySet, keyModifier);
		}
	}

	public static InputStream getResource(ClassLoader loader, String path) {
		return loader.getResourceAsStream(path);
	}

	public static Reader getReader(InputStream stream) {
		return new InputStreamReader(stream, Language.getCharset());
	}

	public static Charset getCharset() {
		return StandardCharsets.UTF_8;
	}

	public static Language fromString(String language) {
		for (Language l : Language.values()) {
			if (l.name().equalsIgnoreCase(language)) {
				return l;
			}
		}
		return GERMAN;
	}

	public boolean containsMessage(String key) {
		return bundle.containsKey(key);
	}

	public Component getMessage(String key, Object... replacements) {
		replacements = Arrays.copyOf(replacements, replacements.length);
		List<Component> components = new ArrayList<>();
		for (int i = 0; i < replacements.length; i++) {
			if (replacements[i] instanceof ComponentLike) {
				ComponentLike componentLike = (ComponentLike) replacements[i];
				replacements[i] = (Formattable) (formatter, flags, width, precision) -> {
					int index = components.size();
					components.add(componentLike.asComponent());
					formatter.format("&#!$%s%s;", (char) 1054, index);
				};
			}
		}
		if (this.bundle.containsKey(key)) {
			String formatted =
					String.format(this.locale, this.bundle.getObject(key).toString(), replacements);
			formatted = ChatColor.translateAlternateColorCodes('&', formatted);
			Component c = Component.empty();
			for (int i = 0; i < components.size(); i++) {
				String[] s = formatted.split(String.format("&#!\\$%s%s;", (char) 1054, i), 2);
				c = c.append(LegacyComponentSerializer.legacySection().deserialize(s[0]));
				Component o = c;
				if (s.length == 2) {
					formatted = s[1];
					c = c.append(components.get(i));
					String str = LegacyComponentSerializer.legacySection()
							.serialize(Component.text(" ").style(getLastStyle(o)));
					str = str.substring(0, str.length() - 1);
					formatted = str + formatted;
					//					c = c.append(LegacyComponentSerializer.legacySection().deserialize(str + s[1]));
				}
			}
			//			if (components.isEmpty()) {
			c = LegacyComponentSerializer.legacySection().deserialize(formatted);
			//			}
			return c;
		} else {
			return LegacyComponentSerializer.legacySection().deserialize(
					key + '[' + String.join(", ",
							Arrays.stream(replacements).map(String::valueOf).toArray(String[]::new))
							+ ']');
		}
	}

	private Style getLastStyle(Component c) {
		return c.children().isEmpty()
				? c.style()
				: c.children().get(c.children().size() - 1).style();
	}

	public void validate(String[] entrySet, Function<String, String> keyModifier) {
		for (String key : entrySet) {
			String mapped = keyModifier.apply(key);
			if (!this.bundle.containsKey(mapped)) {
				System.out.println("Missing translation for language " + this + ": " + mapped);
			}
		}
	}

	public void registerLookup(Map<String, Object> lookup, Function<String, String> keyModifier) {
		this.bundle.append(lookup, keyModifier);
	}

	public void registerLookup(Properties properties, Function<String, String> keyModifier) {
		this.bundle.append(properties, keyModifier);
	}

	public void registerLookup(Reader reader, Function<String, String> keyModifier)
	throws IOException {
		Properties properties = new Properties();
		properties.load(reader);
		this.registerLookup(properties, keyModifier);
	}

	public void registerLookup(ClassLoader loader, String path,
			Function<String, String> keyModifier) throws IOException {
		this.registerLookup(Language.getReader(Language.getResource(loader, path)), keyModifier);
	}

	public void registerLookup(ClassLoader loader, String path) throws IOException {
		this.registerLookup(loader, path, s -> s);
	}

	public Locale getLocale() {
		return this.locale;
	}

	private static class Bundle extends ResourceBundle {

		private Map<String, Object> lookup = new HashMap<>();

		public void append(Map<String, Object> lookup, Function<String, String> keyModifier) {
			for (String key : lookup.keySet()) {
				if (this.lookup.containsKey(keyModifier.apply(key))) {
					System.out.println(
							"[LanguageAPI] Overriding translation: " + keyModifier.apply(key));
				}
				this.lookup.put(keyModifier.apply(key), lookup.get(key));
			}
		}

		public void append(Properties properties, Function<String, String> keyModifier) {
			Map<String, Object> m = new HashMap<>();
			for (Map.Entry<Object, Object> s : properties.entrySet()) {
				m.put(s.getKey().toString(), s.getValue());
			}
			this.append(m, keyModifier);
		}

		@Override
		protected Object handleGetObject(@NotNull String key) {
			return this.lookup.get(key);
		}

		@Override
		public @NotNull Enumeration<String> getKeys() {
			ResourceBundle parent = this.parent;
			return new ResourceBundleEnumeration(this.lookup.keySet(),
					(parent != null) ? parent.getKeys() : null);
		}

		@Override
		public @NotNull Set<String> handleKeySet() {
			return this.lookup.keySet();
		}

		public static class ResourceBundleEnumeration implements Enumeration<String> {

			Set<String> set;
			Iterator<String> iterator;
			Enumeration<String> enumeration; // may remain null
			String next = null;

			/**
			 * Constructs a resource bundle enumeration.
			 *
			 * @param set         a set providing some elements of the enumeration
			 * @param enumeration an enumeration providing more elements of the enumeration.
			 *                    enumeration may be null.
			 */
			public ResourceBundleEnumeration(Set<String> set, Enumeration<String> enumeration) {
				this.set = set;
				this.iterator = set.iterator();
				this.enumeration = enumeration;
			}

			@Override
			public boolean hasMoreElements() {
				if (this.next == null) {
					if (this.iterator.hasNext()) {
						this.next = this.iterator.next();
					} else if (this.enumeration != null) {
						while (this.next == null && this.enumeration.hasMoreElements()) {
							this.next = this.enumeration.nextElement();
							if (this.set.contains(this.next)) {
								this.next = null;
							}
						}
					}
				}
				return this.next != null;
			}

			@Override
			public String nextElement() {
				if (this.hasMoreElements()) {
					String result = this.next;
					this.next = null;
					return result;
				}
				throw new NoSuchElementException();
			}
		}
	}
}
