package eu.darkcube.system.skyland;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.HashMap;
import java.util.Map;

public class TrainingStand implements CommandExecutor, Listener {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
			return false;
		}

		Player player = (Player) sender;

		if ((args.length == 0) && command.getName().equalsIgnoreCase("spawntrainingstand")) {

			World world = player.getWorld();
			Location location = player.getLocation().toBlockLocation().add(0.5, 0, 0.5);
			EntityType Stand = EntityType.ARMOR_STAND;
			Entity ST = world.spawnEntity(location, Stand);
			sender.sendMessage("§7Du hast einen §bTrainingStand §7gespawnt");
			ST.addScoreboardTag("TrainingStand");
			ItemStack itemH = new ItemStack(Material.LEATHER_HELMET);
			ItemStack itemC = new ItemStack(Material.LEATHER_CHESTPLATE);
			ItemStack itemL = new ItemStack(Material.LEATHER_LEGGINGS);
			ItemStack itemB = new ItemStack(Material.LEATHER_BOOTS);
			ItemMeta metaH = itemH.getItemMeta();
			ItemMeta metaC = itemC.getItemMeta();
			ItemMeta metaL = itemL.getItemMeta();
			ItemMeta metaB = itemB.getItemMeta();
			LeatherArmorMeta leatherarmormetaH = (LeatherArmorMeta) metaH;
			LeatherArmorMeta leatherarmormetaC = (LeatherArmorMeta) metaC;
			LeatherArmorMeta leatherarmormetaL = (LeatherArmorMeta) metaL;
			LeatherArmorMeta leatherarmormetaB = (LeatherArmorMeta) metaB;
			leatherarmormetaH.setColor(Color.fromBGR(134, 93, 135));
			leatherarmormetaC.setColor(Color.fromBGR(134, 93, 135));
			leatherarmormetaL.setColor(Color.fromBGR(134, 93, 135));
			leatherarmormetaB.setColor(Color.fromBGR(134, 93, 135));
			EntityEquipment eq = ((ArmorStand) ST).getEquipment();
			itemH.setItemMeta(metaH);
			itemC.setItemMeta(metaC);
			itemL.setItemMeta(metaL);
			itemB.setItemMeta(metaB);
			eq.setHelmet(itemH);
			eq.setChestplate(itemC);
			eq.setLeggings(itemL);
			eq.setBoots(itemB);
			return true;
		}

		sender.sendMessage(
				"§7Unbekannter Befehl. Nutze §b/spawntrainingstand §7um einen TrainingStand zu spawnen");
		return false;
	}

	Map<Entity, BukkitRunnable> runnables = new HashMap<>();

	@SuppressWarnings("deprecation")
	@EventHandler
	public void SpawnStand(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player) || ((Player) e.getDamager()).isSneaking())
			return;

		if (!(e.getEntity().getScoreboardTags().contains("TrainingStand")))
			return;

		if (runnables.containsKey(e.getEntity()))
			runnables.get(e.getEntity()).cancel();

		e.setCancelled(true);
		double d = Math.round(e.getDamage() * 10) / 10;
		e.getEntity().setCustomName(d + "");
		e.getEntity().setCustomNameVisible(true);

		BukkitRunnable run = new BukkitRunnable() {
			@Override
			public void run() {
				e.getEntity().setCustomNameVisible(false);
				this.cancel();
				runnables.remove(e.getEntity());
			}
		};
		run.runTaskTimer(Skyland.getInstance(), 60, 20);
		runnables.put(e.getEntity(), run);
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		if (e.getEntity().getScoreboardTags().contains("TrainingStand"))
			e.getDrops().clear();
	}

	@EventHandler
	public void onArmorstandInteract(PlayerInteractAtEntityEvent e) {
		if (e.getRightClicked().getScoreboardTags().contains("TrainingStand"))
			e.setCancelled(true);
	}

}
