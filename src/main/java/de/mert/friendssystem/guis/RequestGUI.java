package de.mert.friendssystem.guis;

import de.mert.friendssystem.Friends;
import de.mert.friendssystem.Helper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.ipvp.canvas.slot.Slot;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class RequestGUI extends MainGUI {
    public RequestGUI(Player player) {
        super(player, Helper.itemBuilder(Material.IRON_INGOT, "Friends"), "Friend requests");
    }

    @Override
    public void open() {
        new Friends(player.getUniqueId()).getFriendRequests(result -> {
            if (!result.isPresent()) {
                player.sendMessage("§cError while opening friend requests menu");
                return;
            }

            List<String> tmp = new LinkedList<>();
            for (UUID id : result.get()) {
                OfflinePlayer friend = Bukkit.getOfflinePlayer(id);

                if (friend.isOnline())
                   builder.addItem(Helper.getPlayerHead(friend.getPlayer()));
                else
                    tmp.add(friend.getName());
            }

            for (String name : tmp) {
                builder.addItem(Helper.itemBuilder(Material.SKULL_ITEM, name));
            }

            openInv();
        });
    }

    @Override
    protected Slot.ClickHandler switchView() {
        return (player, info) -> new FriendsGUI(player).open();
    }
}
