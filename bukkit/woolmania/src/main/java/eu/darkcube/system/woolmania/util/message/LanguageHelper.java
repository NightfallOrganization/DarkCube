/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.util.message;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import eu.darkcube.system.util.Language;

public class LanguageHelper {

    public static void initialize() {
        try {
            loadLanguage(Language.GERMAN);
            loadLanguage(Language.ENGLISH);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void loadLanguage(Language language) throws IOException {
        ClassLoader loader = LanguageHelper.class.getClassLoader();
        String tag = language.getLocale().toLanguageTag();
        loadLanguage(language, "messages_" + tag + ".properties");
        language.registerLookup(loader, "messages_pure_" + tag + ".properties", Message.KEY_MODIFIER);
        language.registerLookup(loader, "items_" + tag + ".properties", Message.ITEM_MODIFIER);
    }

    private static void loadLanguage(Language language, String path) throws IOException {
        Properties properties = new Properties();
        Reader reader = Language.getReader(LanguageHelper.class.getClassLoader().getResourceAsStream(path));
        properties.load(reader);
        reader.close();
        for (Object o : properties.keySet()) {
            String key = (String) o;
            String translation = properties.getProperty(key);
            if (key.equals("ZINA_GET_MONEY") || key.equals("ZINA_NO_WOOL")) {
                translation = "§7[§bZina§7] " + translation;
                properties.setProperty(key, translation);
            } else if (key.equals("HALLS_RESET") || key.equals("TIMER_IS_OVER") || key.equals("TIMER_IS_OVER_SECOUND")) {
                properties.setProperty(key, translation);
            } else {
                translation = "§7[§bWool§3Mania§7] " + translation;
                properties.setProperty(key, translation);
            }
        }
        language.registerLookup(properties, Message.KEY_MODIFIER);
    }
}
