package eu.darkcube.minigame.woolbattle.common.map;

import java.util.HashMap;
import java.util.Map;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.minigame.woolbattle.api.map.MapIngameData;
import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import io.leangen.geantyref.TypeToken;

public class CommonMapIngameData implements MapIngameData {
    private final Map<String, Position.Directed> spawns = new HashMap<>();
    private final CommonMap map;

    public CommonMapIngameData(CommonMap map) {
        this.map = map;
    }

    @Override
    public @NotNull CommonMap map() {
        return map;
    }

    @Override
    public @Nullable Position.Directed spawn(@NotNull String name) {
        return spawns.get(name);
    }

    @Override
    public void spawn(@NotNull String name, Position.@NotNull Directed position) {
        spawns.put(name, Position.Directed.simple(position));
    }

    @Override
    public void removeSpawn(@NotNull String name) {
        spawns.remove(name);
    }

    void readFromDocument(Document document) {
        var map = document.toInstanceOf(new TypeToken<HashMap<String, Position.Directed.Simple>>() {
        });
        this.spawns.putAll(map);
    }

    Document toDocument() {
        var doc = Document.newJsonDocument();
        doc.appendTree(spawns);
        return doc;
    }
}
