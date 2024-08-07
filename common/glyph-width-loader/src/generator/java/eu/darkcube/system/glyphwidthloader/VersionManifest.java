/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.glyphwidthloader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class VersionManifest {

    private final String id;
    private final String url;

    public VersionManifest(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public String id() {
        return id;
    }

    public String url() {
        return url;
    }

    public CompletableFuture<Resolved> resolve() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(url());
                URLConnection connection = url.openConnection();
                InputStream in = connection.getInputStream();
                byte[] bytes = in.readAllBytes();
                String data = new String(bytes, StandardCharsets.UTF_8);
                JsonObject json = new Gson().fromJson(data, JsonObject.class);
                return new Resolved(id(), url(), json);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static class Resolved extends VersionManifest {

        private final JsonObject json;

        public Resolved(String id, String url, JsonObject json) {
            super(id, url);
            this.json = json;
        }

        public String clientUrl() {
            return json.get("downloads").getAsJsonObject().get("client").getAsJsonObject().get("url").getAsString();
        }

        public JsonObject json() {
            return json;
        }
    }

}
