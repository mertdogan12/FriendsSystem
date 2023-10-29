package de.mert.friendssystem;

import javax.sql.DataSource;
import java.util.UUID;

/**
 * Friends
 */
public class Friends {
    private UUID uuid;
    private Settings settings;
    private DataSource source;
    public Friends(UUID uuid) {
        this.uuid = uuid;

        this.settings = FriendsSystem.settings;
        this.source = FriendsSystem.dataSource;
    }

    // Erstellt Table für den Spieler (falls es den Table noch nicht gibt) und fügt den Freund hinzu
    public boolean addFriend(UUID friend) {

        return true;
    }

    public void removeFriend() {

    }
}
