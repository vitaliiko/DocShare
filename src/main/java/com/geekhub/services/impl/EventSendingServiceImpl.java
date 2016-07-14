package com.geekhub.services.impl;

import com.geekhub.entities.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.geekhub.services.*;
import com.geekhub.services.enams.FileType;
import org.apache.commons.codec.digest.DigestUtils;
import javax.inject.Inject;

import org.springframework.stereotype.Service;

@Service
public class EventSendingServiceImpl implements EventSendingService {

    @Inject
    private EventService eventService;

    @Inject
    private UserDocumentService userDocumentService;

    @Inject
    private UserDirectoryService userDirectoryService;

    @Inject
    private FriendGroupToDirectoryRelationService friendGroupToDirectoryRelationService;

    @Inject
    private FriendGroupToDocumentRelationService friendGroupToDocumentRelationService;

    @Override
    public void sendUpdateEvent(UserDocument document, User user) {
        String eventText = "Document " + document.getName() + " has been updated by " + user.getFullName();
        String eventLinkText = "Browse";
        String eventLinkUrl = buildBrowseDocumentURL(document.getId());

        Set<User> readers = userDocumentService.getAllReadersAndEditors(document.getId());
        eventService.save(createEvent(readers, eventText, eventLinkText, eventLinkUrl, user));
    }

    @Override
    public <T, S extends EntityService<T, Long>> void sendRemoveEvent(S service, FileType fileType, String fileName,
                                                                      long fileId, User user) {

        String eventText = fileType.name() + " " + fileName + " has been removed by " + user.getFullName();

        Set<User> readers = service instanceof UserDirectoryService
                ? ((UserDirectoryService) service).getAllReadersAndEditors(fileId)
                : ((UserDocumentService) service).getAllReadersAndEditors(fileId);
        eventService.save(createEvent(readers, eventText, user));
    }

    @Override
    public <T, S extends EntityService<T, Long>> void sendRecoverEvent(S service, FileType fileType, String fileName,
                                                                       long fileId, User user) {

        String eventText = fileType.name() + " " + fileName + " has been recovered by " + user.getFullName();
        String eventLinkText = null;
        String eventLinkUrl = null;
        if (fileType == FileType.DOCUMENT) {
            eventLinkText = "Browse";
            eventLinkUrl = buildBrowseDocumentURL(fileId);
        }

        Set<User> readers = service instanceof UserDirectoryService
                ? ((UserDirectoryService) service).getAllReadersAndEditors(fileId)
                : ((UserDocumentService) service).getAllReadersAndEditors(fileId);

        eventService.save(createEvent(readers, eventText, eventLinkText, eventLinkUrl, user));
    }

    @Override
    public void sendRenameEvent(Set<User> readers, FileType fileType, String fileOldName,
                                 String fileName, long fileId, User user) {

        String eventText = "User " + user.getFullName() + " has renamed "
                + fileType.name() + " " + fileOldName + " to " + fileName;
        String eventLinkText = null;
        String eventLinkUrl = null;
        if (fileType == FileType.DOCUMENT) {
            eventLinkText = "Browse";
            eventLinkUrl = buildBrowseDocumentURL(fileId);
        }
        eventService.save(createEvent(readers, eventText, eventLinkText, eventLinkUrl, user));
    }

    @Override
    public void sendShareEvent(Set<User> readers, FileType fileType, String fileName, long fileId, User user) {
        String eventText = "User " + user.getFullName() + " has shared " + fileType.name() + " " + fileName;
        String eventLinkText = null;
        String eventLinkUrl = null;
        if (fileType == FileType.DOCUMENT) {
            eventLinkText = "Browse";
            eventLinkUrl = buildBrowseDocumentURL(fileId);
        }
        eventService.save(createEvent(readers, eventText, eventLinkText, eventLinkUrl, user));
    }

    @Override
    public void sendProhibitAccessEvent(Set<User> readers, FileType fileType, String fileName, User user) {
        String eventText = "User " + user.getFullName() + " has prohibited access to " + fileType.name() + " " + fileName;
        eventService.save(createEvent(readers, eventText, user));
    }

    @Override
    public void sendToFriendRequestEvent(User user, User friend) {
        String eventHashName = createHashName();
        String eventText = "User " + user.getFullName() + " wants to add you as a friend.";
        String eventLinkText = "Confirm";
        String eventLinkUrl = "/api/friends/" + user.getId() + "/add/" + eventHashName;

        eventService.save(createEvent(eventHashName, friend, eventText, eventLinkText, eventLinkUrl, user));
    }

    @Override
    public void sendAddToFriendEvent(User user, User friend) {
        String eventText = "User " + user.getFullName() + " confirm your request.";
        String eventLinkText = "Friends";
        String eventLinkUrl = "/api/friends/";

        eventService.save(createEvent(friend, eventText, eventLinkText, eventLinkUrl, user));
    }

    @Override
    public void sendDeleteFromFriendEvent(User user, User friend) {
        String eventText = "User " + user.getFullName() + " removed you from friends.";
        eventService.save(createEvent(friend, eventText, user));
    }

    @Override
    public void sendShareEvent(User user, FriendsGroup group, Set<User> membersSet, Set<User> newMembersSet) {
        long documentsCount = friendGroupToDocumentRelationService.getCountByFriendGroup(group);
        long directoriesCount = friendGroupToDirectoryRelationService.getCountByFriendGroup(group);
        if (documentsCount != 0 || directoriesCount != 0) {
            String filesCount = "";
            if (documentsCount != 0) {
                filesCount += documentsCount + " documents";
            }
            if (directoriesCount != 0 && documentsCount != 0) {
                filesCount += " and ";
            }
            if (directoriesCount != 0) {
                filesCount += directoriesCount + " directories";
            }
            if (newMembersSet != null) {
                newMembersSet.removeAll(membersSet);
                String eventText = "User " + user.getFullName() + " has shared " + filesCount;
                eventService.save(createEvent(newMembersSet, eventText, user));
            }
        }
    }

    private Event createEvent(String hashName, User recipient, String text,
                              String linkText, String linkUtl, User sender) {

        Event event = new Event();
        event.setText(text);
        event.setLinkText(linkText);
        event.setLinkUrl(linkUtl);
        event.setDate(Calendar.getInstance().getTime());
        event.setSenderId(sender.getId());
        event.setSenderName(sender.getFullName());
        event.setRecipient(recipient);
        event.setHashName(hashName == null ? createHashName() : hashName);
        return event;
    }

    private Event createEvent(User recipient, String text, String linkText, String linkUtl, User sender) {
        return createEvent(null, recipient, text, linkText, linkUtl, sender);
    }

    private Event createEvent(User recipient, String text, User sender) {
        return createEvent(null, recipient, text, null, null, sender);
    }

    private List<Event> createEvent(Set<User> recipients, String text, String linkText,
                                    String linkUtl, User sender) {

        List<Event> events = new ArrayList<>();
        recipients.forEach(r -> events.add(createEvent(null, r, text, linkText, linkUtl, sender)));
        return events;
    }

    private List<Event> createEvent(Set<User> recipients, String text, User sender) {
        List<Event> events = new ArrayList<>();
        recipients.forEach(r -> events.add(createEvent(null, r, text, null, null, sender)));
        return events;
    }

    private String createHashName() {
        return DigestUtils.md5Hex("" + new Date().getTime());
    }

    private String buildBrowseDocumentURL(long documentId) {
        return "/api/documents/" + documentId + "/browse";
    }
}
