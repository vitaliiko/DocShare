package com.geekhub.service;

import com.geekhub.entity.RemovedDirectory;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface RemovedDirectoryService extends EntityService<RemovedDirectory, Long> {

    List<RemovedDirectory> getAllByOwnerId(Long ownerId);
}
