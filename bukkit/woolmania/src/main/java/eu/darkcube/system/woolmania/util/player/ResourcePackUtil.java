/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.util.player;

import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;

import eu.darkcube.system.woolmania.WoolMania;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ResourcePackUtil implements Listener {
    private static final String TEXTURE_PACK_URL = "https://aetheria.darkcube.eu/WoolMania.zip";
    private static final String EMPTY_TEXTURE_PACK_URL = "https://aetheria.darkcube.eu/blank.zip";
    private final NamespacedKey key = new NamespacedKey(WoolMania.getInstance(), "texturepack_off");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PersistentDataContainer data = event.getPlayer().getPersistentDataContainer();
        Player player = event.getPlayer();

        if (data.has(key, PersistentDataType.BYTE) && data.get(key, PersistentDataType.BYTE) == 1) {
            return;
        }

        loadTexturePack(player);

    }

    public static void loadEmptyTexturePack(Player player) {
        byte[] sha1 = null;

        try {
            InputStream in = new URL(EMPTY_TEXTURE_PACK_URL).openStream();
            sha1 = MessageDigest.getInstance("SHA-1").digest(in.readAllBytes());
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sha1 != null) {
            player.getPlayer().setResourcePack(EMPTY_TEXTURE_PACK_URL, sha1, Component.text("You have to enable TexturePacks!"), true);
        } else {
            System.out.println("Failed to calculate SHA-1 for the resource pack");
        }
    }

    public static void loadTexturePack(Player player) {
        byte[] sha1 = null;

        try {
            InputStream in = new URL(TEXTURE_PACK_URL).openStream();
            sha1 = MessageDigest.getInstance("SHA-1").digest(in.readAllBytes());
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sha1 != null) {
            player.getPlayer().setResourcePack(TEXTURE_PACK_URL, sha1, Component.text("You have to enable TexturePacks!"), true);

        } else {
            System.out.println("Failed to calculate SHA-1 for the resource pack");
        }
    }

}
