package eu.darkcube.system.pserver.common;

import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.property.DefaultJsonServiceProperty;
import de.dytanic.cloudnet.driver.service.property.ServiceProperty;

public abstract class ServiceInfoUtil {

	private static ServiceInfoUtil instance;
	public static final ServiceProperty<UniqueId> property_uid = DefaultJsonServiceProperty
			.createFromClass("unique_id", UniqueId.class);

	public ServiceInfoUtil() {
		instance = this;
	}
	
	public UniqueId getUniqueId(ServiceInfoSnapshot snapshot) {
		return snapshot.getProperty(property_uid).orElse(null);
	}

	public static ServiceInfoUtil getInstance() {
		return instance;
	}
}
