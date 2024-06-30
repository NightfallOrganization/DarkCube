package building.oneblock.items;

import building.oneblock.OneBlock;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Bukkit;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;

public class RodOfTheSkyItem implements Listener {
    private static final long COOLDOWN_IN_MILLIS = 3000;

    public static ItemStack createRodOfTheSky() {
        ItemStack rodOfTheSky = new ItemStack(Material.FIREWORK_STAR, 1);
        ItemMeta meta = rodOfTheSky.getItemMeta();

        if (meta != null) {
            meta.setCustomModelData(2);
            meta.setDisplayName("§7Rod of the §aSky");
            rodOfTheSky.setItemMeta(meta);
        }

        return rodOfTheSky;
    }

    public static void registerRodOfTheSkyRecipe() {
        ItemStack rodOfTheSky = createRodOfTheSky();
        NamespacedKey key = new NamespacedKey(OneBlock.getInstance(), "rod_of_the_sky");
        ShapedRecipe recipe = new ShapedRecipe(key, rodOfTheSky);

        recipe.shape("A2A", " B ", " B ");
        recipe.setIngredient('A', Material.EMERALD_BLOCK);
        recipe.setIngredient('B', Material.STICK);
        recipe.setIngredient('2', new RecipeChoice.ExactChoice(TotemOfGravityItem.createTotemofGravityItem()));

        Bukkit.addRecipe(recipe);
    }

    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item != null && item.getType() == Material.FIREWORK_STAR && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer data = meta.getPersistentDataContainer();
            String key = "lastUse";
            long lastUseTime = 0;
            if (data.has(new NamespacedKey(OneBlock.getInstance(), key), PersistentDataType.LONG)) {
                lastUseTime = data.get(new NamespacedKey(OneBlock.getInstance(), key), PersistentDataType.LONG);
            }

            long currentTime = System.currentTimeMillis();
            long timeSinceLastUse = currentTime - lastUseTime;
            if (item.getItemMeta().getCustomModelData() == 2) {
                if (timeSinceLastUse < COOLDOWN_IN_MILLIS) {
                    long timeLeft = COOLDOWN_IN_MILLIS - timeSinceLastUse;
                    long secondsLeft = TimeUnit.MILLISECONDS.toSeconds(timeLeft) + 1;

                    player.sendMessage("§7Time left: §a" + secondsLeft);
                    event.setCancelled(true);
                    return;
                }

                if (event.getAction().toString().contains("RIGHT_CLICK")) {
                    Vector direction = player.getLocation().getDirection();
                    direction.setY(1);
                    player.setVelocity(player.getVelocity().add(direction.normalize().multiply(40)));
                    player.playSound(player.getLocation(), Sound.ITEM_ELYTRA_FLYING, 1.0F, 1.0F);

                    data.set(new NamespacedKey(OneBlock.getInstance(), key), PersistentDataType.LONG, currentTime);
                    player.getPersistentDataContainer().set(new NamespacedKey(OneBlock.getInstance(), "noFallDamage"), PersistentDataType.BYTE, (byte) 1);
                    item.setItemMeta(meta);

                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            Player player = (Player) event.getEntity();
            Byte noFallDamage = player.getPersistentDataContainer().get(new NamespacedKey(OneBlock.getInstance(), "noFallDamage"), PersistentDataType.BYTE);
            if (noFallDamage != null && noFallDamage == 1) {
                event.setCancelled(true);
                player.getPersistentDataContainer().remove(new NamespacedKey(OneBlock.getInstance(), "noFallDamage"));
            }
        }
    }
}
