/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.user;

import java.util.logging.Logger;

import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.util.data.PersistentDataType;

public final class HeightDisplay implements Cloneable {
    private static final Logger logger = Logger.getLogger("HeightDisplay");
    public static final PersistentDataType<HeightDisplay> TYPE = new PersistentDataType<>() {
        @Override
        public HeightDisplay deserialize(JsonElement json) {
            var d = json.getAsJsonObject();
            var enabled = d.get("enabled").getAsBoolean();
            var maxDistance = d.get("maxDistance").getAsInt();
            var colorString = d.get("color").getAsString();
            TextColor color = null;
            if (colorString.length() == 1) {
                var format = LegacyComponentSerializer.parseChar(colorString.charAt(0));
                if (format != null) color = format.color();
            }
            if (color == null) color = TextColor.fromHexString(colorString);
            if (color == null) {
                logger.severe("Failed to deserialize HeightDisplay color: " + colorString);
                color = HeightDisplay.getDefault().color();
            }
            return new HeightDisplay(enabled, maxDistance, color);
        }

        @Override
        public JsonElement serialize(HeightDisplay data) {
            var d = new JsonObject();
            d.addProperty("enabled", data.enabled());
            d.addProperty("maxDistance", data.maxDistance());
            d.addProperty("color", data.color().asHexString());
            return d;
        }

        @Override
        public HeightDisplay clone(HeightDisplay object) {
            return object.clone();
        }
    };
    private boolean enabled;
    private int maxDistance;
    private TextColor color;

    public HeightDisplay(boolean enabled, int maxDistance, TextColor color) {
        this.enabled = enabled;
        this.maxDistance = maxDistance;
        this.color = color;
    }

    public static HeightDisplay getDefault() {
        return new HeightDisplay(true, -1, NamedTextColor.GOLD);
    }

    @Override
    public HeightDisplay clone() {
        return new HeightDisplay(enabled, maxDistance, color);
    }

    public int maxDistance() {
        return maxDistance;
    }

    public void maxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public boolean enabled() {
        return enabled;
    }

    public void enabled(boolean enabled) {
        this.enabled = enabled;
    }

    public TextColor color() {
        return color;
    }

    public void color(TextColor color) {
        this.color = color;
    }
}
