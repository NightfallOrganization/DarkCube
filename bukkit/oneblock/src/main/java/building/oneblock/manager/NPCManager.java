package building.oneblock.manager;

import building.oneblock.OneBlock;
import building.oneblock.npc.NPC;
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
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NPCManager {
    private final Platform<World, Player, ItemStack, Plugin> platform;

    public NPCManager(OneBlock oneBlock) {
        platform = BukkitPlatform.bukkitNpcPlatformBuilder().extension(oneBlock).actionController(builder -> {
            builder.flag(NpcActionController.SPAWN_DISTANCE, 50);
            builder.flag(NpcActionController.IMITATE_DISTANCE, 35);
            builder.flag(NpcActionController.TAB_REMOVAL_TICKS, 40);
        }).worldAccessor(BukkitWorldAccessor.worldAccessor()).packetFactory(BukkitProtocolAdapter.protocolLib()).build();


        platform.eventManager().registerEventHandler(AttackNpcEvent.class, event -> {
            Player player = event.player();
            Npc<World, Player, ItemStack, Plugin> npc = event.npc();

            if (player.hasPermission("oneblock.level.3")) {
                player.sendMessage("§7Mindestanforderung: Level §e3");
//                connectToServer(player, "Aetheria-1");
            } else {
                player.sendMessage("§7Mindestanforderung: Level §e3");
            }
        });

        platform.eventManager().registerEventHandler(ShowNpcEvent.Post.class, event -> {
            Player player = event.player();
            event.npc().changeMetadata(EntityMetadataFactory.skinLayerMetaFactory(), true).schedule(player);
        });

    }

    public NPC createNPC(Location location, Skin skin, String name, UUID uuid) {
        var handle = newNpcInternal(location, skin, name, uuid);
        return new NPC(this, handle);
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
