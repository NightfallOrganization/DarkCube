/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.module;

import dev.derklaro.aerogel.Inject;
import dev.derklaro.aerogel.Singleton;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.provider.CloudServiceProvider;
import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import eu.cloudnetservice.driver.service.*;
import eu.cloudnetservice.node.event.instance.CloudNetTickServiceStartEvent;
import eu.darkcube.system.DarkCubeServiceProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Singleton public class ServiceListener {
    private final ServiceHelper woolbattle;
    private final ServiceTaskProvider serviceTaskProvider;
    private final CloudServiceProvider cloudServiceProvider;

    @Inject
    public ServiceListener(ServiceHelper woolbattle, ServiceTaskProvider serviceTaskProvider, CloudServiceProvider cloudServiceProvider) {
        this.woolbattle = woolbattle;
        this.serviceTaskProvider = serviceTaskProvider;
        this.cloudServiceProvider = cloudServiceProvider;
    }

    @EventListener public void handle(CloudNetTickServiceStartEvent event) {
        for (ServiceHelperConfig.ServiceTaskEntry entry : woolbattle.config().entries()) {
            handleServices(entry);
        }
    }

    private void handleServices(ServiceHelperConfig.ServiceTaskEntry entry) {
        String taskName = entry.taskName();
        int minServiceCount = entry.minServiceCount();
        int discrepancy = entry.discrepancy();
        List<ServiceInfoSnapshot> unconfiguredServices = new ArrayList<>();
        for (ServiceInfoSnapshot snapshot : cloudServiceProvider.servicesByTask(taskName)) {
            @Nullable Document snapshotExtra = snapshot.readProperty(DarkCubeServiceProperty.EXTRA);
            boolean snapshotConfigured = snapshotExtra != null && snapshotExtra.getBoolean("configured", false);

            if (!snapshotConfigured) {
                unconfiguredServices.add(snapshot);
            }
        }
        int unconfiguredCount = unconfiguredServices.size();
        ServiceTask task = serviceTaskProvider.serviceTask(taskName);
        if (task == null) return;
        if (minServiceCount > unconfiguredCount) startService(task);
        if (minServiceCount + discrepancy < unconfiguredCount) stopUnconfigured(unconfiguredServices);
    }

    private void stopUnconfigured(List<ServiceInfoSnapshot> unconfigured) {
        // Stop the service that was connected the longest
        unconfigured
                .stream()
                .min(Comparator.comparingLong(ServiceInfoSnapshot::creationTime))
                .ifPresent(snapshot -> snapshot.provider().stop());
    }

    private void startService(ServiceTask task) {
        ServiceCreateResult result = ServiceConfiguration
                .builder(task)
                .retryConfiguration(ServiceCreateRetryConfiguration.NO_RETRY)
                .build()
                .createNewService();
        if (result.state() != ServiceCreateResult.State.CREATED) {
            System.out.println("Failed to create service for task " + task.name());
            return;
        }
        result.serviceInfo().provider().start();
    }
}
