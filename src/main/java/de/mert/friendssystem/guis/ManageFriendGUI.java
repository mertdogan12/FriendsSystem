package de.mert.friendssystem.guis;

import de.mert.friendssystem.Helper;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
    public OfflinePlayer friend;
    private Menu menu;

    public ManageFriendGUI(Player player, OfflinePlayer friend) {
        this.player = player;
        this.friend = friend;

        build();
    }

    private void build() {
        menu = ChestMenu.builder(3)
                .title("Friends Menu: " + friend.getName())
                .build();

        Mask mask = BinaryMask.builder(menu)
                .item(Helper.itemBuilder(Material.STAINED_GLASS_PANE, "", (short) 7))
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
        backButton.setItem(Helper.itemBuilder(Material.STAINED_GLASS_PANE, "Back", (short) 14));
        backButton.setClickOptions(options);
        backButton.setClickHandler((player, info) -> new FriendsGUI(player).open());
    }

    public void open() {
        menu.open(player);
    }
}
