package eu.darkcube.minigame.woolbattle.perk;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableInteger;
import eu.darkcube.minigame.woolbattle.util.observable.ObservableObject;
import eu.darkcube.minigame.woolbattle.util.observable.SimpleObservableInteger;

public class PerkEnderPearl implements Perk {

	public static int COOLDOWN = 5;
	public static int COST = 8;
	private final User owner;
	private final Player player;
	private final ObservableInteger slot;
	private final ObservableInteger cooldown;
	private final PerkNumber perkNumber;

	public PerkEnderPearl(User owner) {
		this.perkNumber = PerkNumber.ENDER_PEARL;
		this.owner = owner;
		this.player = Bukkit.getPlayer(owner.getUniqueId());
		this.cooldown = new SimpleObservableInteger(0) {
			@Override
			public void onChange(ObservableObject<Integer> instance, Integer oldValue, Integer newValue) {
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
		setSlot(perkNumber.getRawSlot(owner));
	}

	@Override
	public int getMaxCooldown() {
		return COOLDOWN;
	}

	@Override
	public boolean hasCooldown() {
		return true;
	}

	@Override
	public Item getCooldownItem() {
		return Item.DEFAULT_PEARL_COOLDOWN;
	}

	@Override
	public Item getItem() {
		return Item.DEFAULT_PEARL;
	}

	@Override
	public ItemStack calculateItem() {
		if (getCooldown() == 0) {
			return getItem().getItem(owner);
		}
		ItemStack item = getCooldownItem().getItem(owner);
		item.setAmount(getCooldown());
		return item;
	}

	@Override
	public PerkName getPerkName() {
		return new PerkName("ENDER_PEARL");
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

	@Override
	public boolean isHardCooldown() {
		return true;
	}

	@Override
	public void setHardCooldown(boolean hardCooldown) {
	}
}
