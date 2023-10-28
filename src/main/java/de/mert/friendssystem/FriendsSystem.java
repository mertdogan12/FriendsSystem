package de.mert.friendssystem;

import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;

/**
 * FriendsSystem
 */
public class FriendsSystem extends JavaPlugin {
    public static final String PREFIX = "§f[§6FriendsSystem§f] ";
    public static Settings settings;
    public static DataSource dataSource;

    @Override
    public void onEnable() {
        // Ladet die Einstellungen von der Config Datei
        settings = new Settings(this);
        getServer().getConsoleSender().sendMessage(PREFIX + "Settings got initialized");

        // Verbindet sich mit der MariaDB Datenbank
        dataSource = new MariaDB().connect();
    }
}
