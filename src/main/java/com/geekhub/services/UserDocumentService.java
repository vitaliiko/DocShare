package com.geekhub.services;

import com.geekhub.dto.FileAccessDto;
import com.geekhub.dto.SharedDto;
import com.geekhub.entities.*;
import com.geekhub.entities.enums.DocumentAttribute;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import com.geekhub.entities.enums.DocumentStatus;
import com.geekhub.utils.DirectoryWithRelations;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface UserDocumentService extends EntityService<UserDocument, Long> {

    Set<UserDocument> getAllByIds(Collection<Long> docIds);

    Set<UserDocument> getAllByIds(Long[] docIds);

    List<UserDocument> getAllByOwnerId(Long ownerId);

    void moveToTrash(Long docId, Long removerId);

    void moveToTrash(Long[] docIds, Long removerId);

    void replace(Set<UserDocument> documents, String destinationDirectoryHash, User user);

    void copy(Collection<UserDocument> documents, String destinationDirectoryHash, User user);

    void copy(Collection<UserDocument> documents, DirectoryWithRelations destinationDirectory);

    UserDocument getByHashName(String hashName);

    UserDocument getByNameAndOwnerId(Long ownerId, String name);

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

    UserDocument saveOrUpdateDocument(MultipartFile multipartFile, String parentDirHashName, User user) throws IOException;

    void updateDocument(UserDocument document, User user, MultipartFile multipartFile) throws IOException;

    void changeAbilityToComment(Long documentId, boolean abilityToComment);

    UserDocument renameDocument(UserDocument document, String newDocName, User user);

    UserDocument shareDocument(UserDocument document, SharedDto shared, User user);

    void shareDocuments(List<UserDocument> documents, SharedDto sharedDto);

    UserDocument recoverOldVersion(DocumentOldVersion oldVersion);

    void recoverRemovedDocument(Long removedDocId, User user);

    void updateDocumentAttribute(DocumentAttribute attribute, List<Long> documentIds);

    FileAccessDto findAllRelations(Long documentId);

    boolean isDocumentNameValid(String parentDirectoryHash, String docName, User owner);

    List<String> getSimilarDocumentNamesInDirectory(String directoryHash, Set<UserDocument> documents);

    void createRelations(List<UserDocument> documents, DirectoryWithRelations parentDirectory);
}
