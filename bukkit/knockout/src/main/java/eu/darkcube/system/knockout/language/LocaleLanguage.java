package eu.darkcube.system.knockout.language;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.regex.*;

import com.google.common.base.*;
import com.google.common.collect.*;

public class LocaleLanguage {

	private static final Pattern pattern = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
	private static final Splitter splitter = Splitter.on('=').limit(2);

	private static final Set<LocaleLanguage> languages = new HashSet<>();

	public static final Map<String, LocaleLanguage> languageMap = new HashMap<>();
	public static final LocaleLanguage EN_US = new LocaleLanguage(new Locale("en", "US"));
	public static final LocaleLanguage DE_DE = new LocaleLanguage(new Locale("de", "DE"));

	private final Locale locale;
	private final Map<String, String> data = new HashMap<>();

	static {
		languageMap.put("GERMAN", DE_DE);
		languageMap.put("ENGLISH", EN_US);
	}

	public static LocaleLanguage getLanguage(Locale locale) {
		for (LocaleLanguage lang : languages) {
			if (lang.locale.getLanguage().equals(locale.getLanguage())) {
				return lang;
			}
		}
		return DE_DE;
	}

	public LocaleLanguage(Locale locale) {
		this.locale = locale;
		languages.add(this);
		try (BufferedReader r = new BufferedReader(new InputStreamReader(
				LocaleLanguage.class.getClassLoader().getResourceAsStream("languages/" + locale.toString() + ".lang"),
				StandardCharsets.UTF_8))) {
			String line;
			while ((line = r.readLine()) != null) {
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}
				final String[] array = Iterables.toArray(splitter.split(line), String.class);
				if (array == null) {
					continue;
				}
				if (array.length != 2) {
					continue;
				}
				this.data.put(array[0], pattern.matcher(array[1]).replaceAll("%§1s"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public final String getMessageUnsafe(String key) {
		return this.data.get(key);
	}

	public final String getMessage(String key) {
		return data.containsKey(key) ? getMessageUnsafe(key) : key;
	}

	public final String getMessageUnsafe(LanguageKey key) {
		return getMessageUnsafe(key.getKey());
	}

	public final String getMessage(LanguageKey key) {
		return getMessage(key.getKey());
	}

	public final String format(LanguageKey key, Object... objects) {
		return format(key.getKey(), objects);
	}

	public final String format(String key, Object... objects) {
		if (objects == null) {
			objects = new Object[0];
		}
		final String msg = this.getMessage(key);
		try {
			return String.format(msg, objects);
		} catch (Exception ex) {
			return "Format error: " + key;
		}
	}
}
