package com.geekhub.dto.convertors;

import com.geekhub.dto.CommentDto;
import com.geekhub.dto.FriendsGroupDto;

import com.geekhub.dto.UserFileDto;
import com.geekhub.dto.UserDto;
import com.geekhub.entities.Comment;
import com.geekhub.entities.DocumentOldVersion;
import com.geekhub.dto.DocumentOldVersionDto;
import com.geekhub.entities.FriendsGroup;
import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserDocument;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.TreeSet;

public class EntityToDtoConverter {

    public static DocumentOldVersionDto convert(DocumentOldVersion oldVersion) {
        DocumentOldVersionDto oldVersionDto = new DocumentOldVersionDto();
        oldVersionDto.setId(oldVersion.getId());
        oldVersionDto.setName(oldVersion.getUserDocument().getName());
        oldVersionDto.setChangedBy(oldVersion.getChangedBy());
        oldVersionDto.setLastModifyTime(oldVersion.getUserDocument().getLastModifyTime());
        oldVersionDto.setSize(oldVersion.getUserDocument().getSize());
        return oldVersionDto;
    }

    public static UserFileDto convert(UserDocument document) {
        DateFormat df = new SimpleDateFormat("MM.dd.yy");
        UserFileDto documentDto = new UserFileDto();
        documentDto.setId(document.getId());
        documentDto.setType("doc");
        documentDto.setSize(document.getSize());
        documentDto.setLastModifyTime(df.format(document.getLastModifyTime()));
        documentDto.setName(document.getName());
        documentDto.setOwnerName(document.getOwner().toString());
        documentDto.setDescription(document.getDescription());
        documentDto.setParentDirectoryHash(document.getParentDirectoryHash());
        documentDto.setAccess(document.getDocumentAttribute().toString());
        documentDto.setReaders(document.getReaders());
        documentDto.setReadersGroups(document.getReadersGroups());
        documentDto.setEditors(document.getEditors());
        documentDto.setEditorsGroups(document.getEditorsGroups());
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
        directoryDto.setReaders(directory.getReaders());
        directoryDto.setReadersGroups(directory.getReadersGroups());
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

    public static FriendsGroupDto convert(FriendsGroup group) {
        FriendsGroupDto groupDto = new FriendsGroupDto();
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
        commentDto.setSenderName(comment.getOwner().toString());
        return commentDto;
    }
}
