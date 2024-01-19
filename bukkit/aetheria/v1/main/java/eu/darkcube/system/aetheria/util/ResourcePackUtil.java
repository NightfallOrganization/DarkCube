/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.util;

import eu.darkcube.system.aetheria.Aetheria;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedInputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResourcePackUtil {

    public static final NamespacedKey KEY = new NamespacedKey(Aetheria.getInstance(), "resource_pack_enabled");
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Aetheria aetheria;
    private final Key hashKey;
    private final String url = "https://aetheria.darkcube.eu/";
    private final String urlBlank = "https://aetheria.darkcube.eu/blank.zip";
    private byte[] hash;

    public ResourcePackUtil(Aetheria aetheria) {
        this.aetheria = aetheria;
        this.hashKey = new Key(aetheria, "resource_pack_hash");
        this.hash = aetheria.persistentDataStorage().get(hashKey, PersistentDataTypes.BYTE_ARRAY);
    }

    public static byte[] decodeHexString(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException("Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    public static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if (digit == -1) {
            throw new IllegalArgumentException("Invalid Hexadecimal Character: " + hexChar);
        }
        return digit;
    }

    public CompletableFuture<Void> reloadHash() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        executor.submit(() -> {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-1");
                URL url = new URL(this.url);
                BufferedInputStream in = new BufferedInputStream(url.openStream());
                byte[] buf = new byte[8192];
                int read;
                while ((read = in.read(buf)) != -1) {
                    if (read > 0) {
                        digest.update(buf, 0, read);
                    }
                }
                byte[] res = digest.digest();
                new BukkitRunnable() {
                    @Override public void run() {
                        hash = res;
                        aetheria.persistentDataStorage().set(hashKey, PersistentDataTypes.BYTE_ARRAY, res);
                        resendResourcePacks();
                        resendResourcePacks();
                        future.complete(null);
                    }
                }.runTask(aetheria);
                in.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        });
        return future;
    }

    public void resendResourcePacks() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendResourcePack(player);
        }
    }

    public void sendResourcePack(Player player) {
        if (player.getPersistentDataContainer().getOrDefault(KEY, PersistentDataType.BOOLEAN, true)) {
            player.setResourcePack(url() + Base64
                    .getEncoder()
                    .encodeToString(hash()) + ".zip", hash(), Component.text("Bitte Lade das TexturePack herunter!", NamedTextColor.GRAY), true);
        }
    }

    public void sendResourcePackBlank(Player player) {
        if (player.getPersistentDataContainer().getOrDefault(KEY, PersistentDataType.BOOLEAN, true)) {
            player.setResourcePack(urlBlank(), hash(), Component.text("Bitte Lade das TexturePack herunter!", NamedTextColor.GRAY), true);
        }
    }

    public String url() {
        return url;
    }

    public String urlBlank() {
        return urlBlank;
    }

    public byte[] hash() {
        return hash;
    }
}
