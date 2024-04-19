package eu.darkcube.minigame.woolbattle.api.event.world.block;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.world.Block;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.material.Material;

public class PlaceBlockEvent extends BlockEvent.User.Cancellable {
    private final @NotNull Material material;

    public PlaceBlockEvent(@NotNull WBUser user, @NotNull Block block, @NotNull Material material) {
        super(user, block);
        this.material = material;
    }

    public @NotNull Material material() {
        return material;
    }

    @ApiStatus.Experimental
    public @NotNull Material placeMaterial() {
        return material;
    }
}