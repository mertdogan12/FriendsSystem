package de.mert.friendssystem;

import org.bukkit.Bukkit;

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
    public static ItemStack getIconHead(String texture, String name) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        ItemMeta meta = item.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:[{Value:\"%s\"}]}}}", texture).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));

        Field profileField;

        try {
            profileField = meta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            logError("Error while getting a icon head", e);
            return null;
        }

        profileField.setAccessible(true);

        try {
            profileField.set(meta, profileField);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            logError("Error while getting a icon head", e);
            return null;
        }

        meta.setDisplayName(name);

        item.setItemMeta(meta);
        return item;
    }
}
