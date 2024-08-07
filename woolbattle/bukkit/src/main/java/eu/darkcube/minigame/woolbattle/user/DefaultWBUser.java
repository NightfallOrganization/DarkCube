/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.user;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.event.user.EventUserAddWool;
import eu.darkcube.minigame.woolbattle.event.user.EventUserRemoveWool;
import eu.darkcube.minigame.woolbattle.event.user.EventUserWoolCountUpdate;
import eu.darkcube.minigame.woolbattle.perk.Perk.ActivationType;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.perks.passive.ExtraWoolPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerks;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.WoolSubtractDirection;
import eu.darkcube.minigame.woolbattle.util.scoreboard.Scoreboard;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventory;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.Language;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

class DefaultWBUser implements WBUser {

    private static final Key KEY_WOOL_SUBTRACT_DIRECTION = Key.key(WoolBattleBukkit.instance(), "wool_subtract_direction");
    private static final PersistentDataType<WoolSubtractDirection> TYPE_WOOL_SUBTRACT_DIRECTION = PersistentDataTypes.enumType(WoolSubtractDirection.class);
    private static final Key KEY_HEIGHT_DISPLAY = Key.key(WoolBattleBukkit.instance(), "height_display");
    private static final PersistentDataType<HeightDisplay> TYPE_HEIGHT_DISPLAY = new PersistentDataType<HeightDisplay>() {
        @Override
        public HeightDisplay deserialize(JsonElement json) {
            var d = json.getAsJsonObject();
            var enabled = d.get("enabled").getAsBoolean();
            var maxDistance = d.get("maxDistance").getAsInt();
            var color = d.get("color").getAsString().charAt(0);
            return new HeightDisplay(enabled, maxDistance, color);
        }

        @Override
        public JsonElement serialize(HeightDisplay data) {
            var d = new JsonObject();
            d.addProperty("enabled", data.enabled);
            d.addProperty("maxDistance", data.maxDistance);
            d.addProperty("color", data.color);
            return d;
        }

        @Override
        public HeightDisplay clone(HeightDisplay object) {
            return object.clone();
        }
    };
    private static final Key KEY_PARTICLES = Key.key(WoolBattleBukkit.instance(), "particles");
    private static final PersistentDataType<Boolean> TYPE_PARTICLES = PersistentDataTypes.BOOLEAN;
    private static final Key KEY_PERKS = Key.key(WoolBattleBukkit.instance(), "perks");
    private static final PersistentDataType<PerkName> TYPE_PERK_NAME = PersistentDataTypes.map(PersistentDataTypes.STRING, PerkName::getName, PerkName::new, p -> p);
    private static final PersistentDataType<List<PerkName>> TYPE_LIST_PERK_NAME = PersistentDataTypes.list(TYPE_PERK_NAME);
    private static final PersistentDataType<PlayerPerks> TYPE_PERKS = new PersistentDataType<>() {
        @Override
        public PlayerPerks deserialize(JsonElement json) {
            var doc = json.getAsJsonObject();
            var docPerks = doc.getAsJsonObject("perks");
            var docPerkSlots = doc.getAsJsonObject("perkSlots");
            Map<ActivationType, PerkName[]> perks = new HashMap<>();
            Map<ActivationType, int[]> perkSlots = new HashMap<>();
            for (String documentKey : docPerks.keySet()) {
                ActivationType type = ActivationType.valueOf(documentKey);
                PerkName[] array = TYPE_LIST_PERK_NAME.deserialize(docPerks.get(documentKey)).toArray(new PerkName[0]);
                perks.put(type, array);
            }
            for (String documentKey : docPerkSlots.keySet()) {
                ActivationType type = ActivationType.valueOf(documentKey);
                int[] array = TYPE_INT_ARRAY.deserialize(docPerkSlots.get(documentKey));
                perkSlots.put(type, array);
            }
            return new DefaultPlayerPerks(perks, perkSlots);
        }

        @Override
        public JsonElement serialize(PlayerPerks data) {
            var d = new JsonObject();
            var docPerks = new JsonObject();
            var docPerkSlots = new JsonObject();
            for (ActivationType type : ActivationType.values()) {
                docPerks.add(type.name(), TYPE_LIST_PERK_NAME.serialize(Arrays.asList(data.perks(type))));
                docPerkSlots.add(type.name(), TYPE_INT_ARRAY.serialize(data.perkInvSlots(type)));
            }
            d.add("perks", docPerks);
            d.add("perkSlots", docPerkSlots);
            return d;
        }

        @Override
        public PlayerPerks clone(PlayerPerks object) {
            return object.clone();
        }
    };
    private static final PersistentDataType<int[]> TYPE_INT_ARRAY = new PersistentDataType<>() {
        @Override
        public int[] deserialize(JsonElement json) {
            byte[] bytes = PersistentDataTypes.BYTE_ARRAY.deserialize(json);
            IntBuffer buf = ByteBuffer.wrap(bytes).asIntBuffer();
            int len = buf.get();
            int[] ar = new int[len];
            for (int i = 0; buf.remaining() > 0; i++) {
                ar[i] = buf.get();
            }
            return ar;
        }

        @Override
        public JsonElement serialize(int[] data) {
            ByteBuffer buf = ByteBuffer.wrap(new byte[data.length * Integer.BYTES + Integer.BYTES]);
            IntBuffer ib = buf.asIntBuffer();
            ib.put(data.length);
            for (int i : data)
                ib.put(i);
            return PersistentDataTypes.BYTE_ARRAY.serialize(buf.array());
        }

        @Override
        public int[] clone(int[] object) {
            return object.clone();
        }
    };
    private final WoolBattleBukkit woolbattle;
    private final User user;
    private final UserPerks perks;
    private Team team;
    private boolean trollmode;
    private int spawnProtectionTicks;
    private IInventory openInventory;
    private int kills;
    private int deaths;
    private WBUser lastHit;
    private int ticksAfterLastHit;
    private int projectileImmunityTicks;
    private int woolCount;

