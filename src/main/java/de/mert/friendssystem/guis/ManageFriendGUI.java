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

public class ManageFriendGUI {
    public Player player;
    private Menu menu;

    public ManageFriendGUI(Player player) {
        this.player = player;

        build();
    }

    private void build() {
        menu = ChestMenu.builder(3)
                .title("Friends Menu")
                .build();

        Mask mask = BinaryMask.builder(menu)
                .item(Helper.itemBuilder(Material.STAINED_GLASS_PANE, ""))
                .pattern("111111111")
                .pattern("100000001")
                .pattern("011111111")
                .build();

        mask.apply(menu);

        menu.getSlot(10).setItem(Helper.itemBuilder(Material.COBBLESTONE, ""));

        ClickOptions options = ClickOptions.builder()
                .allow(ClickType.LEFT, ClickType.RIGHT)
                .build();

        Slot backButton = menu.getSlot(9 * 2);
        backButton.setItem(Helper.itemBuilder(Material.STAINED_GLASS_PANE, "Back"));
        backButton.setClickOptions(options);
        backButton.setClickHandler((player, info) -> new FriendsGUI(player).open());
    }

    public void open() {
        menu.open(player);
    }
}
