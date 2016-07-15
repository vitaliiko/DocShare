package com.geekhub.services;

import com.geekhub.dto.SharedDto;
import com.geekhub.dto.UserFileDto;
import com.geekhub.entities.*;
import com.geekhub.entities.enums.DocumentAttribute;

import java.io.IOException;
import java.util.Set;

import com.geekhub.entities.enums.DocumentStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.print.Doc;
import java.util.List;

@Service
public interface UserDocumentService extends EntityService<UserDocument, Long> {

    Set<UserDocument> getByIds(List<Long> docIds);

    List<UserDocument> getAllByOwnerId(Long ownerId);

    void moveToTrash(Long docId, Long removerId);

    void moveToTrash(Long[] docIds, Long removerId);

    void replace(Long docId, String destinationDirectoryHash);

    boolean replace(Long[] docIds, String destinationDirectoryHash, User user);

    void copy(Long docId, String destinationDirectoryHash);

    boolean copy(Long[] docIds, String destinationDirectoryHash, User user);

    Long recover(Long removedDocId);

    void recover(Long[] removedDocIds);

    UserDocument getByHashName(String hashName);

    UserDocument getByNameAndOwnerId(Long ownerId, String name);

    UserDocument getByFullNameAndOwner(User owner, String parentDirectoryHash, String name);

    UserDocument getDocumentWithComments(Long docId);

    UserDocument getDocumentWithOldVersions(Long docId);

    Set<User> getAllReadersAndEditors(Long docId);

    Set<DocumentOldVersion> getOldVersions(Long docId);

    UserDocument getWithOldVersions(Long docId);

    List<UserDocument> getAllByParentDirectoryHash(String parentDirectoryHash);

    List<UserDocument> getAllByParentDirectoryHashes(List<String> parentDirectoryHashList);

    List<UserDocument> getAllByParentDirectoryHashAndStatus(String parentDirectoryHash, DocumentStatus status);

    List<Object> getActualIdsByParentDirectoryHash(String parentDirectoryHash);

    String getLocation(UserDocument document);

    Set<UserDocument> getAllByOwnerAndAttribute(User owner, DocumentAttribute attribute);

    Set<UserDocument> searchByName(User owner, String name);

    UserDocument saveOrUpdateDocument(MultipartFile multipartFile, UserDirectory directory,
                                      String description, User user) throws IOException;

    void updateDocument(UserDocument document, User user, String description, MultipartFile multipartFile)
            throws IOException;

    void changeAbilityToComment(UserDocument document, boolean abilityToComment);

    UserDocument renameDocument(UserDocument document, String newDocName, User user);

    UserDocument shareDocument(UserDocument document, SharedDto shared, User user);

    void shareDocuments(List<UserDocument> documents, SharedDto sharedDto);

    UserDocument recoverOldVersion(DocumentOldVersion oldVersion);

    void recoverRemovedDocument(Long removedDocId, User user);

    void updateDocumentAttribute(DocumentAttribute attribute, List<Long> documentIds);

    UserFileDto findAllRelations(UserFileDto fileDto);
}
