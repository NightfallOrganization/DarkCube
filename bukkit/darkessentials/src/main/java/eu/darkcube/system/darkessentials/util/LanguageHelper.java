/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.util;

import eu.darkcube.system.util.Language;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

public class LanguageHelper {

    public static void initialize() {
        try {
            loadLanguage(Language.GERMAN, "messages_de.properties");
            loadLanguage(Language.ENGLISH, "messages_en.properties");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void loadLanguage(Language language, String path) throws IOException {
        Properties properties = new Properties();
        Reader reader = Language.getReader(LanguageHelper.class.getClassLoader().getResourceAsStream(path));
        properties.load(reader);
        reader.close();
        for (Object o : properties.keySet()) {
            String key = (String) o;
            String translation = properties.getProperty(key);
            translation = "§7[§6Dark§eCube§7] " + translation;
            properties.setProperty(key, translation);
        }
        language.registerLookup(properties, s -> Message.KEY_PREFIX + s);
    }
}
