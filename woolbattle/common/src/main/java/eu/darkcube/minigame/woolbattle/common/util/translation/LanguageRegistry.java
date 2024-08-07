/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util.translation;

import static eu.darkcube.minigame.woolbattle.common.util.translation.Messages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.system.util.Language;

public class LanguageRegistry {
    public void register() {
        try {
            register(Language.GERMAN);
            register(Language.ENGLISH);

            var entries = new ArrayList<String>();
            entries.addAll(Arrays.stream(Messages.values()).map(Messages::key).toList());
            entries.addAll(Arrays.stream(Items.values()).flatMap(this::names).toList());

            Language.validateEntries(entries.toArray(String[]::new), KEY_MODIFIER);
        } catch (Throwable throwable) {
            throw new Error(throwable);
        }
    }

    private Stream<String> names(Items item) {
        var name = ITEM_PREFIX + item.key();
        var lore = ITEM_PREFIX + LORE_PREFIX + item.key();
        switch (item) {
            case NEXT_PAGE, NEXT_PAGE_UNUSABLE, PREV_PAGE, PREV_PAGE_UNUSABLE, GRAY_GLASS_PANE, BLACK_GLASS_PANE -> lore = null;
        }
        return Stream.of(name, lore).filter(Objects::nonNull);
    }

    private void register(Language language) throws IOException {
        var loader = getClass().getClassLoader();
        var tag = language.getLocale().toLanguageTag();
        language.registerLookup(loader, "messages_" + tag + ".properties", KEY_MODIFIER);
        language.registerLookup(loader, "items_" + tag + ".properties", ITEM_MODIFIER);
        language.registerLookup(loader, "items_lore_" + tag + ".properties", LORE_MODIFIER);
    }

    public void unregister() {
        // There is no way to unregister messages at this time.
        // It is no problem though, once a key is registered a second time, the original value is overridden.
    }
}
