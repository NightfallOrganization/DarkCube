/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common;

import eu.cloudnetservice.driver.document.property.DocProperty;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;

public abstract class ServiceInfoUtil {

    public static final DocProperty<UniqueId> property_uid = DocProperty.property("pserver_unique_id", UniqueId.class);
    private static ServiceInfoUtil instance;

    public ServiceInfoUtil() {
        instance = this;
    }

    public static ServiceInfoUtil getInstance() {
        return instance;
    }

    public UniqueId getUniqueId(ServiceInfoSnapshot snapshot) {
        return snapshot.readPropertyOrDefault(property_uid, null);
    }
}
