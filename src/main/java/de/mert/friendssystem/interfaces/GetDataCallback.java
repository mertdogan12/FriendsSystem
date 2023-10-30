package de.mert.friendssystem.interfaces;

import java.util.Optional;

public interface GetDataCallback<T> {
    void onQuereDone(Optional<T> result);
}