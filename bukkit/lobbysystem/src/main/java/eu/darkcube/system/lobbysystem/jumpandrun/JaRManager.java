package eu.darkcube.system.lobbysystem.jumpandrun;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;

public class JaRManager {

	final List<JaRRegion> regions = new ArrayList<>();
	private final Database database;

	public JaRManager() {
		database = CloudNetDriver.getInstance().getDatabaseProvider()
				.getDatabase("lobbysystem_jumpandrun");
		queryRegions();
	}

	private void queryRegions() {
		regions.clear();
		if (database.contains("regions")) {
			JsonDocument dregions = database.get("regions");
			for (String key : dregions.keys()) {
				JaRRegion region = JaRRegion.deserialize(dregions.getString(key));
				regions.add(region);
			}
		}
	}

	public void saveRegions() {
		JsonDocument dregions = new JsonDocument();
		int i = 0;
		for (JaRRegion r : regions) {
			dregions.append(Integer.toString(i++), r.serialize());
		}
		if (!database.contains("regions")) {
			database.insert("regions", dregions);
		} else {
			database.update("regions", dregions);
		}
	}

	public List<JaRRegion> getRegions() {
		return this.regions;
	}

	public JaR createJaR(Player player) {
		return new JaR(this, player);
	}

	public void startJaR(Player player) {
		JaR jar = createJaR(player);
		Block cur = jar.getCurrentBlock();
		if (cur == null) {
			return;
		}
		player.teleport(cur.getLocation().add(0.5, 1, 0.5));
	}

}
