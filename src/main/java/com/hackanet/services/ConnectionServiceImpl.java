package com.hackanet.services;

import com.hackanet.models.user.User;
import com.hackanet.repositories.user.UserRepository;
import com.hackanet.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Iskander Valiev
 * created by isko
 * on 1/15/20
 */
@Service
public class ConnectionServiceImpl implements ConnectionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConnectionInvitationService connectionInvitationService;

    @Autowired
    private UserService userService;

    @Autowired
    private EntityManager entityManager;

    @Override
    @Caching(evict = {
            @CacheEvict(key = "#user.id", value = "connections-suggestions"),
            @CacheEvict(key = "#userToAdd.id", value = "connections-suggestions")
    }
    )
    public void addConnection(User user, User userToAdd) {
        Set<User> usersToSave = new HashSet<>();
        Set<User> connections = user.getConnections();
        add(connections, usersToSave, user, userToAdd);

        Set<User> userToAddConnections = userToAdd.getConnections();
        add(userToAddConnections, usersToSave, userToAdd, user);
        userRepository.saveAll(usersToSave);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "#user.id", value = "connections-suggestions"),
            @CacheEvict(key = "#userToDelete.id", value = "connections-suggestions")
    }
    )
    public void deleteConnection(User user, User userToDelete) {
        Set<User> usersToSave = new HashSet<>();
        Set<User> connections = user.getConnections();
        delete(connections, usersToSave, user, userToDelete);

        Set<User> userToDeleteConnections = userToDelete.getConnections();
        delete(userToDeleteConnections, usersToSave, userToDelete, user);
        connectionInvitationService.delete(user.getId(), userToDelete.getId());
        userRepository.saveAll(usersToSave);
    }

    @Override
    public void deleteConnection(User user, Long connectionId) {
        User connectionUser = userService.get(connectionId);
        user = userService.get(user.getId());
        deleteConnection(user, connectionUser);
    }

    @Override
    public Set<User> getConnections(Long userId) {
        User user = userService.get(userId);
        return user.getConnections();
    }

    @Override
    @Cacheable(key = "#user.id", value = "connections-suggestions")
    @SuppressWarnings("unchecked")
    public Set<User> getConnectionsSuggestions(User user) {
        String query = "select us.* from users us where us.id in (select cn.connection_id from connections cn where cn.user_id in (select c.connection_id from connections c where c.user_id = :id))";
        Query nativeQuery = entityManager
                .createNativeQuery(query, User.class)
                .setParameter("id", user.getId());
        List<User> resultList = nativeQuery.getResultList();
        return resultList.stream().filter(u -> !u.getId().equals(user.getId())).collect(Collectors.toSet());
    }

    private void add(Set<User> connections, Set<User> usersToSave, User user, User userToAdd) {
        if (!connections.contains(userToAdd)) {
            connections.add(userToAdd);
            user.setConnections(connections);
            usersToSave.add(user);
        }
    }

    private void delete(Set<User> connections, Set<User> usersToSave, User user, User userToDelete) {
        if (connections.contains(userToDelete)) {
            connections.remove(userToDelete);
            user.setConnections(connections);
            usersToSave.add(user);
        }
    }
}
