package eu.darkcube.minigame.woolbattle.common.world;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipInputStream;

import eu.darkcube.minigame.woolbattle.api.map.Map;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface WorldDataProvider {
    @NotNull CompletableFuture<@NotNull ZipInputStream> loadLobbyZip();

    @NotNull CompletableFuture<@NotNull ZipInputStream> loadMap(@NotNull Map map);

    @NotNull CompletableFuture<Void> saveMap(@NotNull Map map, @NotNull InputStream inputStream);
}
