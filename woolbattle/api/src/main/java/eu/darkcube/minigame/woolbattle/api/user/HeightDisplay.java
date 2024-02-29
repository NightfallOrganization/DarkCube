/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.user;

import java.util.logging.Logger;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.libs.net.kyori.adventure.Adventure;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.util.AdventureSupport;
import eu.darkcube.system.util.data.PersistentDataType;

public final class HeightDisplay implements Cloneable {
    private static final Logger logger = Logger.getLogger("HeightDisplay");
    public static final PersistentDataType<HeightDisplay> TYPE = new PersistentDataType<>() {
        @Override public HeightDisplay deserialize(Document doc, String key) {
            var d = doc.readDocument(key);
            var enabled = d.getBoolean("enabled");
            var maxDistance = d.getInt("maxDistance");
            var colorString = d.getString("color");
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

        @Override public void serialize(Document.Mutable doc, String key, HeightDisplay data) {
            var d = Document.newJsonDocument();
            d.append("enabled", data.enabled());
            d.append("maxDistance", data.maxDistance());
            d.append("color", data.color().asHexString());
            doc.append(key, d);
        }

        @Override public HeightDisplay clone(HeightDisplay object) {
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

    @Override public HeightDisplay clone() {
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
