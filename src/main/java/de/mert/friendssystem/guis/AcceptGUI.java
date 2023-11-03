package de.mert.friendssystem.guis;

import de.mert.friendssystem.Helper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.slot.ClickOptions;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;

public class AcceptGUI {
    private final Player player;
    private Menu menu;
    private final String title;
    private Slot.ClickHandler acceptHandler = (player, clickInformation) -> {};
    private Slot.ClickHandler denyHandler = (player, clickInformation) -> {};
    private String acceptText = "§6Accept";
    private String denyText = "§cDeny";

    public AcceptGUI(Player player, String title) {
        this.player = player;
        this.title = title;
    }

    public void open() {
        build();

        menu.open(player);
    }

    public void setAcceptHandler(Slot.ClickHandler handler) {
        this.acceptHandler = handler;
    }

    public void setDenyHandler(Slot.ClickHandler handler) {
        this.denyHandler = handler;
    }

    public void setAcceptText(String text) {
        this.acceptText = text;
    }

    public void setDenyText(String text) {
        this.denyText = text;
    }

    private void build() {
        menu = ChestMenu.builder(3)
                .title(title)
                .build();

        Mask mask = BinaryMask.builder(menu)
                .item(Helper.itemBuilder(Material.STAINED_GLASS_PANE, " ", (short) 7))
                .pattern("111111111")
                .pattern("111010111")
                .pattern("111111111")
                .build();

        mask.apply(menu);

        ClickOptions options = ClickOptions.builder()
                .allow(ClickType.LEFT, ClickType.RIGHT)
                .build();

        Slot accept = menu.getSlot(2, 4);
        accept.setClickOptions(options);
        accept.setItem(Helper.itemBuilder(Material.GOLD_BLOCK, acceptText));
        accept.setClickHandler(acceptHandler);

        Slot deny = menu.getSlot(2, 6);
        deny.setClickOptions(options);
        deny.setItem(Helper.itemBuilder(Material.REDSTONE_BLOCK, denyText));
        deny.setClickHandler(denyHandler);
    }
}