package eu.darkcube.system.miners.items;

import eu.darkcube.system.inventoryapi.ItemBuilder;
import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.player.Message;
import eu.darkcube.system.util.Language;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public enum Item {

    PICKAXE_STONE(new ItemBuilder(Material.STONE_PICKAXE).unbreakable(true)),
    PICKAXE_IRON(new ItemBuilder(Material.IRON_PICKAXE).unbreakable(true)),
    PICKAXE_GOLD(new ItemBuilder(Material.GOLD_PICKAXE).unbreakable(true)),
    PICKAXE_DIAMOND(new ItemBuilder(Material.DIAMOND_PICKAXE).unbreakable(true)),

    INGOT_IRON(new ItemBuilder(Material.IRON_INGOT)),
    INGOT_GOLD(new ItemBuilder(Material.GOLD_INGOT)),
    DIAMOND(new ItemBuilder(Material.DIAMOND)),
    COBBLESTONE(new ItemBuilder(Material.COBBLESTONE)),
    TNT(new ItemBuilder(Material.TNT)),
    SHEARS(new ItemBuilder(Material.SHEARS)),
    CRAFTING_TABLE(new ItemBuilder(Material.WORKBENCH)),
    STICK(new ItemBuilder(Material.STICK)),
    FLINT(new ItemBuilder(Material.FLINT)),
    FLINT_AND_STEEL(new ItemBuilder(Material.FLINT_AND_STEEL));

    public static final String ITEM_PREFIX = "ITEM_";
    public static final String NAME_SUFFIX = "_NAME";
    public static final String LORE_SUFFIX = "_LORE";

    private final ItemBuilder itemBuilder;

    private final String KEY_NAME;
    private final String KEY_LORE;

    Item(ItemBuilder ib) {
        this.itemBuilder = ib;
        ib.unsafe().setString("ITEM", this.name());
        this.KEY_NAME = ITEM_PREFIX + this.name() + NAME_SUFFIX;
        this.KEY_LORE = ITEM_PREFIX + this.name() + LORE_SUFFIX;
    }

    public String getName(Language lang) {
        return Message.getMessage(KEY_NAME, lang);
    }

    public String getLore(Language lang) {
        return Message.getMessage(KEY_LORE, lang);
    }

    public ItemStack getItem(Language lang, int amount) {
        String lore = getLore(lang);
        return lore.equals("") ? itemBuilder.displayname(getName(lang)).amount(amount).build() :
                itemBuilder.displayname(getName(lang)).lore(Arrays.asList(getLore(lang).split("\n"))).amount(amount).build();
    }

    public ItemStack getItem(Player player, int amount) {
        return getItem(Miners.getPlayerManager().getMinersPlayer(player).getLanguage(), amount);
    }

    public ItemStack getItem(Player player) {
        return getItem(player, 1);
    }

}
