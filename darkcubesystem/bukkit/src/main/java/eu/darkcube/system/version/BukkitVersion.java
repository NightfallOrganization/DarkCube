/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.version;

import eu.darkcube.system.DarkCubeSystem;
import eu.darkcube.system.inventoryapi.item.ItemProvider;
import eu.darkcube.system.provider.Provider;

public class BukkitVersion implements Version {

    protected String classifier;
    protected CommandAPI commandApi;
    protected ItemProvider itemProvider;
    protected Provider provider;

    @Override
    public void init() {
        provider = Provider.newProvider();
    }

    public void enabled(DarkCubeSystem system) {
    }

    @Override
    public CommandAPI commandApi() {
        return commandApi;
    }

    @Override
    public ItemProvider itemProvider() {
        return itemProvider;
    }

    @Override
    public String getClassifier() {
        return classifier;
    }

    @Override
    public Provider provider() {
        return provider;
    }
}
