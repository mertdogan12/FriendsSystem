package de.mert.friendssystem.guis;

import de.mert.friendssystem.Friends;
import de.mert.friendssystem.Helper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.slot.Slot;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;


public class FriendsGUI extends MainGUI {
    public FriendsGUI(Player player) {
        super(player, "Friends", Helper.itemBuilder(Material.GOLD_ORE, "Friend requests"));
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

            List<String> tmp = new LinkedList<>();
            for (UUID id : result.get()) {
                OfflinePlayer friend = Bukkit.getOfflinePlayer(id);

                if (friend.isOnline())
                    for (int i = 0; i < 10; i++) {
                        builder.addItem(Helper.getPlayerHead(friend.getPlayer()));
                    }
                else
                    tmp.add(friend.getName());
            }

            for (String name : tmp) {
                builder.addItem(Helper.itemBuilder(Material.SKULL_ITEM, name));
            }

            for (int i = 0; i < 50; i++) {
                builder.addItem(Helper.itemBuilder(Material.SKULL_ITEM, "Placeholder"));
            }

            openInv();
        });
    }

    @Override
    protected Slot.ClickHandler switchView() {
        return (player, info) -> new RequestGUI(player).open();
    }
}