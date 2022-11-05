package eu.darkcube.system.language.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;

public enum Language {

	GERMAN(Locale.GERMAN), ENGLISH(Locale.ENGLISH)

	;

	private static final Database DATABASE = CloudNetDriver.getInstance().getDatabaseProvider()
			.getDatabase("user_languages");
	public static final Language DEFAULT = Language.GERMAN;

	private final Locale locale;
	private final Bundle bundle;

	private Language(Locale locale) {
		this.locale = locale;
		this.bundle = new Bundle();
	}

	public String getMessage(String key, Object... replacements) {
		if (this.bundle.containsKey(key)) {
			return String.format(this.locale, this.bundle.getObject(key).toString(), replacements);
		}
		return new StringBuilder()
				.append(key).append('[').append(String.join(", ", Arrays.asList(replacements).stream()
						.map(String::valueOf).collect(Collectors.toList()).toArray(new String[0])))
				.append(']').toString();
	}

	public static void setLanguage(UUID uuid, Language language) {
		JsonDocument document = new JsonDocument().append("language", language);
		Language.getDatabase().containsAsync(uuid.toString()).onComplete(b -> {
			if (b) {
				Language.getDatabase().updateAsync(uuid.toString(), document);
			} else {
				Language.getDatabase().insertAsync(uuid.toString(), document);
			}
		});
	}

	public static void validateEntries(String[] entrySet, Function<String, String> keyModifier) {
		for (Language language : Language.values()) {
			language.validate(entrySet, keyModifier);
		}
	}

	public void validate(String[] entrySet, Function<String, String> keyModifier) {
		for (String key : entrySet) {
			String mapped = keyModifier.apply(key);
			if (!this.bundle.containsKey(mapped)) {
				System.out.println("Missing translation for language " + this.toString() + ": " + mapped);
			}
		}
	}

	public static ITask<Language> getLanguageAsync(UUID uuid) {
		return Language.getDatabase().getAsync(uuid.toString())
				.map(json -> json == null ? Language.DEFAULT : json.get("language", Language.class));
	}

	public static Language getLanguage(UUID uuid) {
		return Language.getLanguageAsync(uuid).getDef(Language.DEFAULT);
	}

	public void registerLookup(Map<String, Object> lookup, Function<String, String> keyModifier) {
		this.bundle.append(lookup, keyModifier);
	}

	public void registerLookup(Properties properties, Function<String, String> keyModifier) {
		this.bundle.append(properties, keyModifier);
	}

	public void registerLookup(Reader reader, Function<String, String> keyModifier) throws IOException {
		Properties properties = new Properties();
		properties.load(reader);
		this.registerLookup(properties, keyModifier);
	}

	public static Database getDatabase() {
		return Language.DATABASE;
	}

	public void registerLookup(ClassLoader loader, String path, Function<String, String> keyModifier)
			throws IOException {
		this.registerLookup(Language.getReader(Language.getResource(loader, path)), keyModifier);
	}

	public void registerLookup(ClassLoader loader, String path) throws IOException {
		this.registerLookup(loader, path, s -> s);
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

	public Locale getLocale() {
		return this.locale;
	}

	public ResourceBundle getBundle() {
		return this.bundle;
	}

	public static Language fromString(String language) {
		for (Language l : Language.values()) {
			if (l.name().equalsIgnoreCase(language)) {
				return l;
			}
		}
		return GERMAN;
	}

	private class Bundle extends ResourceBundle {

		private Map<String, Object> lookup = new HashMap<>();

		public void append(Map<String, Object> lookup, Function<String, String> keyModifier) {
			for (String key : lookup.keySet()) {
				if (this.lookup.containsKey(keyModifier.apply(key))) {
					System.out.println("[LanguageAPI] Overriding translation: " + keyModifier.apply(key));
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
		protected Object handleGetObject(String key) {
			if (key == null) {
				throw new NullPointerException();
			}
			return this.lookup.get(key);
		}

		@Override
		public Enumeration<String> getKeys() {
			ResourceBundle parent = this.parent;
			return new ResourceBundleEnumeration(this.lookup.keySet(), (parent != null) ? parent.getKeys() : null);
		}

		@Override
		public Set<String> handleKeySet() {
			return this.lookup.keySet();
		}

		public class ResourceBundleEnumeration implements Enumeration<String> {

			Set<String> set;
			Iterator<String> iterator;
			Enumeration<String> enumeration; // may remain null

			/**
			 * Constructs a resource bundle enumeration.
			 * 
			 * @param set         an set providing some elements of the enumeration
			 * @param enumeration an enumeration providing more elements of the enumeration.
			 *                    enumeration may be null.
			 */
			public ResourceBundleEnumeration(Set<String> set, Enumeration<String> enumeration) {
				this.set = set;
				this.iterator = set.iterator();
				this.enumeration = enumeration;
			}

			String next = null;

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
