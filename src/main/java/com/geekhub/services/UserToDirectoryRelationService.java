package com.geekhub.services;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserToDirectoryRelation;
import com.geekhub.entities.enums.FileRelationType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface UserToDirectoryRelationService extends EntityService<UserToDirectoryRelation, Long> {

    List<UserToDirectoryRelation> create(UserDirectory directory, List<User> users, FileRelationType relationType);

    UserToDirectoryRelation create(UserDirectory directory, User user, FileRelationType relationType);

    void deleteByDirectoryBesidesOwner(UserDirectory directory);

    List<UserToDirectoryRelation> getAllByDirectory(UserDirectory directory);

    Set<UserDirectory> getAllAccessibleDirectories(User user);
}
