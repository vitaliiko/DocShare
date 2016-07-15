package com.geekhub.dto.convertors;

import com.geekhub.dto.*;

import com.geekhub.entities.Comment;
import com.geekhub.entities.DocumentOldVersion;
import com.geekhub.entities.Event;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.RemovedDirectory;
import com.geekhub.entities.RemovedDocument;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;
import com.geekhub.entities.enums.EventStatus;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class EntityToDtoConverter {

    public static DocumentOldVersionDto convert(DocumentOldVersion oldVersion) {
        DocumentOldVersionDto oldVersionDto = new DocumentOldVersionDto();
        oldVersionDto.setId(oldVersion.getId());
        oldVersionDto.setName(oldVersion.getUserDocument().getName());
        oldVersionDto.setChangedBy(oldVersion.getModifiedBy());
        oldVersionDto.setLastModifyTime(oldVersion.getLastModifyTime());
        oldVersionDto.setSize(oldVersion.getSize());
        return oldVersionDto;
    }

    public static UserFileDto convert(UserDocument document) {
        DateFormat df = new SimpleDateFormat("MM.dd.yy");
        UserFileDto documentDto = new UserFileDto();
        documentDto.setId(document.getId());
        documentDto.setType("doc");
        documentDto.setSize(document.getSize());
        documentDto.setLastModifyTime(df.format(document.getLastModifyTime()));
        documentDto.setModifiedBy(document.getModifiedBy());
        documentDto.setName(document.getName());
        documentDto.setDescription(document.getDescription());
        documentDto.setParentDirectoryHash(document.getParentDirectoryHash());
        documentDto.setAccess(document.getDocumentAttribute().toString());
        return documentDto;
    }

    public static UserFileDto convert(UserDirectory directory) {
        UserFileDto directoryDto = new UserFileDto();
        directoryDto.setId(directory.getId());
        directoryDto.setType("dir");
        directoryDto.setName(directory.getName());
        directoryDto.setHashName(directory.getHashName());
        directoryDto.setParentDirectoryHash(directory.getParentDirectoryHash());
        directoryDto.setAccess(directory.getDocumentAttribute().toString());
        return directoryDto;
    }

    public static UserDto convert(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setLogin(user.getLogin());
        userDto.setEmail(user.getEmail());
        userDto.setCountry(user.getCountry());
        userDto.setState(user.getState());
        userDto.setCity(user.getCity());
        return userDto;
    }

    public static FriendGroupDto convert(FriendsGroup group) {
        FriendGroupDto groupDto = new FriendGroupDto();
        groupDto.setId(group.getId());
        groupDto.setName(group.getName());

        Set<UserDto> friends = new TreeSet<>();
        group.getFriends().forEach(f -> friends.add(convert(f)));
        groupDto.setFriends(friends);
        return groupDto;
    }

    public static CommentDto convert(Comment comment) {
        DateFormat df = new SimpleDateFormat("MM.dd.yy hh:mm:ss");
        CommentDto commentDto = new CommentDto();
        commentDto.setText(comment.getText());
        commentDto.setDate(df.format(comment.getDate()));
        commentDto.setSenderName(comment.getOwner().getFullName());
        return commentDto;
    }

    public static EventDto convert(Event event) {
        EventDto eventDto = new EventDto();
        eventDto.setText(event.getText());
        eventDto.setLinkText(event.getLinkText());
        eventDto.setLinkUrl(event.getLinkUrl());
        eventDto.setDate(event.getDate());
        eventDto.setStatus(event.getEventStatus() == EventStatus.UNREAD ? "New" : "");
        return eventDto;
    }

    public static RemovedFileDto convert(RemovedDocument document, String removerName) {
        RemovedFileDto removedFileDto = new RemovedFileDto();
        removedFileDto.setId(document.getId());
        removedFileDto.setName(document.getUserDocument().getName());
        removedFileDto.setRemovalDate(document.getRemovalDate());
        removedFileDto.setRemoverName(removerName);
        removedFileDto.setType("doc");
        return removedFileDto;
    }

    public static RemovedFileDto convert(RemovedDirectory directory, String removerName) {
        RemovedFileDto removedFileDto = new RemovedFileDto();
        removedFileDto.setId(directory.getId());
        removedFileDto.setName(directory.getUserDirectory().getName());
        removedFileDto.setRemovalDate(directory.getRemovalDate());
        removedFileDto.setRemoverName(removerName);
        removedFileDto.setType("dir");
        return removedFileDto;
    }

    public static Map<UserDto, List<FriendGroupDto>> convertMap(Map<User, List<FriendsGroup>> friendsMap) {
        Map<UserDto, List<FriendGroupDto>> friendsDtoMap = new TreeMap<>();
        for (User user : friendsMap.keySet()) {
            List<FriendGroupDto> userGroupDtoList =
                    friendsMap.get(user).stream().map(EntityToDtoConverter::convert).collect(Collectors.toList());
            friendsDtoMap.put(EntityToDtoConverter.convert(user), userGroupDtoList);
        }
        return friendsDtoMap;
    }
}
