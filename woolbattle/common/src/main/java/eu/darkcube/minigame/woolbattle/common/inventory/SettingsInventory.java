package eu.darkcube.minigame.woolbattle.common.inventory;

import static eu.darkcube.system.libs.net.kyori.adventure.key.Key.key;

import java.util.List;

import eu.darkcube.minigame.woolbattle.api.event.user.UserWoolSubtractDirectionUpdateEvent;
import eu.darkcube.minigame.woolbattle.api.user.WoolSubtractDirection;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattle;
import eu.darkcube.minigame.woolbattle.common.util.item.ItemManager;
import eu.darkcube.minigame.woolbattle.common.util.item.Items;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.event.EventNode;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.data.PersistentDataTypes;

public class SettingsInventory {
    public static InventoryTemplate create(@NotNull CommonWoolBattle woolbattle) {
        var template = woolbattle.createDefaultTemplate("settings", Messages.SETTINGS_TITLE, Items.SETTINGS);
        template.setItem(10, 21, Items.SETTINGS_WOOL_DIRECTION);
        template.setItem(10, 23, Items.SETTINGS_HEIGHT_DISPLAY);
        template.addListener(new TemplateInventoryListener() {
            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
                var itemId = ItemManager.instance().getItemId(item);
                if (itemId == null) return;
                if (itemId.equals(Items.SETTINGS_WOOL_DIRECTION.itemId())) {
                    woolbattle.settingsWoolDirectionInventoryTemplate().open(user);
                } else if (itemId.equals(Items.SETTINGS_HEIGHT_DISPLAY.itemId())) {
                    woolbattle.settingsHeightDisplayInventoryTemplate().open(user);
                }
            }
        });
        return template;
    }

    public static InventoryTemplate createHeightDisplay(@NotNull CommonWoolBattle woolbattle) {
        var template = woolbattle.createDefaultTemplate("settings_height_display", Messages.HEIGHT_DISPLAY_SETTINGS_TITLE, Items.SETTINGS_HEIGHT_DISPLAY);
        template.setItem(10, 21, Items.SETTINGS_HEIGHT_DISPLAY_COLOR);
        template.setItem(10, 23, user -> {
            var wbUser = woolbattle.api().user(user);
            if (wbUser == null) return ItemBuilder.item();
            return wbUser.heightDisplay().enabled() ? Items.HEIGHT_DISPLAY_ON : Items.HEIGHT_DISPLAY_OFF;
        });
        template.addListener(new TemplateInventoryListener() {
            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
                var itemId = ItemManager.instance().getItemId(item);
                if (itemId == null) return;
                var wbUser = woolbattle.api().user(user);
                if (wbUser == null) return;
                if (itemId.equals(Items.SETTINGS_HEIGHT_DISPLAY_COLOR.itemId())) {
                    woolbattle.settingsHeightDisplayColorInventoryTemplate().open(user);
                } else if (itemId.equals(Items.HEIGHT_DISPLAY_ON.itemId())) {
                    var heightDisplay = wbUser.heightDisplay();
                    heightDisplay.enabled(false);
                    wbUser.heightDisplay(heightDisplay);
                    inventory.updateSlotsAtPriority(10, 23);
                } else if (itemId.equals(Items.HEIGHT_DISPLAY_OFF.itemId())) {
                    var heightDisplay = wbUser.heightDisplay();
                    heightDisplay.enabled(true);
                    wbUser.heightDisplay(heightDisplay);
                    inventory.updateSlotsAtPriority(10, 23);
                }
            }
        });
        return template;
    }

    public static InventoryTemplate createHeightDisplayColor(@NotNull CommonWoolBattle woolbattle) {
        var template = woolbattle.createDefaultTemplate("settings_height_display_color", Messages.HEIGHT_DISPLAY_COLOR_SETTINGS_TITLE, Items.SETTINGS_HEIGHT_DISPLAY_COLOR);
        var keyColor = key(woolbattle, "height_display_color");
        for (var color : NamedTextColor.NAMES.values()) {
            template.pagination().content().addStaticItem(Items.HEIGHT_DISPLAY_COLOR_ENTRY.defaultReplacements(Component.text(color.toString(), color)).modify((user, item) -> {
                item.persistentDataStorage().set(keyColor, PersistentDataTypes.INTEGER, color.value());
                var wbUser = woolbattle.api().user(user);
                if (wbUser == null) return;
                item.setLore(List.of());
                if (color.value() == wbUser.heightDisplay().color().value()) {
                    item.lore(Messages.SELECTED.getMessage(user));
                    item.glow(true);
                } else {
                    item.lore(Messages.CLICK_TO_SELECT.getMessage(user));
                }
            }));
        }
        template.addListener(new TemplateInventoryListener() {
            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
                var wbUser = woolbattle.api().user(user);
                if (wbUser == null) return;
                var colorValue = item.persistentDataStorage().get(keyColor, PersistentDataTypes.INTEGER);
                if (colorValue == null) return;
                var display = wbUser.heightDisplay();
                display.color(TextColor.color(colorValue));
                wbUser.heightDisplay(display);
                inventory.pagedController().publishUpdateAll();
            }
        });
        return template;
    }

    public static InventoryTemplate createWoolDirection(@NotNull CommonWoolBattle woolbattle) {
        var template = woolbattle.createDefaultTemplate("settings_wool_direction", Messages.WOOL_DIRECTION_SETTINGS_TITLE, Items.SETTINGS_WOOL_DIRECTION);
        template.pagination().content().addStaticItem(Items.SETTINGS_WOOL_DIRECTION_LEFT_TO_RIGHT.modify((user, item) -> {
            var wbUser = woolbattle.api().user(user);
            if (wbUser == null) return;
            if (wbUser.woolSubtractDirection() == WoolSubtractDirection.LEFT_TO_RIGHT) item.glow(true);
        }));
        template.pagination().content().addStaticItem(Items.SETTINGS_WOOL_DIRECTION_RIGHT_TO_LEFT.modify((user, item) -> {
            var wbUser = woolbattle.api().user(user);
            if (wbUser == null) return;
            if (wbUser.woolSubtractDirection() == WoolSubtractDirection.RIGHT_TO_LEFT) item.glow(true);
        }));
        template.pagination().pageSlots(new int[]{21, 23});
        template.addListener(new TemplateInventoryListener() {
            private EventNode<?> node;

            @Override
            public void onOpen(@NotNull TemplateInventory inventory, @NotNull User user) {
                node = woolbattle.api().eventManager().addListener(UserWoolSubtractDirectionUpdateEvent.class, event -> {
                    if (event.user().user() != user) return;
                    inventory.pagedController().publishUpdateAll();
                });
            }

            @Override
            public void onClose(@NotNull TemplateInventory inventory, @NotNull User user) {
                woolbattle.api().eventManager().removeChild(node);
                node = null;
            }

            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
                var wbUser = woolbattle.api().user(user);
                if (wbUser == null) return;
                var itemId = ItemManager.instance().getItemId(item);
                if (itemId == null) return;
                if (itemId.equals(Items.SETTINGS_WOOL_DIRECTION_LEFT_TO_RIGHT.itemId())) {
                    wbUser.woolSubtractDirection(WoolSubtractDirection.LEFT_TO_RIGHT);
                } else if (itemId.equals(Items.SETTINGS_WOOL_DIRECTION_RIGHT_TO_LEFT.itemId())) {
                    wbUser.woolSubtractDirection(WoolSubtractDirection.RIGHT_TO_LEFT);
                }
            }
        });
        return template;
    }
}
