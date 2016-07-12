package com.geekhub.services.impl;

import com.geekhub.repositories.FriendsGroupRepository;
import com.geekhub.dto.CreateFriendGroupDto;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.services.EventSendingService;
import com.geekhub.services.FriendGroupService;
import com.geekhub.services.UserService;
import org.hibernate.Hibernate;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class FriendGroupServiceImpl implements FriendGroupService {

    @Inject
    private FriendsGroupRepository friendsGroupRepository;

    @Inject
    private UserService userService;

    @Inject
    private EventSendingService eventSendingService;

    @Override
    public List<FriendsGroup> getAll(String orderParameter) {
        return friendsGroupRepository.getAll(orderParameter);
    }

    @Override
    public FriendsGroup getById(Long id) {
        return friendsGroupRepository.getById(id);
    }

    @Override
    public FriendsGroup get(String propertyName, Object value) {
        return friendsGroupRepository.get(propertyName, value);
    }

    @Override
    public Long save(FriendsGroup entity) {
        return friendsGroupRepository.save(entity);
    }

    @Override
    public void update(FriendsGroup entity) {
        friendsGroupRepository.update(entity);
    }

    @Override
    public void delete(FriendsGroup entity) {
        friendsGroupRepository.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        friendsGroupRepository.deleteById(entityId);
    }

    @Override
    public FriendsGroup create(User owner, CreateFriendGroupDto groupDto) {
        FriendsGroup group = new FriendsGroup();
        group.setOwner(owner);
        group.setName(groupDto.getGroupName());
        if (groupDto.getFriends() != null) {
            group.setFriends(userService.getSetByIds(groupDto.getFriends()));
        }
        save(group);
        return group;
    }

    @Override
    public FriendsGroup update(User owner, CreateFriendGroupDto groupDto) {
        FriendsGroup group = getById(groupDto.getId());
        Set<User> membersSet = new HashSet<>(group.getFriends());
        Set<User> newMembersSet = null;
        group.setName(groupDto.getGroupName());
        if (groupDto.getFriends() != null) {
            newMembersSet = userService.getSetByIds(groupDto.getFriends());
            group.setFriends(newMembersSet);
        } else {
            group.getFriends().clear();
        }
        update(group);

        eventSendingService.sendShareEvent(owner, group, membersSet, newMembersSet);
        return group;
    }

    @Override
    public boolean addFriend(Long groupId, Long friendId) {
        FriendsGroup group = friendsGroupRepository.getById(friendId);
        User user = userService.getById(friendId);
        if (group.getFriends().add(user)) {
            friendsGroupRepository.update(group);
            return true;
        }
        return false;
    }

    @Override
    public FriendsGroup getByName(String groupName) {
        return friendsGroupRepository.get("name", groupName);
    }

    @Override
    public Set<User> getFriendsSet(FriendsGroup group) {
        Hibernate.initialize(group.getFriends());
        return group.getFriends();
    }

    @Override
    public List<FriendsGroup> getFriendsGroups(User owner, String propertyName, Object value) {
        return friendsGroupRepository.getFriendsGroups(owner, propertyName, value);
    }

    @Override
    public List<FriendsGroup> getByOwnerAndFriend(User owner, User friend) {
        return friendsGroupRepository.getByOwnerAndFriend(owner, friend);
    }

    @Override
    public List<FriendsGroup> getByFriend(User friend) {
        return friendsGroupRepository.getByFriend(friend);
    }

    @Override
    public void update(List<FriendsGroup> groups) {
        groups.forEach(this::update);
    }

    @Override
    public FriendsGroup getByOwnerAndName(User owner, String name) {
        return friendsGroupRepository.get(owner, "name", name);
    }

    @Override
    public List<FriendsGroup> getListByOwner(User owner) {
        return friendsGroupRepository.getList("owner", owner);
    }
}
