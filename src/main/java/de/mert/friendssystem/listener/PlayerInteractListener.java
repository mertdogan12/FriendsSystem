package de.mert.friendssystem.listener;

import de.mert.friendssystem.guis.FriendsGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {
    @EventHandler
    public void onPlayerInteractListener(PlayerInteractEvent e) {
        e.setCancelled(true);

        new FriendsGUI(e.getPlayer()).handleClick(e);
    }
}