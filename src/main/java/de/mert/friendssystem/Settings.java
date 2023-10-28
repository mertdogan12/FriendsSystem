package de.mert.friendssystem;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Settings
 */
public class Settings {
    private final MariaDBSettings MARIADB_SETTING;

    public Settings(FriendsSystem plugin) {
        plugin.saveDefaultConfig();

        FileConfiguration config = plugin.getConfig();

        MARIADB_SETTING = new MariaDBSettings();
        MARIADB_SETTING.address = config.getString("mariadb.address");
        MARIADB_SETTING.username = config.getString("mariadb.username");
        MARIADB_SETTING.password = config.getString("mariadb.password");
        MARIADB_SETTING.database = config.getString("mariadb.database");
        MARIADB_SETTING.port = config.getString("mariadb.port");
        MARIADB_SETTING.maxConnections = config.getInt("mariadb.maxConnections");
    }

    public String getMariaDBAddress() {
        return MARIADB_SETTING.address;
    }

    public String getMariaDBUsername() {
        return MARIADB_SETTING.username;
    }

    public String getMariaDBPassword() {
        return MARIADB_SETTING.password;
    }

    public String getMariaDBDatabase() {
        return MARIADB_SETTING.database;
    }

    public String getMariaDBPort() {
        return MARIADB_SETTING.port;
    }

    public int getMariaDBMaxConnections() {
        return MARIADB_SETTING.maxConnections;
    }
}

class MariaDBSettings {
    public String address;
    public String username;
    public String password;
    public String database;
    public String port;
    public int maxConnections;
}
