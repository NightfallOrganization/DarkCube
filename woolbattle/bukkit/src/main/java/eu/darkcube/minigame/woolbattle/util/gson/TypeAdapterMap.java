/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import eu.darkcube.minigame.woolbattle.map.DefaultMap;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.map.MapSize;
import eu.darkcube.minigame.woolbattle.util.GsonSerializer;
import eu.darkcube.system.server.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;

public class TypeAdapterMap implements JsonSerializer<Map>, JsonDeserializer<Map> {

    public static final TypeAdapterMap INSTANCE = new TypeAdapterMap();

    private TypeAdapterMap() {
    }

    @Override public Map deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject o = json.getAsJsonObject();
        String name = o.get("name").getAsString();
        boolean enabled = o.get("enabled").getAsBoolean();
        int deathHeight = o.get("deathHeight").getAsInt();
        MapSize size = GsonSerializer.gson.fromJson(o.get("mapSize"), MapSize.class);
        ItemStack icon = ItemBuilder
                .item(new eu.darkcube.system.libs.com.google.gson.Gson().fromJson(o
                        .get("icon")
                        .toString(), eu.darkcube.system.libs.com.google.gson.JsonElement.class))
                .build();
        return new DefaultMap(name, enabled, deathHeight, icon, size, null);
    }

    @Override public JsonElement serialize(Map map, Type type, JsonSerializationContext context) {
        JsonObject o = new JsonObject();
        o.add("deathHeight", new JsonPrimitive(map.deathHeight()));
        o.add("enabled", new JsonPrimitive(map.isEnabled()));
        o.add("name", new JsonPrimitive(map.getName()));
        o.add("icon", GsonSerializer.gson.fromJson(ItemBuilder.item(map.getIcon()).serialize().toString(), JsonElement.class));
        o.add("mapSize", GsonSerializer.gson.toJsonTree(map.size()));
        return o;
    }
}
