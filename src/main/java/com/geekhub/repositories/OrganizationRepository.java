package com.geekhub.repositories;

import com.geekhub.entities.Organization;
import com.geekhub.entities.User;

import java.util.List;

public interface OrganizationRepository extends EntityRepository<Organization, Long> {

    Organization get(User creator, String propertyName, Object value);

    List<Organization> getList(User creator, String propertyName, Object value);

    List<Organization> getByMember(User member);
}
