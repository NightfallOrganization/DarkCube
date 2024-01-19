/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.skills;

import eu.darkcube.system.aetheria.Aetheria;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class SkillAttraction extends Skill {

    private static final long COOLDOWN_IN_SECONDS = 20; // Zum Beispiel 15 Sekunden
    private HashMap<Player, Long> cooldowns;// TODO use persistent data storage

    public SkillAttraction() {
        super("Attraction", true);
        this.cooldowns = new HashMap<>();
    }

    @Override public void activateSkill(Player player) {
        if (canUse(player)) {
            Vector direction = player.getLocation().getDirection().setY(0).normalize().multiply(5);
            Location targetLocation = player.getLocation().add(direction);
            Block targetBlock = targetLocation.getBlock();

            // Normales Gras und Tall_Gras ignorieren
            while (targetBlock.getType().isAir() || targetBlock.getType().name().equals("GRASS") || targetBlock
                    .getType()
                    .name()
                    .equals("TALL_GRASS")) {
                targetBlock = targetBlock.getRelative(0, -1, 0);
            }

            final Block finalTargetBlock = targetBlock; // Diese Variable ist effektiv final

            if (!targetBlock.getRelative(0, -1, 0).getType().isAir()) {
                player.sendMessage("§7Attraction Skill §aaktiviert§7!");

                new BukkitRunnable() {
                    int ticksRun = 0; // Die Anzahl der Ticks, die der Runnable bereits ausgeführt wurde

                    @Override public void run() {
                        for (Entity entity : player.getNearbyEntities(15, 15, 15)) {
                            if (!(entity instanceof Player)) {
                                Vector directionToTarget = finalTargetBlock
                                        .getLocation()
                                        .toVector()
                                        .subtract(entity.getLocation().toVector())
                                        .normalize()
                                        .multiply(0.5);
                                entity.setVelocity(directionToTarget);
                            }
                        }

                        ticksRun++;
                        if (ticksRun >= 20 * 5) {  // 5 Sekunden (20 Ticks = 1 Sekunde)
                            this.cancel();  // Stoppt den Runnable, nachdem er 5 Sekunden lang ausgeführt wurde
                        }

                        // Partikel-Effekt hinzufügen
                        double angle = 2 * Math.PI * ticksRun / 20.0; // Ein voller Kreis pro Sekunde

                        int numberOfRings = 4; // Anzahl der Ringe
                        double distanceBetweenRings = 2; // Entfernung zwischen den Ringen in Blöcken

                        for (int ring = 0; ring < numberOfRings; ring++) {
                            double maxRadius = 10 - distanceBetweenRings * ring; // Start-Radien für jeden Ring
                            double decreaseRate = 0.1;  // Wie schnell die Ringe sich zur Mitte hin bewegen
                            double radius = maxRadius - decreaseRate * ticksRun;

                            if (radius < 0) {
                                continue; // Wenn ein Ring das Zentrum erreicht, stoppen Sie die Erzeugung von Partikeln für diesen Ring
                            }

                            // RGB Werte basierend auf dem Radius
                            double colorFactor = radius / 10.0;  // Der Farbfaktor nimmt mit zunehmendem Radius zu
                            int baseColor = 200;  // Maximale Helligkeit am Rand
                            int minColor = 50;  // Minimale Helligkeit in der Mitte
                            int r = minColor + (int) ((baseColor - minColor) * colorFactor);
                            int g = 0;
                            int b = minColor + (int) ((baseColor - minColor) * colorFactor);

                            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(r, g, b), 1);

                            for (int i = 0; i < 40; i++) {  // Erzeugt Partikel pro Ring pro Tick
                                double offsetAngle = angle + Math.PI * 2 * i / 40.0;  // Verteilt die Partikel gleichmäßig in 360 Grad

                                double x = Math.cos(offsetAngle) * radius;
                                double z = Math.sin(offsetAngle) * radius;

                                player
                                        .getWorld()
                                        .spawnParticle(Particle.REDSTONE, finalTargetBlock
                                                .getLocation()
                                                .add(x, 1.5, z), 1, 0, 0, 0, 0, dustOptions);
                            }
                        }
                    }

                }.runTaskTimer(Aetheria.getInstance(), 0L, 1L);  // startet sofort und wiederholt sich jeden Tick

                cooldowns.put(player, System.currentTimeMillis());
            } else {
                player.sendMessage("§7Du kannst den Skill nur auf dem Boden aktivieren");
            }
        } else {
            long timeLeft = (cooldowns.get(player) + COOLDOWN_IN_SECONDS * 1000 - System.currentTimeMillis()) / 1000;
            player.sendMessage("§7Du musst noch §a" + timeLeft + " §7Sekunden warten, bevor du diesen Skill wieder verwenden kannst");
        }
    }

    private boolean canUse(Player player) {
        if (!cooldowns.containsKey(player)) {
            return true;
        }

        long timeSinceLastUse = System.currentTimeMillis() - cooldowns.get(player);
        return timeSinceLastUse >= COOLDOWN_IN_SECONDS * 1000;
    }
}
