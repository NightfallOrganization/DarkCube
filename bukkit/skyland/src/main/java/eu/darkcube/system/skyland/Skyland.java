/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland;

import eu.darkcube.system.bukkit.DarkCubePlugin;
import eu.darkcube.system.bukkit.commandapi.CommandAPI;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.skyland.Listener.SkylandListener;
import eu.darkcube.system.skyland.inventoryUI.AllInventory;
import eu.darkcube.system.skyland.mobs.CustomMob;
import eu.darkcube.system.skyland.mobs.FollowingMob;
import eu.darkcube.system.skyland.worldGen.CustomChunkGenerator;
import eu.darkcube.system.skyland.worldGen.Structures.SkylandStructure;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Skyland extends DarkCubePlugin {

    private static Skyland instance;
    ArrayList<CustomMob> mobs = new ArrayList<>();
    List<SkylandStructure> structures = new ArrayList<>();

    CustomChunkGenerator customChunkGenerator;

    public Skyland() {
        super("skyland");
        instance = this;
    }

    public static Skyland getInstance() {
        return instance;
    }

    @Override public void onDisable() {

        for (SkylandStructure skylandStructure : structures) {
            saveStructure(skylandStructure);
        }

        CommandAPI.instance().unregisterByPrefix("skyland");
        SkylandGeneratorCommand.deleteCustomWorlds(this);

    }

    @Override public void onEnable() {

        File file = new File("skyland");
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File("skyland/structures");
        if (!file.exists()) {
            file.mkdirs();
        }

        Gson gson = new Gson();

        for (File f : file.listFiles()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(f);
                String content = Files.readString(f.toPath(), StandardCharsets.UTF_8);
                fileInputStream.close();
                structures.add(gson.fromJson(content, SkylandStructure.class));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }




/*		try {
			Language.GERMAN.registerLookup(getClassLoader(), "messages_de.properties",
					s -> Message.PREFIX + s);
			Language.ENGLISH.registerLookup(getClassLoader(), "messages_en.properties",
					s -> Message.PREFIX + s);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		*/

        SkylandListener damageListener = new SkylandListener(this);
        Bukkit.getPluginManager().registerEvents(damageListener, instance);
        Bukkit.getPluginManager().registerEvents(AllInventory.getInstance(), instance);

        instance.getCommand("gm").setExecutor(new GM());
        instance.getCommand("heal").setExecutor(new Heal());
        instance.getCommand("day").setExecutor(new Day());
        instance.getCommand("night").setExecutor(new Night());
        instance.getCommand("fly").setExecutor(new Fly());
        instance.getCommand("test").setExecutor(new Feed());
        instance.getCommand("max").setExecutor(new Max());
        instance.getCommand("god").setExecutor(new God());
        instance.getCommand("trash").setExecutor(new Trash());
        instance.getCommand("world").setExecutor(new WorldX());
        instance.getCommand("getitem").setExecutor(new GetItem());
        instance.getCommand("getgui").setExecutor(new GetGUI());

        CommandAPI.instance().register(new SkylandGeneratorCommand(this));

        TrainingStand trainingStand = new TrainingStand();
        instance.getCommand("spawntrainingstand").setExecutor(trainingStand);
        Bukkit.getPluginManager().registerEvents(trainingStand, this);

        new BukkitRunnable() {
            @Override public void run() {
                for (CustomMob cm : mobs) {
                    cm.aiTick();
                }
            }
        }.runTaskTimer(this, 20, 1);

        new BukkitRunnable() {
            @Override public void run() {
                for (CustomMob cm : mobs) {
                    cm.updateName();
                }
            }
        }.runTaskTimer(this, 20, 5);

    }

    @Override public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        customChunkGenerator = new CustomChunkGenerator();
        return customChunkGenerator;
    }

    public ArrayList<CustomMob> getMobs() {
        return mobs;
    }

    public CustomMob getCustomMob(Mob mob) {
        for (CustomMob customMob : mobs) {
            if (mob.equals(customMob.getMob())) {
                return customMob;
            }
        }

        if (mob.getPersistentDataContainer().has(CustomMob.getCustomMobTypeKey())) {
            int id = mob.getPersistentDataContainer().get(CustomMob.getCustomMobTypeKey(), PersistentDataType.INTEGER);
            if (id == 0) {
                return new FollowingMob(mob);
            }
        }

        return null;
    }

    public void removeCustomMob(Mob m) {
        for (CustomMob cm : mobs) {

            if (cm.getMob().equals(m)) {
                mobs.remove(cm);
                System.out.println("mob removed");
                return;
            }

        }

        System.out.println("no mob removed");
    }

    public CustomChunkGenerator getCustomChunkGenerator() {
        return customChunkGenerator;
    }

    public List<SkylandStructure> getStructures() {
        return structures;
    }

    public void saveStructure(SkylandStructure skylandStructure) {
        File file = new File("skyland/structures/" + skylandStructure.getNamespacedKey().getKey());
        Gson gson = new Gson();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(gson.toJson(skylandStructure));
            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SkylandStructure getStructure(NamespacedKey nsk) {
        for (SkylandStructure skylandStructure : structures) {
            if (skylandStructure.getNamespacedKey().equals(nsk)) {
                return skylandStructure;
            }
        }
        return null;
    }
}
