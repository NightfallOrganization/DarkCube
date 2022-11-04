package eu.darkcube.minigame.woolbattle.perk;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableInteger;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableObject;
import eu.darkcube.minigame.woolbattle.util.observable.SimpleObservableInteger;

public class PerkTypePerk implements Perk {

	private final PerkType type;
	private final User owner;
	private final Player player;
	private final ObservableInteger slot;
	private final ObservableInteger cooldown;
	private final PerkNumber perkNumber;
	private boolean hardCooldown = true;

	public PerkTypePerk(PerkType type, User owner, PerkNumber perkNumber) {
		this.perkNumber = perkNumber;
		this.owner = owner;
		this.type = type;
		this.player = Bukkit.getPlayer(owner.getUniqueId());
		this.cooldown = new SimpleObservableInteger(0) {
			@Override
			public void onChange(ObservableObject<Integer> instance, Integer oldValue, Integer newValue) {
				if (newValue == 0)
					setHardCooldown(false);
				if (!isHardCooldown() && newValue == getMaxCooldown())
					setHardCooldown(true);
				setItem();
			}

			@Override
			public void onSilentChange(ObservableObject<Integer> instance, Integer oldValue, Integer newValue) {
			}
		};
		this.slot = new SimpleObservableInteger() {
			@Override
			public void onChange(ObservableObject<Integer> instance, Integer oldValue, Integer newValue) {
				if (oldValue != null) {
					if (oldValue == 100)
						player.getOpenInventory().setCursor(null);
					else
						player.getOpenInventory().setItem(oldValue, null);
				}
				perkNumber.setRawSlot(owner, newValue);
				setItem();
			}

			@Override
			public void onSilentChange(ObservableObject<Integer> instance, Integer oldValue, Integer newValue) {
				perkNumber.setRawSlot(owner, newValue);
			}
		};
		if (perkNumber != PerkNumber.DISPLAY)
			setSlot(perkNumber.getRawSlot(owner));
		if (type == PerkType.LINE_BUILDER)
			hardCooldown = false;
	}

	@Override
	public boolean isHardCooldown() {
		return hardCooldown;
	}

	@Override
	public void setHardCooldown(boolean hardCooldown) {
		this.hardCooldown = hardCooldown;
	}

	@Override
	public int getMaxCooldown() {
		return type.getCooldown();
	}

	@Override
	public boolean hasCooldown() {
		return type.hasCooldown();
	}

	@Override
	public Item getCooldownItem() {
		return type.getCooldownItem();
	}

	@Override
	public Item getItem() {
		return type.getItem();
	}

	@Override
	public ItemStack calculateItem() {
		if (getCooldown() == 0
				|| ((type == PerkType.LINE_BUILDER) && getCooldown() != getMaxCooldown() && !isHardCooldown())) {
			ItemStack item = getItem().getItem(owner);
			if (type == PerkType.GRABBER
					&& Main.getInstance().getIngame().listenerGrabberInteract.grabbed.containsKey(owner)) {
				item = Item.PERK_GRABBER_GRABBED.getItem(owner);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(
						Main.getInstance().getIngame().listenerGrabberInteract.grabbed.get(owner).getTeamPlayerName());
				item.setItemMeta(meta);
			} else if (type == PerkType.ARROW_RAIN) {
				ItemMeta meta = item.getItemMeta();
				meta.addEnchant(Enchantment.ARROW_INFINITE, 100, true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
			} else if (type == PerkType.TNT_ARROW) {
				ItemMeta meta = item.getItemMeta();
				meta.addEnchant(Enchantment.ARROW_INFINITE, 100, true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
			}
			return item;
		}
		ItemStack item = getCooldownItem().getItem(owner);
		item.setAmount(getCooldown());
		return item;
	}

	@Override
	public PerkName getPerkName() {
		return type.getPerkName();
	}

	@Override
	public String getDisplayName() {
		return getItem().getDisplayName(owner);
	}

	@Override
	public ObservableInteger getSlotLink() {
		return slot;
	}

	@Override
	public void setSlot(int slot) {
		this.slot.setObject(slot);
	}

	@Override
	public int getSlot() {
		return slot.getObject();
	}

	@Override
	public User getOwner() {
		return owner;
	}

	@Override
	public ObservableInteger getCooldownLink() {
		return cooldown;
	}

	@Override
	public void setCooldown(int cooldown) {
		this.cooldown.setObject(cooldown);
	}

	@Override
	public int getCooldown() {
		return cooldown.getObject();
	}

	@Override
	public PerkNumber getPerkNumber() {
		return perkNumber;
	}

	@Override
	public void setSlotSilent(int slot) {
		this.slot.setSilent(slot);
	}
}