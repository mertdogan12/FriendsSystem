package de.mert.friendssystem;

import de.mert.friendssystem.commands.FriendCommand;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

/**
 * FriendsSystem
 */
public class FriendsSystem extends JavaPlugin {
    public static final String PREFIX = "§f[§6FriendsSystem§f] ";
    public static Settings settings;
    public static DataSource dataSource;

    @Override
    public void onEnable() {
        ConsoleCommandSender s = getServer().getConsoleSender();

        // Ladet die Einstellungen von der Config Datei
        settings = new Settings(this);
        s.sendMessage(PREFIX + "Settings got initialized");

        // Verbindet und initialisiert die MariaDB Datenbank
        try {
            dataSource = MariaDB.connect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            s.sendMessage(PREFIX + "Could not open the file dbsetup.sql check if the file exist");
            throw new RuntimeException(e);
        }
        getServer().getConsoleSender().sendMessage(PREFIX + "Successful connected to the database");

        // Commands
        getCommand("friend").setExecutor(new FriendCommand());
    }
}
