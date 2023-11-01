package de.mert.friendssystem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class Helper {
    public static String uuidArrayToPlayerNames(UUID[] uuids) {
        StringBuilder builder = new StringBuilder();

        if (uuids.length == 0)
            return null;

        for (int i = 0; i < uuids.length; i++) {
            if (i == uuids.length - 1)
                continue;

            String name = Bukkit.getOfflinePlayer(uuids[i]).getName();
            builder.append(name).append(", ");
        }
        builder.append(Bukkit.getOfflinePlayer(uuids[uuids.length - 1]).getName());

        return builder.toString();
    }

    public static ItemStack itemBuilder(Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack getPlayerHead(Player player) {
        ItemStack item  = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setDisplayName(player.getName());
        skull.setOwner(player.getName());
        item.setItemMeta(skull);

        return item;
    }

    public static void logError(String message, Exception error) {
        FriendsSystem plugin = FriendsSystem.getPlugin(FriendsSystem.class);

        plugin.getServer().getConsoleSender().sendMessage(FriendsSystem.PREFIX + message + ": " + error);
    }
}
