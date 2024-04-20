package eu.darkcube.system.server.impl.inventory.animated;

import java.time.Duration;
import java.util.Arrays;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.impl.inventory.InventoryTemplateImpl;
import eu.darkcube.system.server.inventory.InventoryTemplate;
import eu.darkcube.system.server.inventory.animated.AnimatedTemplateSettings;

public class AnimatedTemplateSettingsImpl implements AnimatedTemplateSettings {
    private final InventoryTemplateImpl template;
    private final Duration[] durations;

    public AnimatedTemplateSettingsImpl(InventoryTemplateImpl template) {
        this.template = template;
        this.durations = new Duration[template.size()];
        Arrays.fill(this.durations, Duration.ZERO);
    }

    @Override
    public boolean hasAnimation() {
        for (var duration : durations) {
            if (!duration.isZero()) return true;
        }
        return false;
    }

    @Override
    public void showAfter(int slot, @NotNull Duration duration) {
        durations[slot] = duration;
    }

    @Override
    public @NotNull Duration getShowAfter(int slot) {
        return durations[slot];
    }

    @Override
    public @NotNull InventoryTemplate inventoryTemplate() {
        return template;
    }
}
