package de.mert.friendssystem.guis;

import de.mert.friendssystem.Helper;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.mask.RecipeMask;
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
        menu = ChestMenu.builder(4)
                .title("Friends Menu: " + friend.getName())
                .build();

        Mask mask = RecipeMask.builder(menu)
                .item('l', Helper.itemBuilder(Material.STAINED_GLASS_PANE, " ", (short) 7))
                .item('d', Helper.itemBuilder(Material.STAINED_GLASS_PANE, " ", (short) 15))
                .pattern("0dddddddd")
                .pattern("lllllllll")
                .pattern("l0000000l")
                .pattern("lllllllll")
                .build();

        mask.apply(menu);

        ClickOptions options = ClickOptions.builder()
                .allow(ClickType.LEFT, ClickType.RIGHT)
                .build();

        Slot deleteFriend = menu.getSlot(3, 8);
        deleteFriend.setItem(Helper.itemBuilder(Material.BARRIER, "§cRemove friend"));
        deleteFriend.setClickOptions(options);
        deleteFriend.setClickHandler((player, info) -> new RemoveFriendGUI(player, friend).open());

        Slot backButton = menu.getSlot(0);
        backButton.setItem(Helper.itemBuilder(Material.REDSTONE, "§4> Back <"));
        backButton.setClickOptions(options);
        backButton.setClickHandler((player, info) -> new FriendsGUI(player).open());
    }

    public void open() {
        menu.open(player);
    }
}
