package eu.darkcube.system.server.impl.inventory.paged;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.impl.inventory.item.ItemReferenceImpl;
import eu.darkcube.system.server.inventory.paged.PageButton;

public class PageButtonImpl implements PageButton {
    private Visibility visibility = Visibility.WHEN_USABLE;
    private int[] slots = new int[0];
    private @Nullable ItemReferenceImpl item = null;

    @Override
    public int @NotNull [] slots() {
        return slots;
    }

    @Override
    public void slots(int @NotNull [] slots) {
        this.slots = slots.clone();
    }

    @Override
    public @NotNull ItemReferenceImpl setItem(@NotNull Object item) {
        return this.item = new ItemReferenceImpl(item);
    }

    @Override
    public @NotNull Visibility visibility() {
        return visibility;
    }

    @Override
    public void visibility(@NotNull Visibility visibility) {
        this.visibility = visibility;
    }

    public @Nullable ItemReferenceImpl item() {
        return item;
    }
}
