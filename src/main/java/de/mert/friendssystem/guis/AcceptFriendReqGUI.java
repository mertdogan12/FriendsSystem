package de.mert.friendssystem.guis;

import de.mert.friendssystem.Friends;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class AcceptFriendReqGUI extends AcceptGUI {
    public AcceptFriendReqGUI(Player player, OfflinePlayer friend) {
        super(player, "Friend request: " + friend.getName());

        setAcceptHandler((p, info) -> new Friends(p.getUniqueId()).addFriend(friend.getUniqueId(), status -> {
            if (status == Friends.Status.ERROR) {
                p.closeInventory();
                p.sendMessage("§cError occurred while adding your friend §7" + friend.getName());
            } else {
                new RequestGUI(p).open();

                if (friend.isOnline())
                    friend.getPlayer().sendMessage("§7" + p.getName() + " §6accepted you friend request");
            }
        }));

        setDenyHandler((p, info) -> new Friends(p.getUniqueId()).removeFriend(friend.getUniqueId(), status -> {
            if (status == Friends.Status.ERROR) {
                p.closeInventory();
                p.sendMessage("§cError occurred while removing your friend §7" + friend.getUniqueId());
            } else
                new RequestGUI(p).open();
        }));
    }
}
