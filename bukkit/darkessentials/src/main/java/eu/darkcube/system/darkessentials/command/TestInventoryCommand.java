package eu.darkcube.system.darkessentials.command;

import eu.darkcube.system.darkessentials.DarkCommand;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.inventory.Inventory;
import eu.darkcube.system.server.inventory.InventoryMask;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.TemplateInventory;
import eu.darkcube.system.server.inventory.item.ItemTemplate;
import eu.darkcube.system.server.inventory.listener.TemplateInventoryListener;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import org.bukkit.Material;

public class TestInventoryCommand extends DarkCommand {
    private static final String MASK_5x9 = """
            lllldllll
            dddlllddd
            ldddldddl
            dldldldld
            ldldldldl
            """;
    private static final ItemTemplate items1;
    private static final InventoryTemplate inv1;

    public TestInventoryCommand() {
        super("testinventory", b -> b.executes(ctx -> {
            var player = ctx.getSource().asPlayer();
            inv1.open(player);
            return 0;
        }));
    }

    static {
        items1 = ItemTemplate.create();
        items1.setItem(ItemBuilder.item(Material.APPLE), InventoryMask.slots(MASK_5x9, 'l'));
        items1.setItem(ItemBuilder.item(Material.STONE), InventoryMask.slots(MASK_5x9, 'd'));

        inv1 = Inventory.createChestTemplate(Key.key("darkcubesystem", "inv1"), 45);
        inv1.setItems(100, items1);
        inv1.animation().calculateManifold(22, 3);
        // inv1.pagination().pageSlots(<slots>);
        // inv1.pagination().content().addStaticItem(...)
        inv1.addListener(new TemplateInventoryListener() {
            @Override
            public void onClick(@NotNull TemplateInventory inventory, @NotNull User user, int slot, @NotNull ItemBuilder item) {
                user.sendMessage(Component.text("Click " + slot));
            }
        });
    }
}
