/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.link.luckperms;

import de.dytanic.cloudnet.wrapper.Wrapper;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import org.bukkit.entity.Player;

public class LinkContextCalculator implements ContextCalculator<Player> {
	private final String task = Wrapper.getInstance().getServiceId().getTaskName();

	@Override
	public void calculate(@NotNull Player target, @NotNull ContextConsumer consumer) {
		consumer.accept("cloudtask", task);
	}
}
