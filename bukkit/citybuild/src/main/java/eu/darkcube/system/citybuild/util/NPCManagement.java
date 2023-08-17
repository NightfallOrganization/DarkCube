/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.util;

import com.github.juliarn.npclib.api.Npc;
import com.github.juliarn.npclib.api.NpcActionController;
import com.github.juliarn.npclib.api.Platform;
import com.github.juliarn.npclib.api.event.AttackNpcEvent;
import com.github.juliarn.npclib.api.event.ShowNpcEvent;
import com.github.juliarn.npclib.api.profile.Profile;
import com.github.juliarn.npclib.api.profile.ProfileProperty;
import com.github.juliarn.npclib.api.protocol.meta.EntityMetadataFactory;
import com.github.juliarn.npclib.bukkit.BukkitPlatform;
import com.github.juliarn.npclib.bukkit.BukkitWorldAccessor;
import com.github.juliarn.npclib.bukkit.protocol.BukkitProtocolAdapter;
import com.github.juliarn.npclib.bukkit.util.BukkitPlatformUtil;
import eu.darkcube.system.citybuild.Citybuild;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.GsonBuilder;
import eu.darkcube.system.libs.com.google.gson.JsonArray;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class NPCManagement {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Platform<World, Player, ItemStack, Plugin> platform;

    public NPCManagement(Citybuild citybuild) {
        platform = BukkitPlatform.bukkitNpcPlatformBuilder().extension(citybuild).actionController(builder -> {
            builder.flag(NpcActionController.SPAWN_DISTANCE, 50);
            builder.flag(NpcActionController.IMITATE_DISTANCE, 15);
            builder.flag(NpcActionController.TAB_REMOVAL_TICKS, 40);
        }).worldAccessor(BukkitWorldAccessor.worldAccessor()).packetFactory(BukkitProtocolAdapter.protocolLib()).build();
        platform.eventManager().registerEventHandler(AttackNpcEvent.class, event -> {
            Player player = event.player();
            Npc<World, Player, ItemStack, Plugin> npc = event.npc();
            player.sendMessage(npc.profile().name() + " geschlagen");
        });
        platform.eventManager().registerEventHandler(ShowNpcEvent.Post.class, event -> {
            Player player = event.player();
            event.npc().changeMetadata(EntityMetadataFactory.skinLayerMetaFactory(), true).schedule(player);
        });
//        try {
//            citybuild.getDataFolder().mkdirs();
//
//            Path npcsPath = citybuild.getDataFolder().toPath().resolve("npcs.json");
//
//            String string = Files.readString(npcsPath, StandardCharsets.UTF_8);
//            List<String> json = gson.fromJson(string, List.class);
//
//            string = gson.toJson(json);
//            Files.writeString(npcsPath, string);
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    public void unload() {
        for (Npc<World, Player, ItemStack, Plugin> npc : new ArrayList<>(platform.npcTracker().trackedNpcs())) {
            npc.unlink();
        }
    }

    public Npc<World, Player, ItemStack, Plugin> newNpc(Location location, Skin skin, String name, UUID uuid) {

        return newNpcInternal(location, skin, name, uuid);
    }

    private Npc<World, Player, ItemStack, Plugin> newNpcInternal(Location location, Skin skin, String name, UUID uuid) {
        Npc.Builder<World, Player, ItemStack, Plugin> builder = platform.newNpcBuilder();
        Set<ProfileProperty> properties = new HashSet<>();
        properties.add(ProfileProperty.property("textures", skin.value(), skin.signature()));
        builder.profile(Profile.resolved(name, uuid, properties));
        builder.flag(Npc.LOOK_AT_PLAYER, true);
        builder.flag(Npc.SNEAK_WHEN_PLAYER_SNEAKS, true);
        builder.position(BukkitPlatformUtil.positionFromBukkitModern(location));
        return builder.buildAndTrack();
    }

    public record Skin(String value, String signature) {
    }
}
