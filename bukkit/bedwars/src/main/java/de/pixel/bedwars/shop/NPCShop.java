package de.pixel.bedwars.shop;

import java.util.*;

import org.bukkit.*;
import org.bukkit.event.*;

import com.comphenix.protocol.wrappers.*;
import com.github.juliarn.npc.*;

import de.pixel.bedwars.*;

public class NPCShop extends Shop {

	private static Collection<NPCShop> shops = new HashSet<>();

//	private final NPC npc;
	private final NPC npc;
	private Listener listener;

	public NPCShop(Location loc) {
		super();
		shops.add(this);
		npc = NPC.builder().imitatePlayer(false).location(loc).build(Main.getInstance().getNpcPool());
		npc.getGameProfile().getProperties().put("textures", new WrappedSignedProperty("textures",
				"ewogICJ0aW1lc3RhbXAiIDogMTU5MTYyNjcyMjQ2MywKICAicHJvZmlsZUlkIiA6ICJiMGM5MjQ2ZTcyMDE0ZDI2YTc3NWFmNDIyMTZkOTUwMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJFbGl0ZURhcmtuZXNzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2RmNThhMmQ0Y2IyMDIzYWRhNmI4M2Y5NzliOTUyZjFlY2ZjNGI0Y2MwNmY0NzQxMGMwZWI3YjU4NGY0M2Q5YjUiCiAgICB9CiAgfQp9",
				"klyGMbTVCzsg+YjkMf2uBNdk1dUKOuq4pthi7uybUSlkZmClOfxPwXtWJ1n2fcfROmw5KO+J87zvsqLkdn5ky5yyQudy4texUS3gNxmA8trTM5QnSheUTyZDc/ceg378h2KUqkJiX/idWP/ZhN6ME83xj3/lDJU2Zlt/Fwg9DwPpRKJDJxDuiDLYm9c/JEbIHddy2i3LBIK5v2ePlIoXjLvKu/f+Rg3G/83t9t3qPkjJNcLh+BW9cEyBGTVjDKPAqo4P6CbtEEgaLv/bvt0I7VG+n40TdsHZWX371t4mzh19SJQwus6T4JKoga83o6qB/0AKr8dk2AGMA8Aj2JaGoL1Q7lAWBdyauG1hu0lMO3IvQpEm6hrQI/w64XW8mQoeQRPxJ3dd9PmpYY6voV3CTI3Ol5/XdyXQpuHaUl+yw3YUxKB9YckCkro5GHFvm7WzWa1YMK0soYy+5uXe4esD4XAH0BBIdVyFRWIFZ69H0WFi40rrEZHuZv6LACon61q+uCCtKEvHBmdX5nqK8tmktq6AHrLkSwV/gaQSF7+Bkq3MIBA4YnTMxtaqgkxSpqF9hdcS06EYoTvQpV6kMFxXRifhY6/WfR9dnZyKBsz2jUINxlExenfq5P4OuBynoeUZfARdi8H0GqykYxRwRsyeSUSFYgBaMoP5jiRuK9jtfwI="));
//		npc = new DefaultNPC(Main.getInstance(), loc, false, "§6Shop", false, null, false, p.getUniqueId());
//		npc.setSkin(new Skin() {
//			@Override
//			public String getValue() {
//				return "ewogICJ0aW1lc3RhbXAiIDogMTU5MTYyNjcyMjQ2MywKICAicHJvZmlsZUlkIiA6ICJiMGM5MjQ2ZTcyMDE0ZDI2YTc3NWFmNDIyMTZkOTUwMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJFbGl0ZURhcmtuZXNzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2RmNThhMmQ0Y2IyMDIzYWRhNmI4M2Y5NzliOTUyZjFlY2ZjNGI0Y2MwNmY0NzQxMGMwZWI3YjU4NGY0M2Q5YjUiCiAgICB9CiAgfQp9";
//			}
//
//			@Override
//			public String getSignature() {
//				return "klyGMbTVCzsg+YjkMf2uBNdk1dUKOuq4pthi7uybUSlkZmClOfxPwXtWJ1n2fcfROmw5KO+J87zvsqLkdn5ky5yyQudy4texUS3gNxmA8trTM5QnSheUTyZDc/ceg378h2KUqkJiX/idWP/ZhN6ME83xj3/lDJU2Zlt/Fwg9DwPpRKJDJxDuiDLYm9c/JEbIHddy2i3LBIK5v2ePlIoXjLvKu/f+Rg3G/83t9t3qPkjJNcLh+BW9cEyBGTVjDKPAqo4P6CbtEEgaLv/bvt0I7VG+n40TdsHZWX371t4mzh19SJQwus6T4JKoga83o6qB/0AKr8dk2AGMA8Aj2JaGoL1Q7lAWBdyauG1hu0lMO3IvQpEm6hrQI/w64XW8mQoeQRPxJ3dd9PmpYY6voV3CTI3Ol5/XdyXQpuHaUl+yw3YUxKB9YckCkro5GHFvm7WzWa1YMK0soYy+5uXe4esD4XAH0BBIdVyFRWIFZ69H0WFi40rrEZHuZv6LACon61q+uCCtKEvHBmdX5nqK8tmktq6AHrLkSwV/gaQSF7+Bkq3MIBA4YnTMxtaqgkxSpqF9hdcS06EYoTvQpV6kMFxXRifhY6/WfR9dnZyKBsz2jUINxlExenfq5P4OuBynoeUZfARdi8H0GqykYxRwRsyeSUSFYgBaMoP5jiRuK9jtfwI=";
//			}
//		});
//		npc.spawn(p);
	}

	@Override
	public void registerOpenListener() {
		if (listener == null) {
//			Bukkit.getPluginManager().registerEvents((listener = new Listener() {
//				@EventHandler
//				public void handle(NPCInteractEvent e) {
//					if (e.getNPC() == npc) {
//						Player p = e.getClicker();
//						open(p);
//					}
//				}
//
//				@EventHandler
//				public void handle(PlayerQuitEvent e) {
//					if (e.getPlayer().getUniqueId().equals(p.getUniqueId())) {
//						shops.remove(NPCShop.this);
//						npc.remove();
//						unregisterOpenListener();
//					}
//				}
//			}), Main.getInstance());
		}
	}

	@Override
	public void unregisterOpenListener() {
		HandlerList.unregisterAll(listener);
		listener = null;
	}

}
