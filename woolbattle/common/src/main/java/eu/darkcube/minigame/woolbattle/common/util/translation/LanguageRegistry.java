/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util.translation;

import static eu.darkcube.minigame.woolbattle.common.util.translation.Messages.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.system.util.Language;

public class LanguageRegistry {
    public void register() {
        try {
            register(Language.GERMAN);
            register(Language.ENGLISH);

            Language.validateEntries(Arrays.stream(Messages.values()).map(Messages::key).toArray(String[]::new), KEY_MODIFIER);
            Language.validateEntries(Arrays.stream(Items.values()).flatMap(this::names).toArray(String[]::new), Function.identity());
        } catch (Throwable throwable) {
            throw new Error(throwable);
        }
    }

    private Stream<String> names(Items item) {
        var name = ITEM_MODIFIER.apply(item.key());
        var lore = LORE_MODIFIER.apply(item.key());
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
