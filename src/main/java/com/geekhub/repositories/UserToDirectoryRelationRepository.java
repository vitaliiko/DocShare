package com.geekhub.repositories;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserToDirectoryRelation;
import com.geekhub.entities.enums.FileRelationType;

import java.util.List;

public interface UserToDirectoryRelationRepository extends EntityRepository<UserToDirectoryRelation, Long> {

    void deleteAllBesidesOwnerByDirectory(UserDirectory directory);

    List<UserDirectory> getAllAccessibleDirectories(User user);

    List<User> getAllUsersByDirectoryIdAndRelation(UserDirectory directory, FileRelationType relationType);

    UserToDirectoryRelation getByDirectoryAndUser(UserDirectory directory, User user);

    Long getDirectoriesCountByOwnerAndDirectoryIds(User owner, List<Long> idList);

    User getDirectoryOwner(UserDirectory directory);

    List<FileRelationType> getAllRelationsByDirectoriesAndUser(List<UserDirectory> directories, User user);

    List<UserToDirectoryRelation> getAllAccessibleInRoot(User user, List<String> directoryHashes);
}
