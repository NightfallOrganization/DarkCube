/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util.scoreboard;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.scoreboard.DisplaySlot;

public class Objective {

    private org.bukkit.scoreboard.Objective obj;

    public Objective(org.bukkit.scoreboard.Objective obj) {
        this.obj = obj;
    }

    public void setDisplayName(Component name) {
        obj.setDisplayName(LegacyComponentSerializer.legacySection().serialize(name));
    }

    public String getDisplayName() {
        return obj.getDisplayName();
    }

    public org.bukkit.scoreboard.Objective getObjective() {
        return obj;
    }

    public void setDisplaySlot(DisplaySlot slot) {
        obj.setDisplaySlot(slot);
    }

    public void unsetDisplaySlot() {
        setDisplaySlot(null);
    }

    public void setScore(String entry, int score) {
        obj.getScore(entry).setScore(score);
    }

    public int getScore(String entry) {
        return obj.getScore(entry).getScore();
    }
}
