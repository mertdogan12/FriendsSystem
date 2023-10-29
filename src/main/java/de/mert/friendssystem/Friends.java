package de.mert.friendssystem;

import de.mert.friendssystem.interfaces.ChangeDataCallback;
import org.bukkit.Bukkit;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Friends
 */
public class Friends {
    private final UUID uuid;
    private final DataSource source = FriendsSystem.dataSource;
    private final FriendsSystem plugin = FriendsSystem.getPlugin(FriendsSystem.class);

    public Friends(UUID uuid) {
        this.uuid = uuid;
    }

    // Erstellt Table für den Spieler (falls es den Table noch nicht gibt) und fügt den Freund hinzu
    public void addFriend(UUID friend, final ChangeDataCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean success = false;

            try (Connection conn = source.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO friends(player1, player2) VALUES(?, ?)"
            )) {
                stmt.setString(1, uuid.toString());
                stmt.setString(2, friend.toString());
                stmt.execute();

                success = true;
            } catch (SQLException e) {
                MariaDB.logSQLError("Error while adding a friend", e);
            }

            final boolean fsuccess = success;
            Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(fsuccess));
        });
    }

    public void removeFriend() {

    }
}
