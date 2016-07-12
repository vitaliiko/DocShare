package com.geekhub.services.impl;

import com.geekhub.repositories.UserRepository;
import com.geekhub.dto.SearchDto;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;

import com.geekhub.entities.UserDocument;
import com.geekhub.services.EventSendingService;
import com.geekhub.services.FriendGroupService;
import com.geekhub.services.UserDocumentService;
import com.geekhub.services.UserService;
import com.geekhub.utils.UserFileUtil;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Inject
    private UserRepository repository;

    @Inject
    private FriendGroupService friendGroupService;

    @Inject
    private EventSendingService eventSendingService;

    @Inject
    private UserDocumentService userDocumentService;

    @Override
    public List<User> getAll(String orderParameter) {
        return repository.getAll(orderParameter);
    }

    @Override
    public User getById(Long id) {
        return repository.getById(id);
    }

    @Override
    public User get(String propertyName, Object value) {
        return repository.get(propertyName, value);
    }

    @Override
    public Long save(User entity) {
        return repository.save(entity);
    }

    @Override
    public void update(User entity) {
        repository.update(entity);
    }

    @Override
    public void delete(User entity) {
        removeFromFriends(entity);
        List<String> filesHashNames =
                userDocumentService.getAllByOwner(entity).stream().map(UserDocument::getName).collect(Collectors.toList());
        repository.delete(entity);
        UserFileUtil.removeUserFiles(filesHashNames);
    }

    @Override
    public void deleteById(Long entityId) {
        repository.deleteById(entityId);
    }

    @Override
    public User getByLogin(String login) {
        return repository.get("login", login);
    }

    @Override
    public Set<User> getFriends(Long userId) {
        User user = repository.getById(userId);
        Hibernate.initialize(user.getFriends());
        return user.getFriends();
    }

    @Override
    public FriendsGroup getFriendsGroupByName(Long ownerId, String groupName) {
        User owner = repository.getById(ownerId);
        return friendGroupService.getFriendsGroups(owner, "name", groupName).get(0);
    }

    @Override
    public List<FriendsGroup> getAllFriendsGroups(Long ownerId) {
        User owner = repository.getById(ownerId);
        return owner.getFriendsGroups().stream().collect(Collectors.toList());
    }

    @Override
    public List<User> getAllFriends(Long userId) {
        User owner = repository.getById(userId);
        return owner.getFriends().stream().collect(Collectors.toList());
    }

    @Override
    public List<FriendsGroup> getGroupsByOwnerAndFriend(Long ownerId, User friend) {
        User owner = repository.getById(ownerId);
        return friendGroupService.getByOwnerAndFriend(owner, friend);
    }

    @Override
    public void addFriendsGroup(Long ownerId, FriendsGroup group) {
        User user = repository.getById(ownerId);
        if (user.getFriendsGroups().stream().noneMatch(fg -> fg.getName().equals(group.getName()))) {
            user.getFriendsGroups().add(group);
        } else {
            throw new HibernateException("Friends Group with such name already exist");
        }
        repository.update(user);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = repository.getById(userId);
        User friend = repository.getById(friendId);
        user.getFriends().add(friend);
        friend.getFriends().add(user);
        repository.update(user);
        repository.update(friend);
        eventSendingService.sendAddToFriendEvent(user, friend);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User user = repository.getById(userId);
        User friend = repository.getById(friendId);

        deleteFriend(user, friend);
        deleteFriend(friend, user);
        eventSendingService.sendDeleteFromFriendEvent(user, friend);
    }

    private void deleteFriend(User user, User friend) {
        user.getFriends().remove(friend);
        repository.update(user);
        List<FriendsGroup> groups = friendGroupService.getByOwnerAndFriend(user, friend);
        groups.forEach(g -> {
            g.getFriends().remove(friend);
            friendGroupService.update(g);
        });
    }

    @Override
    public boolean areFriends(Long userId, User friend) {
        Set<User> friends = repository.getById(userId).getFriends();
        return friends.contains(friend);
    }

    @Override
    public Map<User, List<FriendsGroup>> getFriendsGroupsMap(Long ownerId) {
        Set<User> friends = repository.getById(ownerId).getFriends();
        Map<User, List<FriendsGroup>> friendsGroupsMap = new HashMap<>();
        friends.forEach(friend -> friendsGroupsMap.put(friend, getGroupsByOwnerAndFriend(ownerId, friend)));
        return friendsGroupsMap;
    }

    @Override
    public void removeFromFriends(User friend) {
        List<User> users = repository.getByFriend(friend);
        users.forEach(u -> u.getFriends().remove(friend));
        update(users);

        List<FriendsGroup> groups = friendGroupService.getByFriend(friend);
        groups.forEach(g -> g.getFriends().remove(friend));
        friendGroupService.update(groups);
    }

    @Override
    public void update(List<User> users) {
        users.forEach(this::update);
    }

    @Override
    public Set<User> getSetByIds(Long[] usersIds) {
        Set<User> users = new HashSet<>();
        Arrays.stream(usersIds).forEach(id -> users.add(getById(id)));
        return users;
    }

    @Override
    public Set<User> searchByName(String name) {
        String[] names = name.split(" ");
        Set<User> users = new TreeSet<>();
        Arrays.stream(names)
                .filter(n -> !n.isEmpty())
                .forEach(n -> users.addAll(repository.search("firstName", n)));
        Arrays.stream(names)
                .filter(n -> !n.isEmpty())
                .forEach(n -> users.addAll(repository.search("lastName", n)));
        return users;
    }

    @Override
    public Set<User> search(SearchDto searchDto) {
        Map<String, String> searchingMap = searchDto.toMap();
        String[] names = searchDto.getName().split(" ");
        Set<User> users = new TreeSet<>();
        Arrays.stream(names)
                .filter(n -> !n.isEmpty())
                .forEach(n -> users.addAll(repository.search("firstName", n, searchingMap)));
        Arrays.stream(names)
                .filter(n -> !n.isEmpty())
                .forEach(n -> users.addAll(repository.search("lastName", n, searchingMap)));
        return users;
    }
}
