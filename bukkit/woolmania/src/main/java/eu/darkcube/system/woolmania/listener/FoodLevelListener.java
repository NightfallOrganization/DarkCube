/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.listener;

import static eu.darkcube.system.woolmania.enums.Abilitys.AUTO_EAT;
import static eu.darkcube.system.woolmania.enums.Sounds.EAT;

import java.util.Objects;

import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.ItemComponent;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.items.CustomItem;
import eu.darkcube.system.woolmania.util.WoolManiaPlayer;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

public class FoodLevelListener implements Listener {
    private final NamespacedKey blockBreakSpeedKey = new NamespacedKey(WoolMania.getInstance(), "block_break_speed");

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        AttributeInstance attribute = player.getAttribute(Attribute.PLAYER_BLOCK_BREAK_SPEED);
        attribute.removeModifier(blockBreakSpeedKey);
        int level = event.getFoodLevel();

        if(event.getFoodLevel() != player.getFoodLevel()) {
            eatFromInventory(player, level, event);
        }

        double amount = switch(level) {
            case 0 -> -0.95;
            case 1, 2, 3, 4, 5, 6 -> -0.7;
            default -> -2;
        };

        if (amount != -2) {
            attribute.addModifier(new AttributeModifier(blockBreakSpeedKey, amount, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
        }
    }

    private void eatFromInventory(Player player, int level, FoodLevelChangeEvent event) {
        User user = UserAPI.instance().user(player.getUniqueId());
        WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);

        if (level < 8 && woolManiaPlayer.isActiveAbility(AUTO_EAT)) {
            boolean hasFood = false;

            for (ItemStack item : player.getInventory().getContents()) {

                if (item != null) {
                    ItemBuilder itemBuilder = ItemBuilder.item(item);
                    CustomItem customItem = new CustomItem(itemBuilder);

                    if (customItem.isFood()) {
                        event.setCancelled(true);

                        int nutrition = Objects.requireNonNull(itemBuilder.get(ItemComponent.FOOD)).nutrition();
                        int playerFoodLevel = Math.min(nutrition, 20);
                        float saturation = itemBuilder.get(ItemComponent.FOOD).saturationModifier();

                        player.setFoodLevel(player.getFoodLevel() + playerFoodLevel);
                        player.setSaturation(saturation);

                        if (item.getAmount() > 1) {
                            item.setAmount(item.getAmount() - 1);
                        } else {
                            player.getInventory().remove(item);
                        }

                        hasFood = true;
                        break;
                    }
                }
            }

            if (hasFood) {
                EAT.playSound(player);
            } else {
                // user.sendMessage(Message.NO_FOOD, woolValue);
                player.sendMessage("§7Kein Essen");
            }
        }
    }
}
