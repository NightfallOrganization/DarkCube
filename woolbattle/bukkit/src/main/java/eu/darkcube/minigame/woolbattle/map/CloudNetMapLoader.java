/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.map;

import de.dytanic.cloudnet.common.io.FileUtils;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceTemplate;
import de.dytanic.cloudnet.driver.template.TemplateStorage;
import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.util.GsonSerializer;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CloudNetMapLoader implements MapLoader {
    private final TemplateStorage woolbattleStorage = CloudNetDriver.getInstance().getTemplateStorage("woolbattle");
    private final WoolBattleBukkit woolbattle;

    public CloudNetMapLoader(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
    }

    private static ServiceTemplate template(Map map) {
        return new ServiceTemplate(map.size().toString(), map.getName(), "woolbattle");
    }

    private static void deleteDirectory(Path path) throws IOException {
        try (Stream<Path> walk = Files.walk(path)) {
            //noinspection ResultOfMethodCallIgnored
            walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        }
    }

    @Override public CompletableFuture<Void> loadMap(Map map) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        if (map.ingameData() != null) throw new IllegalStateException("Map already loaded");
        DefaultMap dmap = (DefaultMap) map;
        Path rootFolder = Paths.get("anything").toAbsolutePath().normalize().getParent();
        woolbattleStorage
                .asZipInputStreamAsync(template(map))
                .onCancelled(suc -> future.cancel(true))
                .onFailure(throwable -> new Scheduler(woolbattle, () -> future.completeExceptionally(throwable)).runTask())
                .onComplete(zipIn -> {
                    try {
                        Path worldFolder = rootFolder.resolve(map.getName() + "-" + map.size());
                        CloudNetMapIngameData ingameData = null;
                        ZipEntry entry;
                        while ((entry = zipIn.getNextEntry()) != null) {
                            try {
                                String name = entry.getName();
                                if (name.equals("data.json")) {
                                    byte[] bytes = FileUtils.toByteArray(zipIn);
                                    String sdata = new String(bytes, StandardCharsets.UTF_8);
                                    ingameData = GsonSerializer.gson.fromJson(sdata, CloudNetMapIngameData.class);
                                    ingameData.worldName(map.getName() + "-" + map.size());
                                    continue;
                                }
                                if (entry.isDirectory()) continue;
                                Path path = worldFolder.resolve(entry.getName());
                                Files.createDirectories(path.getParent());
                                Files.copy(zipIn, path);
                            } finally {
                                zipIn.closeEntry();
                            }
                        }
                        zipIn.close();
                        if (ingameData == null) throw new InternalError("IngameData could not be loaded! Most likely no data.json found");
                        CloudNetMapIngameData finalIngameData = ingameData;
                        new Scheduler(woolbattle, () -> {
                            WorldCreator wc = new WorldCreator(finalIngameData.worldName());
                            World world = wc.createWorld();
                            finalIngameData.world(world);
                            dmap.ingameData(finalIngameData);
                            future.complete(null);
                        }).runTask();
                    } catch (Throwable throwable) {
                        new Scheduler(woolbattle, () -> future.completeExceptionally(throwable)).runTask();
                    }
                });
        return future;
    }

    @Override public CompletableFuture<Void> save(Map map) {
        MapIngameData ingameData = map.ingameData();
        if (ingameData == null) throw new IllegalArgumentException("Map not loaded");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(out, StandardCharsets.UTF_8);
        try {
            zip.putNextEntry(new ZipEntry("data.json"));
            zip.write(GsonSerializer.gson.toJson(ingameData).getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
            Path worldFolder = ingameData.world().getWorldFolder().toPath();
            Files.walkFileTree(worldFolder, new SimpleFileVisitor<Path>() {
                @Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (dir.equals(worldFolder)) return FileVisitResult.CONTINUE;
                    zip.putNextEntry(new ZipEntry(worldFolder.relativize(dir) + "/"));
                    zip.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
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
        ServiceTemplate template = template(map);
        CompletableFuture<Void> fut = new CompletableFuture<>();
        woolbattleStorage
                .deployAsync(new ByteArrayInputStream(out.toByteArray()), template)
                .onFailure(t -> new Scheduler(woolbattle, () -> fut.completeExceptionally(t)).runTask())
                .onCancelled(booleanITask -> fut.cancel(true))
                .onComplete(suc -> new Scheduler(woolbattle, () -> fut.complete(null)).runTask());
        return fut;
    }

    @Override public void unloadMap(Map map) {
        DefaultMap dmap = (DefaultMap) map;
        World world = dmap.ingameData().world();
        dmap.ingameData(null);

        Path folder = world.getWorldFolder().toPath();
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
