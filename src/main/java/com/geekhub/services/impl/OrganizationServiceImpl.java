package com.geekhub.services.impl;

import com.geekhub.repositories.OrganizationRepository;
import com.geekhub.entities.Organization;
import com.geekhub.entities.User;
import java.util.List;

import com.geekhub.services.OrganizationService;
import javax.inject.Inject;
import org.springframework.stereotype.Service;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    @Inject
    private OrganizationRepository organizationRepository;

    @Override
    public List<Organization> getAll(String orderParameter) {
        return organizationRepository.getAll(orderParameter);
    }

    @Override
    public Organization getById(Long id) {
        return organizationRepository.getById(id);
    }

    @Override
    public Organization get(String propertyName, Object value) {
        return organizationRepository.get(propertyName, value);
    }

    @Override
    public Long save(Organization entity) {
        return organizationRepository.save(entity);
    }

    @Override
    public void update(Organization entity) {
        organizationRepository.update(entity);
    }

    @Override
    public void delete(Organization entity) {
        organizationRepository.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        organizationRepository.deleteById(entityId);
    }

    @Override
    public Organization getByCreatorAndName(User creator, String organizationName) {
        return organizationRepository.get(creator, "name", organizationName);
    }

    @Override
    public List<Organization> getByCreator(User creator) {
        return organizationRepository.getList("creator", creator);
    }

    @Override
    public List<Organization> getByMember(User member) {
        return organizationRepository.getByMember(member);
    }
}
