/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.translation;

import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.system.util.Language;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LanguageHelper {
    public static void load() {
        // Load all messages
        try {
            ClassLoader cl = LanguageHelper.class.getClassLoader();
            Language.GERMAN.registerLookup(cl, "messages_de.properties", Message.KEY_MODFIIER);
            Language.ENGLISH.registerLookup(cl, "messages_en.properties", Message.KEY_MODFIIER);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        List<String> languageEntries = new ArrayList<>();
        languageEntries.addAll(Arrays.asList(Message.values()).stream().map(Message::key).collect(Collectors.toList()));
        languageEntries.addAll(Arrays.asList(Item.values()).stream().map(i -> Message.ITEM_PREFIX + i.getKey()).collect(Collectors.toList()));
        languageEntries.addAll(Arrays.asList(Item.values()).stream().filter(i -> i.getBuilder().lore().size() > 0).map(i -> Message.ITEM_PREFIX + Message.LORE_PREFIX + i.getKey()).collect(Collectors.toList()));
        Language.validateEntries(languageEntries.toArray(new String[0]), s -> Message.KEY_PREFIX + s);
    }
}
