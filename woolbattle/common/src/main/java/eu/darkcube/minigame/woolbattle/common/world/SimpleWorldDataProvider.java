package eu.darkcube.minigame.woolbattle.common.world;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipInputStream;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.service.ServiceTemplate;
import eu.cloudnetservice.driver.template.TemplateStorage;
import eu.cloudnetservice.driver.template.TemplateStorageProvider;
import eu.darkcube.minigame.woolbattle.api.map.Map;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class SimpleWorldDataProvider implements WorldDataProvider {
    private final @NotNull TemplateStorage woolbattleStorage = Objects.requireNonNull(InjectionLayer.boot().instance(TemplateStorageProvider.class).templateStorage("woolbattle"));

    @Override
    public @NotNull CompletableFuture<@NotNull ZipInputStream> loadLobbyZip() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                var path = Path.of("lobby.zip");
                var in = Files.newInputStream(path);
                return new ZipInputStream(in, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<@NotNull ZipInputStream> loadMap(@NotNull Map map) {
        return woolbattleStorage.openZipInputStreamAsync(template(map));
    }

    @Override
    public @NotNull CompletableFuture<Void> saveMap(@NotNull Map map, @NotNull InputStream inputStream) {
        return woolbattleStorage.deployAsync(template(map), inputStream).thenCompose(aBoolean -> {
            var future = new CompletableFuture<Void>();
            if (aBoolean) future.complete(null);
            else future.completeExceptionally(new FileNotFoundException());
            return future;
        });
    }

    private static ServiceTemplate template(Map map) {
        return ServiceTemplate.builder().prefix(map.size().toString()).name(map.name()).storage("woolbattle").build();
    }
}