    public DefaultWBUser(WoolBattleBukkit woolbattle, User user) {
        this.woolbattle = woolbattle;
        this.user = user;
        user.persistentData().setIfNotPresent(KEY_HEIGHT_DISPLAY, TYPE_HEIGHT_DISPLAY, HeightDisplay.getDefault());
        user.persistentData().setIfNotPresent(KEY_PARTICLES, TYPE_PARTICLES, true);
        user.persistentData().setIfNotPresent(KEY_PERKS, TYPE_PERKS, new DefaultPlayerPerks());
        user.persistentData().setIfNotPresent(KEY_WOOL_SUBTRACT_DIRECTION, TYPE_WOOL_SUBTRACT_DIRECTION, WoolSubtractDirection.getDefault());
        spawnProtectionTicks = 0;
        trollmode = false;
        kills = 0;
        deaths = 0;
        perks = new UserPerks(woolbattle, this);
    }

    @Override
    public User user() {
        return user;
    }

    @Override
    public UUID getUniqueId() {
        return user.uniqueId();
    }

    @Override
    public String getPlayerName() {
        return user.name();
    }

    @Override
    public Component getTeamPlayerName() {
        return Component.text(user.name()).style(getTeam().getPrefixStyle());
    }

    @Override
    public Language getLanguage() {
        return user.language();
    }

    @Override
    public void setLanguage(Language language) {
        user.language(language);
    }

    @Override
    public Team getTeam() {
        return team;
    }

