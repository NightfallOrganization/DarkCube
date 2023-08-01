/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.map;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceTemplate;
import de.dytanic.cloudnet.driver.template.TemplateStorage;
import eu.darkcube.minigame.woolbattle.util.GsonSerializer;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CloudNetMapLoader implements MapLoader {
    private final TemplateStorage woolbattleStorage = CloudNetDriver.getInstance().getTemplateStorage("woolbattle");

    @Override
    public CompletableFuture<Void> loadMap(Map map) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        if (map.ingameData() != null) throw new IllegalStateException("Map already loaded");
        DefaultMap dmap = (DefaultMap) map;
        Path rootFolder = Paths.get("anything").getParent();
        woolbattleStorage.copyAsync(new ServiceTemplate(map.size().toString(), map.getName(), "woolbattle"), rootFolder).onComplete(suc -> {
            new Scheduler(() -> {
                if (!suc) {
                    future.completeExceptionally(new InternalError());
                    return;
                }
                Path pdata = rootFolder.resolve("data.json");
                String sdata;
                try {
                    sdata = new String(Files.readAllBytes(pdata), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    future.completeExceptionally(e);
                    return;
                }
                CloudNetMapIngameData ingameData = GsonSerializer.gson.fromJson(sdata, CloudNetMapIngameData.class);
                WorldCreator wc = new WorldCreator(ingameData.worldName());
                World world = wc.createWorld();
                ingameData.world(world);
                dmap.ingameData(ingameData);
                future.complete(null);
            }).runTask();
        });

        return future;
    }

    @Override
    public void save(Map map) {
        MapIngameData ingameData = map.ingameData();
        if (ingameData == null) throw new IllegalArgumentException("Map not loaded");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(out, StandardCharsets.UTF_8);
        ZipEntry entry = new ZipEntry("data.json");
        try {
            zip.putNextEntry(entry);
            zip.write(GsonSerializer.gson.toJson(ingameData).getBytes(StandardCharsets.UTF_8));
            zip.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unloadMap(Map map) {
    }
}
