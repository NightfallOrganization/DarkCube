/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver.heads.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import eu.darkcube.system.bauserver.heads.Head;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.GsonBuilder;
import eu.darkcube.system.libs.com.google.gson.JsonArray;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinecraftHeadsComProvider implements RemoteHeadProvider {
    private static final String URL = "https://minecraft-heads.com/scripts/api.php?tags=true&cat=%s";
    private static final Logger LOGGER = LoggerFactory.getLogger(MinecraftHeadsComProvider.class);
    private final Gson gson = new GsonBuilder().create();

    @Override
    public String name() {
        return "Minecraft-Heads.com";
    }

    @Override
    public List<String> categories() {
        return List.of("alphabet", "animals", "blocks", "decoration", "food-drinks", "humans", "humanoid", "miscellaneous", "monsters", "plants");
    }

    @Override
    public List<Head> heads(String category) {
        try {
            var url = URI.create(URL.formatted(category)).toURL();
            var connection = (HttpURLConnection) url.openConnection();
            try (var reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                var jsonArray = gson.fromJson(reader, JsonArray.class);
                return heads(category, jsonArray);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to get heads from minecraft-heads.com", e);
            return List.of();
        }
    }

    private @NotNull ArrayList<Head> heads(String category, JsonArray jsonArray) {
        var heads = new ArrayList<Head>();
        for (var element : jsonArray) {
            var json = element.getAsJsonObject();
            var name = json.get("name").getAsString();
            var texture = json.get("value").getAsString();
            var tagsString = json.has("tags") ? json.get("tags").getAsString() : null;
            var tags = tagsString == null ? List.<String>of() : List.of(tagsString.split(","));
            var head = new Head(name, texture, category, this.name(), tags);
            heads.add(head);
        }
        return heads;
    }
}
