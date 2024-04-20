package eu.darkcube.minigame.woolbattle.common.util.translation;

import static eu.darkcube.minigame.woolbattle.common.util.translation.Messages.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.system.util.Language;

public class LanguageRegistry {
    public void register() {
        try {
            var loader = getClass().getClassLoader();
            Language.GERMAN.registerLookup(loader, "messages_de.properties", KEY_MODFIIER);
            Language.ENGLISH.registerLookup(loader, "messages_en.properties", KEY_MODFIIER);

            var entries = new ArrayList<String>();
            entries.addAll(Arrays.stream(Messages.values()).map(Messages::key).toList());
            entries.addAll(Arrays.stream(Items.values()).flatMap(i -> Stream.of(ITEM_PREFIX + i.key(), ITEM_PREFIX + LORE_PREFIX + i.key())).toList());

            Language.validateEntries(entries.toArray(String[]::new), KEY_MODFIIER);
        } catch (Throwable throwable) {
            throw new Error(throwable);
        }
    }

    public void unregister() {
        // There is no way to unregister messages at this time.
        // It is no problem though, once a key is registered a second time, the original value is overridden.
    }
}
