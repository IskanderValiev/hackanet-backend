package com.hackanet.services;

import com.hackanet.models.user.User;

import java.util.Set;

public interface ConnectionService {
    void addConnection(User user, User userToAdd);
    void deleteConnection(User user, User userToDelete);
    void deleteConnection(User user, Long connectionId);
    Set<User> getConnections(Long userId);
    Set<User> getConnectionsSuggestions(User user);
}
