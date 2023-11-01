package de.mert.friendssystem.guis;

import de.mert.friendssystem.Helper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.paginate.PaginatedMenuBuilder;
import org.ipvp.canvas.slot.ClickOptions;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;

import java.util.List;

public abstract class MainGUI {
    private Mask mask;
    protected PaginatedMenuBuilder builder;
    protected final Player player;
    private final String title;
    private final ItemStack switchViewItem;

    public MainGUI(Player player, String title, ItemStack switchViewItem) {
        this.player = player;
        this.title = title;
        this.switchViewItem = switchViewItem;

        build();
    }

    public abstract void open();
    protected abstract Slot.ClickHandler switchView();

    private void build() {
        Menu.Builder<ChestMenu.Builder> pageTemplate = ChestMenu.builder(6)
                .title(title)
                .redraw(true);

        Mask itemSlots = BinaryMask.builder(pageTemplate.getDimensions())
                .pattern("111111111")
                .pattern("111111111")
                .pattern("111111111")
                .pattern("111111111")
                .pattern("000000000")
                .pattern("000000000").build();

        builder = PaginatedMenuBuilder.builder(pageTemplate)
                .slots(itemSlots)
                .nextButton(Helper.itemBuilder(Material.ARROW, "Next page"))
                .nextButtonSlot(9 * 5 + 5)
                .previousButton(Helper.itemBuilder(Material.ARROW, "Previous page"))
                .previousButtonSlot(9 * 5 + 3);

        mask = BinaryMask.builder(pageTemplate.getDimensions())
                .item(Helper.itemBuilder(Material.STAINED_GLASS_PANE, ""))
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("111111111")
                .pattern("000000000").build();
    }

    protected void openInv() {
        List<Menu> pages = builder.build();
        for (Menu page : pages) {
            mask.apply(page);

            Slot slot = page.getSlot(9 * 6 - 1);
            slot.setItem(switchViewItem);

            ClickOptions options = ClickOptions.builder()
                    .allow(ClickType.LEFT, ClickType.RIGHT)
                    .build();
            slot.setClickOptions(options);

            slot.setClickHandler(switchView());
        }

        pages.get(0).open(player);
    }
}