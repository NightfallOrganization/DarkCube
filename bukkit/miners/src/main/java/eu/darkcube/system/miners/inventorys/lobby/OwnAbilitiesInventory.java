/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.inventorys.lobby;

import static eu.darkcube.system.miners.enums.InventoryItems.HOTBAR_ITEM_ABILITIES;
import static eu.darkcube.system.miners.enums.Names.ABILITIES;
import static eu.darkcube.system.miners.enums.Sounds.SOUND_SET;
import static eu.darkcube.system.miners.utils.message.Message.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.enums.Abilities;
import eu.darkcube.system.miners.enums.InventoryItems;
import eu.darkcube.system.miners.utils.MinersPlayer;
import eu.darkcube.system.server.inventory.DarkCubeInventoryTemplates;
import eu.darkcube.system.server.inventory.DarkCubeItemTemplates;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryMask;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.item.ItemReference;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.inventory.paged.PagedTemplateSettings;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.data.DataKey;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class OwnAbilitiesInventory implements TemplateInventoryListener {

    private static final String MASK = """
            .........
            .........
            .#######.
            .#######.
            .........
            """;
    private static final Key ITEM_REFERENCES_KEY = Key.key(Miners.getInstance(), "own_ability_item_references");
    private static final DataKey<Integer> KEY_POSITION = DataKey.of(Key.key(Miners.getInstance(), "own_ability_position"), PersistentDataTypes.INTEGER);
    private final InventoryTemplate inventoryTemplate;

    public void openInventory(User user) {
        inventoryTemplate.open(user);
    }

    public void openInventory(Player player) {
        inventoryTemplate.open(player);
    }

    public OwnAbilitiesInventory() {
        inventoryTemplate = Inventory.createChestTemplate(Key.key(Miners.getInstance(), "ability_own"), 45);
        inventoryTemplate.title(ABILITIES.getName());
        inventoryTemplate.animation().calculateManifold(4, 1);
        inventoryTemplate.setItems(0, DarkCubeItemTemplates.Gray.TEMPLATE_5);
        DarkCubeInventoryTemplates.Paged.configure5x9(inventoryTemplate, HOTBAR_ITEM_ABILITIES);
        PagedTemplateSettings pagination = inventoryTemplate.pagination();
        pagination.pageSlots(InventoryMask.slots(MASK, '#'));

        inventoryTemplate.addListener(this);
    }

    @Override
    public void onPreOpen(@NotNull TemplateInventory inventory, @NotNull User user) {
        var bukkitPlayer = Bukkit.getPlayer(user.uniqueId());
        if (bukkitPlayer == null) return;
        MinersPlayer minersPlayer = Miners.getStaticPlayer(bukkitPlayer);

        var content = inventory.pagedController().staticContent();
        var list = new ArrayList<Map.Entry<Abilities, ItemReference>>();

        int position = 0;
        for (Abilities ability : Abilities.values()) {
            if (!minersPlayer.isBoughtAbility(ability)) continue;

            ItemReference reference = content.addItem(getDisplayItem(ability.getInventoryItems(), position, ability, minersPlayer));
            list.add(Map.entry(ability, reference));

            position++;
        }

        user.metadata().set(ITEM_REFERENCES_KEY, list);
    }

    private Function<User, ItemBuilder> getDisplayItem(InventoryItems item, int position, Abilities ability, MinersPlayer minersPlayer) {
        return user -> {
            if (minersPlayer.isActiveAbility(ability)) {
                ItemBuilder itemBuilder = item.getItem(user, ITEM_IS_ACTIVE.getMessage(user));
                itemBuilder.persistentDataStorage().set(KEY_POSITION, position);
                return itemBuilder;
            } else {
                ItemBuilder itemBuilder = item.getItem(user, ITEM_IS_INACTIVE.getMessage(user));
                itemBuilder.persistentDataStorage().set(KEY_POSITION, position);
                return itemBuilder;
            }
        };
    }

    @Override
    public void onClose(@NotNull TemplateInventory inventory, @NotNull User user) {
        user.metadata().remove(ITEM_REFERENCES_KEY);
    }

    @Override
    public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
        List<Map.Entry<Abilities, ItemReference>> references = user.metadata().get(ITEM_REFERENCES_KEY);
        Player player = Bukkit.getPlayer(user.uniqueId());
        MinersPlayer minersPlayer = Miners.getStaticPlayer(player);
        if (references == null) return;
        Integer position = item.persistentDataStorage().get(KEY_POSITION);
        if (position == null) return;
        var entry = references.get(position);
        var ability = entry.getKey();

        if (minersPlayer.isActiveAbility(ability)) {
            minersPlayer.deactivateAbility(ability);
            user.sendMessage(DEACTIVATE_ABILITY, ability.getInventoryItems().getItem(user, "").displayname());
        } else {
            minersPlayer.activateAbility(ability);
            user.sendMessage(ACTIVATE_ABILITY, ability.getInventoryItems().getItem(user, "").displayname());
        }

        SOUND_SET.playSound(player);
        inventory.pagedController().publishUpdatePage();
    }
}