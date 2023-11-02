package de.mert.friendssystem.guis;

import de.mert.friendssystem.Friends;
import de.mert.friendssystem.Helper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.slot.ClickOptions;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.slot.SlotSettings;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


public class FriendsGUI extends MainGUI {
    public FriendsGUI(Player player) {
        super(player, Helper.itemBuilder(Material.GOLD_INGOT, "Friend requests"), "Friends");
    }

    public void handleClick(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        if (item == null)
            return;

        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta == null)
            return;

        String itemName = itemMeta.getDisplayName();
        if (itemName == null)
            return;

        if (itemName.equals("Friends"))
            open();
    }

    @Override
    public void open() {
        new Friends(player.getUniqueId()).getFriends(result -> {
            if (!result.isPresent()) {
                player.sendMessage("§cError while opening friends menu");
                return;
            }

            ClickOptions options = ClickOptions.builder()
                    .allow(ClickType.LEFT, ClickType.RIGHT)
                    .build();

            List<SlotSettings> tmp = new LinkedList<>();
            for (UUID id : result.get()) {
                OfflinePlayer friend = Bukkit.getOfflinePlayer(id);

                Slot.ClickHandler handler = (player, info) -> new ManageFriendGUI(player).open();

                SlotSettings.Builder settingsBuilder = SlotSettings.builder()
                        .clickOptions(options)
                        .clickHandler(handler);

                if (friend.isOnline()) {
                    settingsBuilder.item(Helper.getPlayerHead(friend.getPlayer()));
                    builder.addItem(settingsBuilder.build());
                } else {
                    settingsBuilder.item(Helper.itemBuilder(Material.SKULL_ITEM, friend.getName()));
                    tmp.add(settingsBuilder.build());
                }
            }

            for (SlotSettings setting : tmp) {
                builder.addItem(setting);
            }

            openInv();
        });
    }

    @Override
    protected Slot.ClickHandler switchView() {
        return (player, info) -> new RequestGUI(player).open();
    }
}