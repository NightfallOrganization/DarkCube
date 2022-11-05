package eu.darkcube.system.lobbysystem.inventory.pserver.gameserver;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.inventory.abstraction.InventoryType;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager.PServerUserSlots.PServerUserSlot;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;

public class InventoryGameServerSelectionWoolBattle extends InventoryGameServerSelection {

	public InventoryGameServerSelectionWoolBattle(User user, PServerUserSlot psslot, int slot) {
		super(user, Item.GAMESERVER_SELECTION_WOOLBATTLE, InventoryType.GAMESERVER_SELECTION_WOOLBATTLE, new Sup(),
				new Func(), psslot, slot);
	}

	@Override
	protected void insertDefaultItems(InventoryManager manager) {
		super.insertDefaultItems(manager);
	}

	public static class Func implements BiFunction<User, ServiceTask, ItemBuilder> {

		private static final Pattern pattern = Pattern.compile("\\d");

		@Override
		public ItemBuilder apply(User user, ServiceTask t) {
			Matcher matcher = Func.pattern.matcher(t.getName());
			String text;
			if (matcher.find()) {
				text = t.getName().substring(matcher.start(), t.getName().length());
			} else {
				text = "Invalid Task!";
			}
			return new ItemBuilder(Item.GAMESERVER_WOOLBATTLE.getItem(user, text));
		}
	}

	public static class Sup implements Supplier<Collection<ServiceTask>> {

		@Override
		public Collection<ServiceTask> get() {
			final CloudNetDriver cnd = CloudNetDriver.getInstance();
			return Lobby.getInstance()
					.getDataManager()
					.getWoolBattleTasks()
					.stream()
					.filter(s -> cnd.getServiceTaskProvider().isServiceTaskPresent(s))
					.map(s -> cnd.getServiceTaskProvider().getServiceTask(s))
					.collect(Collectors.toList());
		}
	}
}
