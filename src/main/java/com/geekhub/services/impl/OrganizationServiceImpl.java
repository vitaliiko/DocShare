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
    private OrganizationRepository repository;

    @Override
    public List<Organization> getAll(String orderParameter) {
        return repository.getAll(orderParameter);
    }

    @Override
    public Organization getById(Long id) {
        return repository.getById(id);
    }

    @Override
    public Organization get(String propertyName, Object value) {
        return repository.get(propertyName, value);
    }

    @Override
    public Long save(Organization entity) {
        return repository.save(entity);
    }

    @Override
    public void update(Organization entity) {
        repository.update(entity);
    }

    @Override
    public void delete(Organization entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        repository.deleteById(entityId);
    }

    @Override
    public Organization getByCreatorAndName(User creator, String organizationName) {
        return repository.get(creator, "name", organizationName);
    }

    @Override
    public List<Organization> getByCreator(User creator) {
        return repository.getList("creator", creator);
    }

    @Override
    public List<Organization> getByMember(User member) {
        return repository.getByMember(member);
    }
}
