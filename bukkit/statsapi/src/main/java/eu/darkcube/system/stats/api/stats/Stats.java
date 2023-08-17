/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.stats.api.stats;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.stats.api.Duration;
import eu.darkcube.system.stats.api.gamemode.GameMode;
import eu.darkcube.system.stats.api.user.User;

public abstract class Stats {

    private final Duration duration;

    private final GameMode gamemode;

    private final User owner;

    public Stats(User owner, Duration duration, GameMode gamemode) {
        this.gamemode = gamemode;
        this.duration = duration;
        this.owner = owner;
    }

    public static Component format(String key, String user, long placement) {
        return LegacyComponentSerializer.legacySection().deserialize("§7" + key + "§7: §6" + user + " §f» §7(Platz " + placement + ")");
    }

    public static Component formatToplist(String user, long placement, String value) {
        return LegacyComponentSerializer.legacySection().deserialize("§a" + placement + "§7: §5" + user + " §7(§6" + value + "§7)");
    }

    public GameMode getGamemode() {
        return this.gamemode;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public User getOwner() {
        return this.owner;
    }

    public final Component format() {
        Component component = Component
                .text("»")
                .color(NamedTextColor.GRAY)
                .appendSpace()
                .append(Component
                        .text("Wool")
                        .color(NamedTextColor.DARK_PURPLE)
                        .append(Component.text("Battle").color(NamedTextColor.LIGHT_PURPLE)))
                .appendNewline()
                .appendSpace()
                .append(Component.text("Statistiken von ").color(NamedTextColor.GRAY))
                .append(Component.text(owner.getName()).color(NamedTextColor.GREEN))
                .appendNewline();
        component = component.append(breakLine()).append(create()).append(breakLine());
        return component;
    }

    protected abstract Component create();

    protected final Component breakLine() {
        return Component.text("------------------------").color(NamedTextColor.DARK_GRAY).appendNewline();
        //		list.addAll(Arrays.asList(
        //				new ChatEntry.Builder().text("§8------------------------\n").build()));
    }

    public abstract Document serializeData();

    public abstract void loadData(Document document);

}
