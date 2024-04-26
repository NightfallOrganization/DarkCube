/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.aetheria.listener;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Arrays;
import java.util.List;

public class NoMonsterSpawnListener implements Listener {
    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        // Liste der verbotenen Monster
        List<Class<? extends Entity>> forbiddenMobs = Arrays.asList(

                // Böse Kreaturen
                ZombieVillager.class, Phantom.class, Blaze.class, ElderGuardian.class, Endermite.class,
                Ghast.class, Guardian.class, Hoglin.class, MagmaCube.class, PolarBear.class, PiglinBrute.class,
                Piglin.class, Shulker.class, Silverfish.class, Slime.class, Stray.class, Zoglin.class, Warden.class,
                Witch.class, WitherSkeleton.class, PigZombie.class, Enderman.class,

                // Friedliche Kreaturen
                Chicken.class, Cow.class, Fox.class, Horse.class, Ocelot.class,
                Parrot.class, Pig.class, Rabbit.class, Sheep.class, Turtle.class, Villager.class,
                Bee.class, Cat.class, Donkey.class, Mule.class, Panda.class, PolarBear.class,
                Snowman.class, IronGolem.class, Llama.class, Bat.class, Dolphin.class, Squid.class,
                Wolf.class, MushroomCow.class
        );

        // Überprüfen Sie, ob das gespawnte Monster in der Liste der verbotenen Monster ist
        if (forbiddenMobs.contains(event.getEntity().getClass())) {
            // Überprüfen Sie, ob das Monster in der Welt "Beastrealm" gespawnt wird
            if (event.getLocation().getWorld().getName().equals("Beastrealm")) {
                // Verhindern Sie das Spawnen des Monsters
                event.setCancelled(true);
            }
        }
    }
}
