package de.mert.friendssystem.listener;

import de.mert.friendssystem.Friends;
import de.mert.friendssystem.Helper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoinListener(PlayerJoinEvent e) {
        e.setJoinMessage(null);

        Player p = e.getPlayer();

        Friends friends = new Friends(p.getUniqueId());
        friends.getFriends(result -> {
            if (!result.isPresent()) {
                return;
            }

            for (UUID id : result.get()) {
                Player friend = Bukkit.getPlayer(id);

                if (friend != null)
                    friend.sendMessage("§7" + p.getName() + " §fjoined the server");
            }
        });

        p.getInventory().setItem(0, Helper.itemBuilder(Material.DIAMOND, "Friends"));
    }
}
