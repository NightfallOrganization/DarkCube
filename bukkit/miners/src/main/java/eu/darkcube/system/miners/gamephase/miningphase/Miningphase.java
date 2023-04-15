/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.gamephase.miningphase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.items.Item;
import eu.darkcube.system.miners.player.Message;
import eu.darkcube.system.miners.util.Timer;

public class Miningphase {

    private Timer miningTimer;

    private static final Random RANDOM = new Random();

    public static final int CUBE_DISTANCE = 100;
    public static final Vector SPAWN_OFFSET = new Vector(33.5, 32, 33.5);

    public static World MINING_WORLD;

    public Miningphase() {
        MINING_WORLD = Bukkit.getWorld(Miners.MINING_WORLD_NAME);

        for (Map<String, String> map : readVeinConfig()) {
            if (map.containsKey("type") && map.containsKey("count")) {
                generateVeinsFromMap(map);
            }
        }

        miningTimer = new Timer() {
            private final int[] notifications = {
                    240,
                    180,
                    120,
                    60,
                    30,
                    10,
                    5,
                    4,
                    3,
                    2,
                    1
            };
            private int nextNotification = 0;

            @Override
            public void onIncrement() {
                int remainingSeconds = (int) Math.ceil(getTimeRemainingMillis() / 1000);
                if (nextNotification < notifications.length && remainingSeconds == notifications[nextNotification]) {
                    sendTimeRemaining(notifications[nextNotification]);
                    nextNotification++;
                }
            }

            @Override
            public void onEnd() {
                Miners.nextGamephase();
            }

            private void sendTimeRemaining(int secs) {
                if (secs > 60) {
                    int mins = secs / 60;
                    Bukkit.getOnlinePlayers().forEach(p -> Miners.sendTranslatedMessage(p, Message.TIME_REMAINING, mins, Message.TIME_MINUTES));
                } else {
                    Bukkit.getOnlinePlayers().forEach(p -> Miners.sendTranslatedMessage(p, Message.TIME_REMAINING, secs, Message.TIME_SECONDS));
                }
            }
        };
    }

    public void enable() {
        miningTimer.start(Miners.getMinersConfig().MINING_PHASE_DURATION * 1000);

        Bukkit.getOnlinePlayers().forEach(p -> {
            p.getInventory().addItem(Item.PICKAXE_STONE.getItem(p));
            p.getInventory().addItem(Item.COBBLESTONE.getItem(p));
            p.getInventory().addItem(Item.CRAFTING_TABLE.getItem(p));
            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 0, false, false));
            p.teleport(Miningphase.getSpawn(Miners.getTeamManager().getPlayerTeam(p)));
        });

        Miners.getTeamManager().getPlayersWithoutTeam().forEach(p -> p.setAllowFlight(true));
    }

    public void disable() {
        miningTimer.cancel(false);
    }

    public Timer getTimer() {
        return miningTimer;
    }

    /**
     * returns the location of the north-western bottom corner of a given team's
     * cube
     */
    public static Location getCubeBase(int team) {
        return new Location(MINING_WORLD, ((team - 1) * CUBE_DISTANCE), 100, 0);
    }

    /**
     * returns the spawnpoint of a given team in the mining world
     */
    public static Location getSpawn(int team) {
        return getCubeBase(team).clone().add(SPAWN_OFFSET);
    }

    /**
     * read json with ore vein generation from the config
     */
    private static List<Map<String, String>> readVeinConfig() {
        Gson gson = new Gson();
        String json = Miners.getMinersConfig().CONFIG.getString("generation.veins");
        List<String> list1 = gson.fromJson(json, new TypeToken<List<String>>() {
        }.getType());
        List<Map<String, String>> list2 = new ArrayList<>();
        for (String s : list1)
            list2.add(gson.fromJson(s, new TypeToken<Map<String, String>>() {
            }.getType()));
        return list2;
    }

    private static void generateVeinsFromMap(Map<String, String> map) {
        try {
            Material material = Material.valueOf(map.get("type"));
            int count = Integer.parseInt(map.get("count"));
            int min = 1, max = 1;
            if (map.containsKey("minSize") && map.containsKey("maxSize")) {
                min = Integer.parseInt(map.get("minSize"));
                max = Integer.parseInt(map.get("maxSize"));
            }
            Miners.log("Generating " + count + " veins of type \"" + material + "\" with a size between " + min + " and " + max);
            for (int i = 0; i <= count; i++)
                MiningGenerator.generateOreVein(MiningGenerator.pickRandomBlock(), material, randomBetween(min, max));

        } catch (Exception e) {
            Miners.log("Invalid ore vein generation config!");
            if (e instanceof NumberFormatException)
                Miners.log("Invalid number!");
            else
                Miners.log("Invalid block material!");
        }
    }

    private static int randomBetween(int min, int max) {
        if (min >= max)
            return min;
        int i = RANDOM.nextInt(max - min);
        return i + min;
    }

}
