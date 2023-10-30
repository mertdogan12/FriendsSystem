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
}
