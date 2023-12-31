package de.mert.friendssystem.guis;

import de.mert.friendssystem.Helper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.paginate.PaginatedMenuBuilder;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;

import java.util.List;

public abstract class MainGUI {
    private Mask mask;
    protected PaginatedMenuBuilder builder;
    protected final Player player;
    private final String title;

    public MainGUI(Player player, String title) {
        this.player = player;
        this.title = title;

        build();
    }

    public abstract void open();

    protected abstract void setMenuBar(List<Slot> menuBar);

    private void build() {
        Menu.Builder<ChestMenu.Builder> pageTemplate = ChestMenu.builder(6)
                .title(title)
                .redraw(false);

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
                .item(Helper.itemBuilder(Material.STAINED_GLASS_PANE, " ", (short) 7))
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
            Mask menuBarmask = BinaryMask.builder(page.getDimensions())
                    .pattern("000000000")
                    .pattern("000000000")
                    .pattern("000000000")
                    .pattern("000000000")
                    .pattern("000000000")
                    .pattern("111111111")
                    .build();

            mask.apply(page);
            setMenuBar(page.getSlots(menuBarmask));
        }

        pages.get(0).open(player);
    }
}