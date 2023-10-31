package de.mert.friendssystem.listener;

import de.mert.friendssystem.Friends;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Arrays;
import java.util.UUID;

public class ChatListener implements Listener {
    @EventHandler
    public void onChatListener(final AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();

        e.setCancelled(true);

        new Friends(p.getUniqueId()).getFriends(result -> {
            UUID [] data = result.orElseGet(() -> new UUID[0]);

            Bukkit.getOnlinePlayers().forEach(player -> {
                if (Arrays.asList(data).contains(player.getUniqueId()))
                    player.sendMessage("[§6Friend§f] " + player.getName() + ": " + e.getMessage());
                else
                    player.sendMessage(player.getName() + ": " + e.getMessage());
            });
        });
    }
}
