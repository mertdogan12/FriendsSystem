package de.mert.friendssystem;

import de.mert.friendssystem.interfaces.ChangeDataCallback;
import de.mert.friendssystem.interfaces.GetDataCallback;
import org.bukkit.Bukkit;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Optional;
import java.util.UUID;

/**
 * Friends
 */
public class Friends {
    private final UUID uuid;
    private final DataSource source = FriendsSystem.dataSource;
    private final FriendsSystem plugin = FriendsSystem.getPlugin(FriendsSystem.class);

    public enum Status {
        ERROR,
        ALREADY_FRIENDS,
        SUCCESSFUL_ADDED,
        ALREADY_SEND_REQUEST,
        SUCCESSFUL_SEND,
    }

    public Friends(UUID uuid) {
        this.uuid = uuid;
    }

    public void addFriend(UUID friend, final ChangeDataCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            // Überprüft ob ihr schon freunde seit
            Optional<Boolean> isAlreadyAdded = isFriendWith(friend);
            if (!isAlreadyAdded.isPresent()) {
                Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.ERROR));
                return;
            }

            if (isAlreadyAdded.get()) {
                Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.ALREADY_FRIENDS));
                return;
            }

            // wenn req vorhanden ist wird friend geadded
            Optional<Boolean> friendRequest = gotFriendRequest(friend, uuid);
            if (!friendRequest.isPresent()) {
                Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.ERROR));
                return;
            }

            if (friendRequest.get()) {
                if (addFriend(friend))
                    if (removeFriendRequest(friend, uuid))
                        Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.SUCCESSFUL_ADDED));
                    else
                        Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.ERROR));
                else
                    Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.ERROR));

                return;
            }

            // Überprüft ob ihr eine friend req schon gesendet wurde
            Optional<Boolean> alreadySendReq = gotFriendRequest(uuid, friend);
            if (!alreadySendReq.isPresent()) {
                Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.ERROR));
                return;
            }

            if (alreadySendReq.get()) {
                Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.ALREADY_SEND_REQUEST));
                return;
            }

            // Sendet friend req
            if (sendFriendRequest(friend))
                Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.SUCCESSFUL_SEND));
            else
                Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.ERROR));
        });
    }

    private boolean addFriend(UUID friend) {
        try (Connection conn = source.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO friends(player1, player2) VALUES(?, ?)"
        )) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, friend.toString());
            stmt.execute();

            return true;
        } catch (SQLException e) {
            MariaDB.logSQLError("Error while adding a friend", e);
            return false;
        }
    }

    private boolean sendFriendRequest(UUID friend) {
        try (Connection conn = source.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO requests(sender, receiver) VALUES(?, ?)"
        )) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, friend.toString());
            stmt.execute();

            return true;
        } catch (SQLException e) {
            MariaDB.logSQLError("Error while adding a friend request", e);
            return false;
        }
    }

    private boolean removeFriendRequest(UUID sender, UUID receiver) {
        try (Connection conn = source.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM requests WHERE sender=? AND receiver=?"
        )) {
            stmt.setString(1, sender.toString());
            stmt.setString(2, receiver.toString());
            stmt.execute();

            return true;
        } catch (SQLException e) {
            MariaDB.logSQLError("Error while deleting a friend request", e);
            return false;
        }
    }

    private Optional<Boolean> gotFriendRequest(UUID sender, UUID receiver) {
        try (Connection conn = source.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM requests WHERE sender=? AND receiver=?"
        )) {
            stmt.setString(1, sender.toString());
            stmt.setString(2, receiver.toString());

            ResultSet resultSet = stmt.executeQuery();
            return Optional.of(resultSet.next());
        } catch (SQLException e) {
            MariaDB.logSQLError("Error while checking for a friend request", e);
            return Optional.empty();
        }
    }

    private Optional<Boolean> isFriendWith(UUID player) {
        try (Connection conn = source.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM friends WHERE player1=? AND player2=? OR player1=? AND player2=?"
        )) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, player.toString());
            stmt.setString(3, player.toString());
            stmt.setString(4, uuid.toString());

            ResultSet resultSet = stmt.executeQuery();
            return Optional.of(resultSet.next());
        } catch (SQLException e) {
            MariaDB.logSQLError("Error while getting a friendship", e);
            return Optional.empty();
        }
    }

    public void getFriends(final GetDataCallback<UUID[]> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Optional<UUID[]> result = Optional.empty();

            try (Connection conn = source.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM friends WHERE player1=? OR player2=?"
            )) {
                LinkedList<UUID> out = new LinkedList<>();

                stmt.setString(1, uuid.toString());
                stmt.setString(2, uuid.toString());
                ResultSet resultSet = stmt.executeQuery();
                while (resultSet.next()) {
                    String player1 = resultSet.getString(1);
                    String player2 = resultSet.getString(2);

                    // Überprüft welcher von den beiden Spielern der Freund ist und fügt nur diesen zu Liste hinzu
                    out.add((player1.equals(uuid.toString())) ? UUID.fromString(player2) : UUID.fromString(player1));
                }

                result = Optional.of(out.toArray(new UUID[0]));
            } catch (SQLException e) {
                MariaDB.logSQLError("Error while getting the friends from " + uuid, e);
            }

            final Optional<UUID[]> fresult = result;
            Bukkit.getScheduler().runTask(plugin, () -> callback.onQuereDone(fresult));
        });
    }
}