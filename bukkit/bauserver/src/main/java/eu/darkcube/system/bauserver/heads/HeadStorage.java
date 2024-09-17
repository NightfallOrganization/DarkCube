/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver.heads;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import eu.darkcube.system.bauserver.Main;
import eu.darkcube.system.bauserver.heads.inventory.HeadInventories;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.GsonBuilder;
import eu.darkcube.system.libs.com.google.gson.reflect.TypeToken;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class HeadStorage {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = Main.getInstance().getDataPath().resolve("head_storage.json");
    private static final Type STORED_HEADS_TYPE = TypeToken.getParameterized(List.class, Head.class).getType();
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
        save();
        return index;
    }

    public void removeHead(int index) {
        storedHeads.remove(index);
        HeadInventories.STORED_LIST.pagination().content().publishUpdateAll();
        save();
    }

    public void removeHead(Head head) {
        storedHeads.remove(head);
        HeadInventories.STORED_LIST.pagination().content().publishUpdateAll();
        save();
    }

    public void load() {
        if (!Files.exists(FILE)) return;
        if (!Files.isRegularFile(FILE)) return;
        try (var reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
            this.storedHeads.addAll(GSON.fromJson(reader, STORED_HEADS_TYPE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (var writer = Files.newBufferedWriter(FILE)) {
                GSON.toJson(this.storedHeads, STORED_HEADS_TYPE, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
