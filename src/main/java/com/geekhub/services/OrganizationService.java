package com.geekhub.services;

import com.geekhub.entities.Organization;
import com.geekhub.entities.User;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface OrganizationService extends EntityService<Organization, Long> {

    Organization getByCreatorAndName(User creator, String organizationName);

    List<Organization> getByCreator(User creator);

    List<Organization> getByMember(User member);
}
