package de.mert.friendssystem.interfaces;

import de.mert.friendssystem.Friends;

public interface ChangeDataCallback {
    void onQueryDone(Friends.Status status);
}