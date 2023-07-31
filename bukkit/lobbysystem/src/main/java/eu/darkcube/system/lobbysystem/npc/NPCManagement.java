/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.npc;

import com.github.juliarn.npclib.api.Npc;
import com.github.juliarn.npclib.api.NpcActionController;
import com.github.juliarn.npclib.api.Platform;
import com.github.juliarn.npclib.api.event.AttackNpcEvent;
import com.github.juliarn.npclib.api.event.HideNpcEvent;
import com.github.juliarn.npclib.api.event.InteractNpcEvent;
import com.github.juliarn.npclib.api.event.ShowNpcEvent;
import com.github.juliarn.npclib.api.flag.NpcFlag;
import com.github.juliarn.npclib.api.profile.Profile;
import com.github.juliarn.npclib.api.profile.ProfileProperty;
import com.github.juliarn.npclib.api.protocol.OutboundPacket;
import com.github.juliarn.npclib.api.protocol.enums.EntityAnimation;
import com.github.juliarn.npclib.api.protocol.meta.EntityMetadataFactory;
import com.github.juliarn.npclib.api.settings.NpcTrackingRule;
import com.github.juliarn.npclib.bukkit.BukkitPlatform;
import com.github.juliarn.npclib.bukkit.BukkitWorldAccessor;
import com.github.juliarn.npclib.bukkit.protocol.BukkitProtocolAdapter;
import com.github.juliarn.npclib.bukkit.util.BukkitPlatformUtil;
import com.github.juliarn.npclib.common.flag.CommonNpcFlaggedObject;
import com.github.juliarn.npclib.ext.labymod.LabyModExtension;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.event.NPCShowEvent;
import eu.darkcube.system.lobbysystem.event.PlayerNPCInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class NPCManagement {

    private static final NpcFlag<NPC> flagNPC = NpcFlag.flag("npc", null);
    private final @NotNull Platform<World, Player, ItemStack, Plugin> platform;
    private final int spawnDistance = 50;
    private final int imitateDistance = 45;
    private final int tabRemovalTicks = 40;

    public NPCManagement(Lobby lobby) {
        platform = BukkitPlatform.bukkitNpcPlatformBuilder().extension(lobby).actionController(builder -> {
            builder.flag(NpcActionController.SPAWN_DISTANCE, spawnDistance);
            builder.flag(NpcActionController.IMITATE_DISTANCE, imitateDistance);
            builder.flag(NpcActionController.TAB_REMOVAL_TICKS, tabRemovalTicks);
        }).worldAccessor(BukkitWorldAccessor.worldAccessor()).packetFactory(BukkitProtocolAdapter.protocolLib()).build();

        platform.eventBus().subscribe(ShowNpcEvent.Pre.class, event -> {
        });
        platform.eventBus().subscribe(AttackNpcEvent.class, event -> {
            PlayerNPCInteractEvent.Hand hand = PlayerNPCInteractEvent.Hand.MAIN_HAND;
            PlayerNPCInteractEvent.EntityUseAction useAction = PlayerNPCInteractEvent.EntityUseAction.ATTACK;
            Bukkit.getPluginManager().callEvent(new PlayerNPCInteractEvent(event.player(), event.npc().flagValueOrDefault(flagNPC), hand, useAction));
        });
        platform.eventBus().subscribe(InteractNpcEvent.class, event -> {
            PlayerNPCInteractEvent.Hand hand = event.hand() == InteractNpcEvent.Hand.MAIN_HAND ? PlayerNPCInteractEvent.Hand.MAIN_HAND : PlayerNPCInteractEvent.Hand.OFF_HAND;
            PlayerNPCInteractEvent.EntityUseAction useAction = PlayerNPCInteractEvent.EntityUseAction.INTERACT;
            Bukkit.getPluginManager().callEvent(new PlayerNPCInteractEvent(event.player(), event.npc().flagValueOrDefault(flagNPC), hand, useAction));
        });
        platform.eventBus().subscribe(HideNpcEvent.class, event -> Bukkit.getPluginManager().callEvent(new NPCShowEvent(event.npc().flagValueOrDefault(flagNPC), event.player())));
        platform.eventBus().subscribe(ShowNpcEvent.Post.class, event -> {
            Player player = event.player();
            event.npc().changeMetadata(EntityMetadataFactory.skinLayerMetaFactory(), true).schedule(player);
            Bukkit.getPluginManager().callEvent(new NPCShowEvent(event.npc().flagValueOrDefault(flagNPC), player));
        });
    }

    public Builder builder() {
        return new Builder();
    }

    public static class Skin {
        private final String value;
        private final String signature;

        public Skin(String value, String signature) {
            this.value = value;
            this.signature = signature;
        }

        public String value() {
            return value;
        }

        public String signature() {
            return signature;
        }
    }

    public class NPC extends CommonNpcFlaggedObject {
        private final Builder builder;
        private volatile Npc<World, Player, ItemStack, Plugin> npc = null;
        private volatile Location location;
        private volatile String name;
        private boolean destroyed = false;

        private NPC(Builder builder) {
            super(new HashMap<>());
            this.builder = builder;
        }

        public synchronized NPC location(final @Nullable Location location) {
            if (destroyed) throw new IllegalStateException("destroyed");
            this.location = location;
            if (npc != null) npc.unlink();
            if (location == null) npc = null;
            else {
                Npc.Builder<World, Player, ItemStack, Plugin> b = platform.newNpcBuilder();
                builder.apply(b);
                b.flag(flagNPC, this);
                b.position(BukkitPlatformUtil.positionFromBukkitLegacy(location));
                npc = b.build();
                name = npc.profile().name();
                platform.npcTracker().trackNpc(npc);
            }
            return this;
        }

        public synchronized void destroy() {
            destroyed = true;
            if (npc != null) {
                npc.unlink();
                npc = null;
            }
            name = null;
            location = null;
        }

        public String name() {
            return name;
        }

        public void animation(Player player, EntityAnimation animation) {
            sendPacket(player, platform.packetFactory().createAnimationPacket(animation));
        }

        public boolean shownFor(Player player) {
            Npc<World, Player, ItemStack, Plugin> npc = this.npc;
            if (npc == null) return false;
            return npc.tracksPlayer(player);
        }

        public void sendEmotes(int... emotes) {
            OutboundPacket<World, Player, ItemStack, Plugin> packet = LabyModExtension.createEmotePacket(platform.packetFactory(), emotes);
            sendPacket(packet);
        }

        public void sendEmotes(Player player, int... emotes) {
            OutboundPacket<World, Player, ItemStack, Plugin> packet = LabyModExtension.createEmotePacket(platform.packetFactory(), emotes);
            sendPacket(player, packet);
        }

        public void sendPacket(Player player, OutboundPacket<World, Player, ItemStack, Plugin> packet) {
            Npc<World, Player, ItemStack, Plugin> npc = this.npc;
            if (npc != null) packet.schedule(player, npc);
        }

        public void sendPacket(OutboundPacket<World, Player, ItemStack, Plugin> packet) {
            Npc<World, Player, ItemStack, Plugin> npc = this.npc;
            if (npc != null) packet.scheduleForTracked(npc);
        }

        public Collection<? extends Player> trackedPlayers() {
            Npc<World, Player, ItemStack, Plugin> npc = this.npc;
            return npc != null ? npc.trackedPlayers() : Collections.emptyList();
        }

        public Location location() {
            return location;
        }
    }

    public final class Builder {

        private final ProfileHelper profileHelper = new ProfileHelper();
        private boolean lookAtPlayer = true;
        private boolean hitWhenPlayerHits = false;
        private boolean sneakWhenPlayerSneaks = false;
        private Profile.Resolved profile = null;
        private NpcTrackingRule<Player> trackingRule = null;

        public NPC build() {
            return new NPC(this);
        }

        public boolean lookAtPlayer() {
            return lookAtPlayer;
        }

        public Builder lookAtPlayer(boolean lookAtPlayer) {
            this.lookAtPlayer = lookAtPlayer;
            return this;
        }

        public boolean hitWhenPlayerHits() {
            return hitWhenPlayerHits;
        }

        public Builder hitWhenPlayerHits(boolean hitWhenPlayerHits) {
            this.hitWhenPlayerHits = hitWhenPlayerHits;
            return this;
        }

        public ProfileHelper profileHelper() {
            return profileHelper;
        }

        public boolean sneakWhenPlayerSneaks() {
            return sneakWhenPlayerSneaks;
        }

        public Builder profile(Profile.Resolved profile) {
            this.profile = profile;
            return this;
        }

        public Profile.Resolved profile() {
            return profile;
        }

        public Builder sneakWhenPlayerSneaks(boolean sneakWhenPlayerSneaks) {
            this.sneakWhenPlayerSneaks = sneakWhenPlayerSneaks;
            return this;
        }

        public NpcTrackingRule<Player> trackingRule() {
            return trackingRule;
        }

        public Builder trackingRule(NpcTrackingRule<Player> trackingRule) {
            this.trackingRule = trackingRule;
            return this;
        }

        @Override
        public Builder clone() {
            return new Builder().lookAtPlayer(lookAtPlayer).hitWhenPlayerHits(hitWhenPlayerHits).sneakWhenPlayerSneaks(sneakWhenPlayerSneaks).trackingRule(trackingRule);
        }

        private void apply(@NotNull Npc.Builder<World, Player, ItemStack, Plugin> builder) {
            applyFlags(builder);
            applySettings(builder);
            applyProfile(builder);
        }

        private void applyProfile(@NotNull Npc.Builder<World, Player, ItemStack, Plugin> builder) {
            String name = null;
            UUID uuid = null;
            Set<ProfileProperty> profileProperties = new HashSet<>();
            if (profile != null) {
                profileProperties.addAll(profile.properties());
                name = profile.name();
                uuid = profile.uniqueId();
            }
            uuid = profileHelper.uuid == null ? uuid : profileHelper.uuid;
            if (uuid == null) uuid = UUID.randomUUID();
            name = profileHelper.name == null ? name : profileHelper.name;
            if (name == null) name = uuid.toString().replace("-", "").substring(0, 16);
            if (profileHelper.skin != null) profileProperties.add(ProfileProperty.property("textures", profileHelper.skin.value, profileHelper.skin.signature));

            Profile.Resolved profile = Profile.resolved(name, uuid, profileProperties);

            builder.profile(profile);
        }

        private void applySettings(@NotNull Npc.Builder<World, Player, ItemStack, Plugin> builder) {
            builder.npcSettings(b -> {
                if (trackingRule != null) b.trackingRule(trackingRule);
            });
        }

        private void applyFlags(@NotNull Npc.Builder<World, Player, ItemStack, Plugin> builder) {
            builder.flag(Npc.LOOK_AT_PLAYER, lookAtPlayer);
            builder.flag(Npc.HIT_WHEN_PLAYER_HITS, hitWhenPlayerHits);
            builder.flag(Npc.SNEAK_WHEN_PLAYER_SNEAKS, sneakWhenPlayerSneaks);
        }

        public class ProfileHelper {

            private String name = null;
            private UUID uuid = null;
            private Skin skin = null;

            public String name() {
                return name;
            }

            public ProfileHelper name(String name) {
                this.name = name;
                return this;
            }

            public UUID uuid() {
                return uuid;
            }

            public ProfileHelper uuid(UUID uuid) {
                this.uuid = uuid;
                return this;
            }

            public Skin skin() {
                return skin;
            }

            public ProfileHelper skin(Skin skin) {
                this.skin = skin;
                return this;
            }

            public Builder builder() {
                return Builder.this;
            }
        }
    }
}
