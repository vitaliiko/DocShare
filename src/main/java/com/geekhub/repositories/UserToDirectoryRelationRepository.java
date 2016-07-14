package com.geekhub.repositories;

import com.geekhub.entities.User;
import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserToDirectoryRelation;

import java.util.List;
import java.util.Set;

public interface UserToDirectoryRelationRepository extends EntityRepository<UserToDirectoryRelation, Long> {

    void deleteByDirectoryBesidesOwner(UserDirectory directory);

    List<UserDirectory> getAllAccessibleDirectories(User user);
}
