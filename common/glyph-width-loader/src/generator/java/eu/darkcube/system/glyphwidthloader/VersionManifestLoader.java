/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.glyphwidthloader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class VersionManifestLoader {
    public static final String URL = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";

    private final ConcurrentMap<String, VersionManifest> manifests = new ConcurrentHashMap<>();

    public CompletableFuture<Map<String, VersionManifest>> load() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(URL);
                URLConnection connection = url.openConnection();
                InputStream in = connection.getInputStream();
                byte[] bytes = in.readAllBytes();
                String data = new String(bytes, StandardCharsets.UTF_8);
                JsonObject object = new Gson().fromJson(data, JsonObject.class);
                JsonArray arrayVersions = object.get("versions").getAsJsonArray();
                for (JsonElement element : arrayVersions) {
                    JsonObject o = element.getAsJsonObject();
                    String id = o.get("id").getAsString();
                    String versionUrl = o.get("url").getAsString();
                    manifests.put(id, new VersionManifest(id, versionUrl));
                }
                JsonObject objectLatest = object.getAsJsonObject("latest");
                String latestSnapshot = objectLatest.get("snapshot").getAsString();
                String latestRelease = objectLatest.get("release").getAsString();

                manifests.put("latest", manifests.get(latestRelease));
                manifests.put("latestRelease", manifests.get(latestRelease));
                manifests.put("latestSnapshot", manifests.get(latestSnapshot));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return manifests;
        });
    }

    public ConcurrentMap<String, VersionManifest> manifests() {
        return manifests;
    }
}
