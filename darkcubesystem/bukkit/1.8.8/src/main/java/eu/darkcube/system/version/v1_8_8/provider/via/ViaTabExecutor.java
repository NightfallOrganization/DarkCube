/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.version.v1_8_8.provider.via;

import com.viaversion.viaversion.libs.fastutil.ints.IntArrayList;
import com.viaversion.viaversion.libs.fastutil.ints.IntList;
import eu.darkcube.system.libs.com.mojang.brigadier.context.StringRange;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestion;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import org.bukkit.entity.Player;

import java.util.List;

@ApiStatus.Internal
public class ViaTabExecutor {

    private static final Cache[] cached = Cache.create(16);
    private static int cid = 0;

    public ViaTabExecutor() {
    }

    public static int work(String commandLine, List<Suggestion> completions) {
        int id = cid;
        cid = cid + 1;
        cid = cid % 16;
        Component[] tooltips = new Component[completions.size()];
        String[] suggestions = new String[completions.size()];
        int commonStart = commandLine.length();
        for (Suggestion completion : completions) {
            StringRange range = completion.getRange();
            commonStart = Math.min(range.getStart(), commonStart);
        }

        for (int i = 0; i < tooltips.length; i++) {
            Suggestion completion = completions.get(i);

            suggestions[i] = completion.apply(commandLine).substring(commonStart);

            Component hover = Component.empty();
            if (completion.getTooltip() != null && completion.getTooltip().getString() != null)
                hover = hover.append(Component.text(completion.getTooltip().getString())).append(Component.newline());
            tooltips[i] = hover;
        }

        int length = commandLine.length() - commonStart;

        cached[id].data = new Data(commonStart, length, suggestions, tooltips);
        return id;
    }

    public static @Nullable Data take(int id) {
        Cache cache = cached[id];
        Data a = cache.data;
        if (a != null) cache.data = null;
        return a;
    }

    private static class Cache {
        private volatile Data data;

        public Cache() {
        }

        public static Cache[] create(int length) {
            Cache[] a = new Cache[length];
            for (int i = 0; i < a.length; i++) a[i] = new Cache();
            return a;
        }
    }

    public static class Data {
        private final int start;
        private final int length;
        private final String[] suggestions;
        private final Component[] tooltips;

        public Data(int start, int length, String[] suggestions, Component[] tooltips) {
            this.start = start;
            this.length = length;
            this.suggestions = suggestions;
            this.tooltips = tooltips;
        }

        public String[] suggestions() {
            return suggestions;
        }

        public int length() {
            return length;
        }

        public int start() {
            return start;
        }

        public Component[] tooltips() {
            return tooltips;
        }
    }
}
