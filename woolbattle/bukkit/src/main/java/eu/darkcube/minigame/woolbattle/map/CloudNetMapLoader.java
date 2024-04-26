/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.map;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.service.ServiceTemplate;
import eu.cloudnetservice.driver.template.TemplateStorage;
import eu.cloudnetservice.driver.template.TemplateStorageProvider;
import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.util.GsonSerializer;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;

public class CloudNetMapLoader implements MapLoader {
    private final TemplateStorage woolbattleStorage = InjectionLayer.boot().instance(TemplateStorageProvider.class).templateStorage("woolbattle");
    private final WoolBattleBukkit woolbattle;

    public CloudNetMapLoader(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
    }

    private static ServiceTemplate template(Map map) {
        return ServiceTemplate.builder().prefix(map.size().toString()).name(map.getName()).storage("woolbattle").build();
    }

    private static void deleteDirectory(Path path) throws IOException {
        try (var walk = Files.walk(path)) {
            // noinspection ResultOfMethodCallIgnored
            walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
    }

    @Override
    public CompletableFuture<Void> loadMap(@NotNull Map map) {
        if (map.ingameData() != null) throw new IllegalStateException("Map already loaded");
        var dmap = (DefaultMap) map;
        var rootFolder = Paths.get("anything").toAbsolutePath().normalize().getParent();
        return woolbattleStorage.openZipInputStreamAsync(template(map)).thenCompose(zipIn -> {
            var future = new CompletableFuture<Void>();
            if (zipIn == null) {
                var ex = new FileNotFoundException("Zip was null: " + template(map));
                new Scheduler(woolbattle, () -> future.completeExceptionally(ex)).runTask();
                return future;
            }
            try {
                var worldFolder = rootFolder.resolve(map.getName() + "-" + map.size());
                CloudNetMapIngameData ingameData = null;
                ZipEntry entry;
                while ((entry = zipIn.getNextEntry()) != null) {
                    try {
                        var name = entry.getName();
                        if (name.equals("data.json")) {
                            var bytes = zipIn.readAllBytes();
                            var sdata = new String(bytes, StandardCharsets.UTF_8);
                            ingameData = GsonSerializer.gson.fromJson(sdata, CloudNetMapIngameData.class);
                            ingameData.worldName(map.getName() + "-" + map.size());
                            continue;
                        }
                        if (entry.isDirectory()) continue;
                        var path = worldFolder.resolve(entry.getName());
                        Files.createDirectories(path.getParent());
                        Files.copy(zipIn, path);
                    } finally {
                        zipIn.closeEntry();
                    }
                }
                zipIn.close();
                if (ingameData == null) throw new InternalError("IngameData could not be loaded! Most likely no data.json found");
                var finalIngameData = ingameData;
                new Scheduler(woolbattle, () -> {
                    var wc = new WorldCreator(finalIngameData.worldName());
                    var world = wc.createWorld();
                    finalIngameData.world(world);
                    dmap.ingameData(finalIngameData);
                    future.complete(null);
                }).runTask();
            } catch (Throwable throwable) {
                new Scheduler(woolbattle, () -> future.completeExceptionally(throwable)).runTask();
            }
            return future;
        });
    }

    @Override
    public CompletableFuture<Void> save(@NotNull Map map) {
        var ingameData = map.ingameData();
        if (ingameData == null) throw new IllegalArgumentException("Map not loaded");
        var out = new ByteArrayOutputStream();
        var zip = new ZipOutputStream(out, StandardCharsets.UTF_8);
        try {
            zip.putNextEntry(new ZipEntry("data.json"));
            zip.write(GsonSerializer.gson.toJson(ingameData).getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
            var worldFolder = ingameData.world().getWorldFolder().toPath();
            Files.walkFileTree(worldFolder, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (dir.equals(worldFolder)) return FileVisitResult.CONTINUE;
                    zip.putNextEntry(new ZipEntry(worldFolder.relativize(dir) + "/"));
                    zip.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    zip.putNextEntry(new ZipEntry(worldFolder.relativize(file).toString()));
                    zip.write(Files.readAllBytes(file));
                    zip.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
            zip.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var template = template(map);
        return woolbattleStorage.deployAsync(template, new ByteArrayInputStream(out.toByteArray())).thenCompose(v -> {
            var fut = new CompletableFuture<Void>();
            new Scheduler(woolbattle, () -> fut.complete(null)).runTask();
            return fut;
        }).exceptionallyCompose(t -> {
            var fut = new CompletableFuture<Void>();
            new Scheduler(woolbattle, () -> fut.completeExceptionally(t)).runTask();
            return fut;
        });
    }

    @Override
    public void unloadMap(@NotNull Map map) {
        var dmap = (DefaultMap) map;
        var world = dmap.ingameData().world();
        dmap.ingameData(null);

        var folder = world.getWorldFolder().toPath();
        new Scheduler(woolbattle, () -> {
            Bukkit.unloadWorld(world, false);
            try {
                deleteDirectory(folder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).runTask();
    }
}