    @Override
    public void setTeam(Team team) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            WBUser u = WBUser.getUser(p);
            Scoreboard s = new Scoreboard(u);
            if (this.team != null) s.getTeam(this.team.getType().getScoreboardTag()).removePlayer(getPlayerName());
            if (team != null) s.getTeam(team.getType().getScoreboardTag()).addPlayer(getPlayerName());
        }
        if (team != null) {
            if (!team.isSpectator()) {
                var be = getBukkitEntity();
                if (be != null) {
                    for (Player o : Bukkit.getOnlinePlayers()) {
                        o.showPlayer(be);
                    }
                }
            }
        }
        this.team = team;
        if (team != null) {
            if (woolbattle.ingame().enabled()) {
                woolbattle.ingame().playerUtil().setPlayerItems(this);
                woolbattle.ingame().checkGameEnd();
            }
        }
    }

    @Override
    public UserPerks perks() {
        return perks;
    }

    @Override
    public PlayerPerks perksStorage() {
        return user.persistentData().get(KEY_PERKS, TYPE_PERKS, DefaultPlayerPerks::new).clone();
    }

    @Override
    public void perksStorage(PlayerPerks perks) {
        user.persistentData().set(KEY_PERKS, TYPE_PERKS, perks.clone());
    }

    @Override
    public boolean particles() {
        return user.persistentData().get(KEY_PARTICLES, TYPE_PARTICLES, () -> true);
    }

    @Override
    public void particles(boolean particles) {
        user.persistentData().set(KEY_PARTICLES, TYPE_PARTICLES, particles);
    }

    @Override
    public HeightDisplay heightDisplay() {
        return user.persistentData().get(KEY_HEIGHT_DISPLAY, TYPE_HEIGHT_DISPLAY, HeightDisplay::getDefault).clone();
    }

    @Override
    public void heightDisplay(HeightDisplay heightDisplay) {
        user.persistentData().set(KEY_HEIGHT_DISPLAY, TYPE_HEIGHT_DISPLAY, heightDisplay.clone());
    }

    @Override
    public WoolSubtractDirection woolSubtractDirection() {
        return user.persistentData().get(KEY_WOOL_SUBTRACT_DIRECTION, TYPE_WOOL_SUBTRACT_DIRECTION, WoolSubtractDirection::getDefault);
    }

    @Override
    public void woolSubtractDirection(WoolSubtractDirection woolSubtractDirection) {
        user.persistentData().set(KEY_WOOL_SUBTRACT_DIRECTION, TYPE_WOOL_SUBTRACT_DIRECTION, woolSubtractDirection);
    }

    @Override
    public int woolCount() {
        return woolCount;
    }

    @Override
    public int addWool(int count) {
        return addWool(count, false);
    }

    @Override
    public int addWool(int count, boolean dropIfFull) {
        if (count == 0) return 0;
        int maxAdd = getMaxWoolSize() - woolCount;
        if (maxAdd < 0) {
            removeWool(-maxAdd);
        }
        EventUserAddWool event = new EventUserAddWool(this, count, dropIfFull);
        Bukkit.getPluginManager().callEvent(event);
        maxAdd = getMaxWoolSize() - woolCount; // we do this again in case some idiot tries to
        // change the woolcount in the EventHandler
        if (maxAdd < 0) {
            removeWool(-maxAdd);
            maxAdd = 0;
        }
        int addCount = Math.min(event.amount(), maxAdd);
        int dropCount = event.amount() - addCount;
        if (event.isCancelled()) return 0;
        int added = 0;
        ItemStack item = getSingleWoolItem();
        var bukkitEntity = getBukkitEntity();
        if (bukkitEntity != null) {
            Inventory inv = bukkitEntity.getInventory();
            while (addCount > 0) {
                ItemStack i = item.clone();
                i.setAmount(Math.min(64, addCount));
                addCount -= 64;
                added += i.getAmount();
                inv.addItem(i);
            }
        }
        woolCount += added;
        EventUserWoolCountUpdate eventUserWoolCountUpdate = new EventUserWoolCountUpdate(this, woolCount);
        Bukkit.getPluginManager().callEvent(eventUserWoolCountUpdate);
        if (bukkitEntity != null) {
            if (event.dropRemaining()) {
                while (dropCount > 0) {
                    ItemStack i = item.clone();
                    i.setAmount(Math.min(64, dropCount));
                    dropCount -= 64;
                    added += i.getAmount();
                    getBukkitEntity().getWorld().dropItemNaturally(getBukkitEntity().getLocation().add(0, 0.5, 0), i);
                }
            }
        }
        return added;
    }

    @Override
    public int removeWool(int count) {
        return removeWool(count, true);
    }

    @Override
    public int removeWool(int count, boolean updateInventory) {
        if (count == 0) return 0;
        EventUserRemoveWool event = new EventUserRemoveWool(this, count);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return 0;
        int removeCount = Math.min(woolCount, event.amount());
        woolCount -= removeCount;
        if (updateInventory) {
            ItemStack singleItem = getSingleWoolItem();
            var bukkitEntity = getBukkitEntity();
            if (bukkitEntity != null) ItemManager.removeItems(this, bukkitEntity.getInventory(), singleItem, removeCount);
        }
        EventUserWoolCountUpdate eventUserWoolCountUpdate = new EventUserWoolCountUpdate(this, woolCount);
        Bukkit.getPluginManager().callEvent(eventUserWoolCountUpdate);
        return removeCount;
    }

    @Override
    public int getMaxWoolSize() {
        return 64 * 3 * (1 + perkCount(ExtraWoolPerk.EXTRA_WOOL));
    }

    @Override
    public int getWoolBreakAmount() {
        return 2 * (1 + perkCount(ExtraWoolPerk.EXTRA_WOOL));
    }

    @Override
    public int getSpawnProtectionTicks() {
        return spawnProtectionTicks;
    }

    @Override
    public void setSpawnProtectionTicks(int ticks) {
        this.spawnProtectionTicks = ticks;
        getBukkitEntity().setExp((float) getSpawnProtectionTicks() / (float) woolbattle.ingame().spawnprotectionTicks());
    }

    @Override
    public boolean hasSpawnProtection() {
        return getSpawnProtectionTicks() > 0;
    }

    @Override
    public boolean isTrollMode() {
        return trollmode;
    }

    @Override
    public void setTrollMode(boolean trollmode) {
        this.trollmode = trollmode;
        woolbattle.sendMessage("§aTrollmode " + (trollmode ? "§aAn" : "§cAus"), getBukkitEntity());
    }

    @Override
    public CraftPlayer getBukkitEntity() {
        return (CraftPlayer) Bukkit.getPlayer(getUniqueId());
    }

    @Override
    public IInventory getOpenInventory() {
        return openInventory;
    }

    @Override
    public void setOpenInventory(IInventory id) {
        this.openInventory = id;
        if (this.openInventory != null) this.openInventory.open(this.getBukkitEntity());
    }

    @Override
    public ItemStack getSingleWoolItem() {
        return new ItemStack(Material.WOOL, 1, getTeam().getType().getWoolColor().getWoolData());
    }

    @Override
    public WBUser getLastHit() {
        return lastHit;
    }

    @Override
    public void setLastHit(WBUser user) {
        this.lastHit = user;
    }

    @Override
    public int getTicksAfterLastHit() {
        return ticksAfterLastHit;
    }

    @Override
    public void setTicksAfterLastHit(int ticks) {
        ticksAfterLastHit = ticks;
    }

    @Override
    public int projectileImmunityTicks() {
        return projectileImmunityTicks;
    }

    @Override
    public void projectileImmunityTicks(int projectileImmunityTicks) {
        this.projectileImmunityTicks = projectileImmunityTicks;
    }

    @Override
    public int getKills() {
        return kills;
    }

    @Override
    public void setKills(int kills) {
        this.kills = kills;
        if (getBukkitEntity() != null) {
            getBukkitEntity().setLevel(kills);
        }
    }

    @Override
    public int getDeaths() {
        return deaths;
    }

    @Override
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    @Override
    public double getKD() {
        return getDeaths() != 0 ? (double) getKills() / (double) getDeaths() : getKills();
    }

    private int perkCount(@SuppressWarnings("SameParameterValue") PerkName perk) {
        int c = 0;
        for (UserPerk p : perks.perks())
            if (p.perk().perkName().equals(perk)) c++;
        return c;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WBUser && getUniqueId().equals(((WBUser) obj).getUniqueId());
    }

    @Override
    public String toString() {
        return getPlayerName();
    }
}
