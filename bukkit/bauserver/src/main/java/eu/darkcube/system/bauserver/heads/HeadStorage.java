/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver.heads;

import java.util.ArrayList;
import java.util.List;

import eu.darkcube.system.bauserver.heads.inventory.HeadInventories;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class HeadStorage {
    private final List<Head> storedHeads = new ArrayList<>();

    public @NotNull Head head(int index) {
        return storedHeads.get(index);
    }

    public int size() {
        return storedHeads.size();
    }

    public int addHead(Head head) {
        int index = storedHeads.size();
        storedHeads.add(head);
        HeadInventories.STORED_LIST.pagination().content().publishUpdateAll();
        return index;
    }

    public void removeHead(int index) {
        storedHeads.remove(index);
        HeadInventories.STORED_LIST.pagination().content().publishUpdateAll();
    }

    public void removeHead(Head head) {
        storedHeads.remove(head);
        HeadInventories.STORED_LIST.pagination().content().publishUpdateAll();
    }

    public void load() {
    }

    public void save() {
    }
}
