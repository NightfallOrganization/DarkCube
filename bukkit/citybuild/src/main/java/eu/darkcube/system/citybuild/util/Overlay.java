/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.util;

import eu.darkcube.system.citybuild.Citybuild;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public enum Overlay {
    CRAFTING_TABLE('Ḇ', -39),
    WARP_INVENTORY('ḇ', -17),
    ;
    private final char character;
    private final float offset;
    private final Component component;
    private final eu.darkcube.system.libs.net.kyori.adventure.text.Component darkcubeComponent;

    Overlay(char character, float offset) {
        this.character = character;
        this.offset = offset;
        String offsetText = Citybuild.getInstance().glyphWidthManager().spacesForWidth(offset);
        this.component = Component.text(offsetText).append(Component.text(character, NamedTextColor.WHITE));
        this.darkcubeComponent = eu.darkcube.system.libs.net.kyori.adventure.text.Component
                .text(offsetText)
                .append(eu.darkcube.system.libs.net.kyori.adventure.text.Component.text(character, eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor.WHITE));
    }

    public char character() {
        return character;
    }

    public float offset() {
        return offset;
    }

    public Component component() {
        return component;
    }

    public eu.darkcube.system.libs.net.kyori.adventure.text.Component darkcubeComponent() {
        return darkcubeComponent;
    }
}
