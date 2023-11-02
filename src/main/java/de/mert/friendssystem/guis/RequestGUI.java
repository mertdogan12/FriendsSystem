package de.mert.friendssystem.guis;

import de.mert.friendssystem.Friends;
import de.mert.friendssystem.Helper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.ipvp.canvas.slot.ClickOptions;
import org.ipvp.canvas.slot.Slot;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class RequestGUI extends MainGUI {
    public RequestGUI(Player player) {
        super(player, "Friend requests");
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
    protected void setMenuBar(List<Slot> menuBar) {
        ClickOptions options = ClickOptions.builder()
                .allow(ClickType.LEFT, ClickType.RIGHT)
                .build();

        Slot switchView = menuBar.get(menuBar.size() - 1);
        switchView.setClickOptions(options);
        switchView.setClickHandler((player, info) -> new FriendsGUI(player).open());
        switchView.setItem(Helper.itemBuilder(Material.IRON_INGOT, "Friends"));

        Slot acceptAllReq = menuBar.get(menuBar.size() - 2);
        acceptAllReq.setClickOptions(options);
        acceptAllReq.setItem(Helper.itemBuilder(Material.EMERALD, "Accept all requests"));
        acceptAllReq.setClickHandler((player, info) -> new Friends(player.getUniqueId()).acceptAllRequests(status -> {
            switch (status) {
                case ERROR:
                    player.closeInventory();
                    player.sendMessage("§cError occurred while accepting your requests");
                    return;

                case SUCCESSFUL_ADDED:
                    new RequestGUI(player).open();
            }
        }));
    }
}
