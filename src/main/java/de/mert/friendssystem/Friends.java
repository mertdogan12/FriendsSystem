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
        SUCCESSFUL_REMOVED,
        SUCCESSFUL_REMOVED_REQ,
        NOT_FRIENDS,
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
            Optional<Boolean> friendRequest = friendRequestExists(friend, uuid);
            if (!friendRequest.isPresent()) {
                Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.ERROR));
                return;
            }

            if (friendRequest.get()) {
                if (addFriend(friend))
                    if (removeFriendRequest(friend, uuid).isPresent())
                        Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.SUCCESSFUL_ADDED));
                    else
                        Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.ERROR));
                else
                    Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.ERROR));

                return;
            }

            // Überprüft ob ihr eine friend req schon gesendet wurde
            Optional<Boolean> alreadySendReq = friendRequestExists(uuid, friend);
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

    public void removeFriend(UUID friend, final ChangeDataCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Optional<Integer> rmFriend = removeFriend(friend);
            if (!rmFriend.isPresent()) {
                Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.ERROR));
                return;
            }

            if (rmFriend.get() > 0) {
                Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.SUCCESSFUL_REMOVED));
                return;
            }

            Optional<Integer> rmFriendRequest = removeFriendRequest(friend, uuid);
            if (!rmFriendRequest.isPresent()) {
                Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.ERROR));
                return;
            }

            if (rmFriendRequest.get() > 0) {
                Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.SUCCESSFUL_REMOVED_REQ));
                return;
            }

            Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.NOT_FRIENDS));
        });
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
                Helper.logError("Error while getting the friends from " + uuid, e);
            }

            final Optional<UUID[]> fresult = result;
            Bukkit.getScheduler().runTask(plugin, () -> callback.onQuereDone(fresult));
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
            Helper.logError("Error while adding a friend", e);
            return false;
        }
    }

    private Optional<Integer> removeFriend(UUID friend) {
        try (Connection conn = source.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM friends WHERE player1=? AND player2=? OR player1=? AND player2=?"
        )) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, friend.toString());
            stmt.setString(3, friend.toString());
            stmt.setString(4, uuid.toString());
            int update = stmt.executeUpdate();

            return Optional.of(update);
        } catch (SQLException e) {
            Helper.logError("Error while deleting a friend request", e);
            return Optional.empty();
        }
    }

    public void getFriendRequests(final GetDataCallback<UUID[]> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Optional<UUID[]> requests = getAllFriendRequests();
            Bukkit.getScheduler().runTask(plugin, () -> callback.onQuereDone(requests));
        });
    }

    public void acceptAllRequests(final ChangeDataCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Optional<UUID[]> requests = getAllFriendRequests();
            if (!requests.isPresent()) {
                Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.ERROR));
                return;
            }

            for (UUID id : requests.get()) {
                if (!addFriend(id)) {
                    Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.ERROR));
                    return;
                }

                if (!removeFriendRequest(id, uuid).isPresent()) {
                    Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.ERROR));
                    return;
                }
            }

            Bukkit.getScheduler().runTask(plugin, () -> callback.onQueryDone(Status.SUCCESSFUL_ADDED));
        });
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
            Helper.logError("Error while adding a friend request", e);
            return false;
        }
    }

    private Optional<Integer> removeFriendRequest(UUID sender, UUID receiver) {
        try (Connection conn = source.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM requests WHERE sender=? AND receiver=?"
        )) {
            stmt.setString(1, sender.toString());
            stmt.setString(2, receiver.toString());
            int update = stmt.executeUpdate();

            return Optional.of(update);
        } catch (SQLException e) {
            Helper.logError("Error while deleting a friend request", e);
            return Optional.empty();
        }
    }

    private Optional<Boolean> friendRequestExists(UUID sender, UUID receiver) {
        try (Connection conn = source.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM requests WHERE sender=? AND receiver=?"
        )) {
            stmt.setString(1, sender.toString());
            stmt.setString(2, receiver.toString());

            ResultSet resultSet = stmt.executeQuery();
            return Optional.of(resultSet.next());
        } catch (SQLException e) {
            Helper.logError("Error while checking for a friend request", e);
            return Optional.empty();
        }
    }

    private Optional<UUID[]> getAllFriendRequests() {
        try (Connection conn = source.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM requests WHERE receiver=?"
        )) {
            LinkedList<UUID> out = new LinkedList<>();

            stmt.setString(1, uuid.toString());
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next())
                out.add(UUID.fromString(resultSet.getString(1)));

            return Optional.of(out.toArray(new UUID[0]));
        } catch (SQLException e) {
            Helper.logError("Error while getting the friend requests from " + uuid, e);
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
            Helper.logError("Error while getting a friendship", e);
            return Optional.empty();
        }
    }
}