package com.geekhub.services;

import com.geekhub.entities.RemovedDirectory;
import com.geekhub.entities.UserDirectory;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public interface RemovedDirectoryService extends EntityService<RemovedDirectory, Long> {

    Set<RemovedDirectory> getAllByOwnerId(Long ownerId);

    RemovedDirectory getByUserDirectory(UserDirectory directory);
}
