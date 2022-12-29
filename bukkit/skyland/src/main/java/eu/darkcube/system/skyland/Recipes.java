package eu.darkcube.system.skyland;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public class Recipes {

    private static final Map<NamespacedKey, Recipe> recipes = new ConcurrentHashMap<>();

    public static void addRecipe(ShapedRecipe recipe) {
        recipes.put(recipe.getKey(), recipe);
        Bukkit.addRecipe(recipe);
        giveToPlayers(recipe.getKey());
    }

    public static void removeRecipe(NamespacedKey key) {
        recipes.remove(key);
        removeFromPlayers(key);
        Bukkit.removeRecipe(key);
    }

    private static void removeFromPlayers(NamespacedKey key) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasDiscoveredRecipe(key)) {
                p.undiscoverRecipe(key);
            }
        }
    }

    public static void giveToPlayers(NamespacedKey key) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasDiscoveredRecipe(key)) {
                p.discoverRecipe(key);
            }
        }
    }

    public static void giveToPlayer(Player p) {
        for (NamespacedKey key : recipes.keySet()) {
            if (!p.hasDiscoveredRecipe(key)) {
                p.discoverRecipe(key);
            }
        }
    }

    public static class RecipeListener implements Listener {

        @EventHandler
        public void handle(PlayerJoinEvent event) {
            giveToPlayer(event.getPlayer());
        }
    }
}
