package de.mert.friendssystem.guis;

import de.mert.friendssystem.Friends;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class RemoveFriendGUI extends AcceptGUI {
    public RemoveFriendGUI(Player player, OfflinePlayer friend) {
        super(player, "Remove player: " + friend.getName());

        setAcceptHandler((p, info) -> new Friends(p.getUniqueId()).removeFriend(friend.getUniqueId(), status -> {
            if (status == Friends.Status.ERROR) {
                p.closeInventory();
                p.sendMessage("§cError occurred while removing your friend §7" + friend.getName());
            } else {
                new FriendsGUI(p).open();
            }
        }));

        setDenyHandler((p, info) -> new ManageFriendGUI(p, friend).open());

        setAcceptText("§6Remove friend");
        setDenyText("§cCancel");
    }
}
