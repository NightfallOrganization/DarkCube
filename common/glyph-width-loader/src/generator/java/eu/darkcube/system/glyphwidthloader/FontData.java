/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.glyphwidthloader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2FloatMap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FontData {
    private static final ResourceLocation DEFAULT = new ResourceLocation("minecraft", "default");
    private final List<Provider> providers = new ArrayList<>();

    public static FontData load(Path assets) throws IOException {
        Gson gson = new Gson();
        FontData fontData = new FontData();
        append(gson, assets, DEFAULT, fontData);
        return fontData;
    }

    private static void append(Gson gson, Path assets, ResourceLocation path, FontData fontData) throws IOException {
        String data = Files.readString(assets.resolve(path.namespace()).resolve("font").resolve(path.path() + ".json"));
        JsonObject json = gson.fromJson(data, JsonObject.class);
        JsonArray providers = json.get("providers").getAsJsonArray();
        for (JsonElement element : providers) {
            JsonObject providerJson = element.getAsJsonObject();
            Provider provider = new Provider(providerJson);
            if (provider.type().equals("reference")) {
                String id = provider.string("id");
                ResourceLocation loc = ResourceLocation.fromString(id);
                append(gson, assets, loc, fontData);
            } else {
                fontData.providers.add(provider);
            }
        }
    }

    public List<Provider> providers() {
        return providers;
    }

    @Override public String toString() {
        return "FontData{" + "providers=" + providers + '}';
    }

    public record Provider(JsonObject json) {
        public static void space(Provider provider, Int2FloatMap widths) {
            JsonObject advances = provider.json().get("advances").getAsJsonObject();
            for (String key : advances.keySet()) {
                float advance = advances.get(key).getAsFloat();
                widths.put(key.codePointAt(0), advance);
            }
        }

        public static void bitmap(Path assets, Provider provider, Int2FloatMap widths) throws IOException {
            ResourceLocation loc = ResourceLocation.fromString(provider.string("file"));
            Path path = assets.resolve(loc.namespace()).resolve("textures").resolve(loc.path());
            InputStream in = Files.newInputStream(path);
            BufferedImage image = ImageIO.read(in);
            in.close();

            int displayHeight = 8;
            if (provider.json().has("height")) displayHeight = provider.json().get("height").getAsInt();

            JsonArray charsArray = provider.json().get("chars").getAsJsonArray();
            int[][] codepointMap = new int[charsArray.size()][];
            int y = 0;
            for (JsonElement element : charsArray) {
                String characters = element.getAsString();
                codepointMap[y++] = characters.codePoints().toArray();
            }

            int bitmapWidth = image.getWidth();
            int bitmapHeight = image.getHeight();
            int glyphWidth = bitmapWidth / codepointMap[0].length;
            int glyphHeight = bitmapHeight / codepointMap.length;
            float scale = displayHeight / (float) glyphHeight;

            for (y = 0; y < codepointMap.length; y++) {
                for (int x = 0; x < codepointMap[y].length; x++) {
                    int actualWidth = actualWidth(image, glyphWidth, glyphHeight, x, y);
                    int advance = (int) (0.5D + (double) ((float) actualWidth * scale)) + 1;
                    widths.put(codepointMap[y][x], advance);
                }
            }
        }

        private static int actualWidth(BufferedImage image, int glyphWidth, int glyphHeight, int xIndex, int yIndex) {
            int i;
            for (i = glyphWidth - 1; i >= 0; --i) {
                int j = xIndex * glyphWidth + i;

                for (int k = 0; k < glyphHeight; ++k) {
                    int l = yIndex * glyphHeight + k;
                    int pixel = image.getRGB(j, l);
                    int alpha = (pixel >> 24) & 0xff;
                    if (alpha != 0) {
                        return i + 1;
                    }
                }
            }

            return i + 1;
        }

        public String type() {
            return string("type");
        }

        public String string(String key) {
            return json.get(key).getAsString();
        }

        @Override public String toString() {
            return json.toString();
        }
    }
}
