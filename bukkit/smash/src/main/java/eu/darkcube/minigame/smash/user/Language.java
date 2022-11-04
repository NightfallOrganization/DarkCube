package eu.darkcube.minigame.smash.user;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

public enum Language {

	ENGLISH(Locale.ENGLISH), GERMAN(Locale.GERMAN),

	;

	private final Locale locale;
	private ResourceBundle bundle;

	Language(Locale locale) {
		this.locale = locale;
		try {
//			this.bundle = ResourceBundle.getBundle("messages", this.locale,
//					Control.getNoFallbackControl(Control.FORMAT_DEFAULT));
			String name = Control.getControl(Control.FORMAT_DEFAULT).toBundleName("messages", this.locale);
			InputStream in = getClass().getClassLoader().getResourceAsStream(name + ".properties");
			if(in == null) {
				in = getClass().getClassLoader().getResourceAsStream("messages.properties");
			}
			InputStreamReader r = new InputStreamReader(in, Charset.forName("UTF-8"));
			bundle = new PropertyResourceBundle(r);
		} catch (MissingResourceException ex) {
			System.out.println(ex.getMessage());
			this.bundle = null;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static Language fromString(String language) {
		for (Language l : Language.values()) {
			if (l.name().equalsIgnoreCase(language)) {
				return l;
			}
		}
		return GERMAN;
	}

	public ResourceBundle getBundle() {
		return this.bundle;
	}

	public Locale getLocale() {
		return locale;
	}
}
