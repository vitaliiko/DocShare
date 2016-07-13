package com.geekhub.repositories;

import com.geekhub.entities.UserDirectory;
import com.geekhub.entities.UserToDirectoryRelation;

public interface UserToDirectoryRelationRepository extends EntityRepository<UserToDirectoryRelation, Long> {

    void deleteByDirectoryBesidesOwner(UserDirectory directory);
}
