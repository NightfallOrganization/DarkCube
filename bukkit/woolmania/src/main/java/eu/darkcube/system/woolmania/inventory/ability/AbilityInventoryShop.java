/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.inventory.ability;

import static eu.darkcube.system.woolmania.enums.InventoryItems.INVENTORY_ABILITY_SHOP;
import static eu.darkcube.system.woolmania.enums.Names.ASTAROTH;
import static eu.darkcube.system.woolmania.enums.Sounds.BUY;
import static eu.darkcube.system.woolmania.enums.Sounds.NO;
import static eu.darkcube.system.woolmania.util.message.Message.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
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
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.Abilitys;
import eu.darkcube.system.woolmania.enums.InventoryItems;
import eu.darkcube.system.woolmania.util.WoolManiaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AbilityInventoryShop implements TemplateInventoryListener {

    private static final String MASK = """
            .........
            .........
            .#######.
            .#######.
            .........
            """;
    private static final Key ITEM_REFERENCES_KEY = Key.key(WoolMania.getInstance(), "shop_ability_item_references");
    private static final DataKey<Integer> KEY_POSITION = DataKey.of(Key.key(WoolMania.getInstance(), "shop_ability_position"), PersistentDataTypes.INTEGER);
    private final InventoryTemplate inventoryTemplate;

    public void openInventory(User user) {
        inventoryTemplate.open(user);
    }

    public void openInventory(Player player) {
        inventoryTemplate.open(player);
    }

    public AbilityInventoryShop() {
        inventoryTemplate = Inventory.createChestTemplate(Key.key(WoolMania.getInstance(), "ability_shop"), 45);
        inventoryTemplate.title(ASTAROTH.getName());
        inventoryTemplate.animation().calculateManifold(4, 1);
        inventoryTemplate.setItems(0, DarkCubeItemTemplates.Gray.TEMPLATE_5);
        DarkCubeInventoryTemplates.Paged.configure5x9(inventoryTemplate, INVENTORY_ABILITY_SHOP);
        PagedTemplateSettings pagination = inventoryTemplate.pagination();
        pagination.pageSlots(InventoryMask.slots(MASK, '#'));

        inventoryTemplate.addListener(this);
    }

    private Function<User, ItemBuilder> getDisplayItem(InventoryItems item, int amount, int position) {
        return user -> {
            ItemBuilder itemBuilder = item.getItem(user, ITEM_BUY_COST.getMessage(user, amount));
            itemBuilder.persistentDataStorage().set(KEY_POSITION, position);
            return itemBuilder;
        };
    }

    @Override
    public void onPreOpen(@NotNull TemplateInventory inventory, @NotNull User user) {
        var bukkitPlayer = Bukkit.getPlayer(user.uniqueId());
        if (bukkitPlayer == null) return;
        WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(bukkitPlayer);

        var content = inventory.pagedController().staticContent();
        var list = new ArrayList<Map.Entry<Abilitys, ItemReference>>();

        int position = 0;
        for (Abilitys ability : Abilitys.values()) {
            if (woolManiaPlayer.isBoughtAbility(ability)) continue;

            var reference = content.addItem(getDisplayItem(ability.getInventoryItems(), ability.getCost(), position));
            list.add(Map.entry(ability, reference));

            position++;
        }

        user.metadata().set(ITEM_REFERENCES_KEY, list);
    }

    @Override
    public void onClose(@NotNull TemplateInventory inventory, @NotNull User user) {
        user.metadata().remove(ITEM_REFERENCES_KEY);
    }

    @Override
    public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
        var content = inventory.pagedController().staticContent();
        List<Map.Entry<Abilitys, ItemReference>> references = user.metadata().get(ITEM_REFERENCES_KEY);
        Player player = Bukkit.getPlayer(user.uniqueId());
        WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);
        if (references == null) return;
        String clickedInventoryItem = InventoryItems.getItemID(item);
        if (clickedInventoryItem == null) return;
        Integer position = item.persistentDataStorage().get(KEY_POSITION);
        if (position == null) return;
        var entry = references.get(position);
        var reference = entry.getValue();
        var ability = entry.getKey();
        content.removeItem(reference);
        int abilityCost = ability.getCost();
        int playerMoney = WoolMania.getStaticPlayer(player).getMoney();

        if (abilityCost > playerMoney) {
            user.sendMessage(NO_MONEY);
            NO.playSound(player);
            return;
        }

        WoolMania.getStaticPlayer(player).removeMoney(abilityCost, player);
        BUY.playSound(player);
        woolManiaPlayer.buyAbility(ability);
        user.sendMessage(ABILITY_BOUGHT, ability.getInventoryItems().getItem(user, "").displayname(), abilityCost);
        inventory.pagedController().publishUpdatePage();
    }

}
