package com.geekhub.services.impl;

import com.geekhub.dao.OrganizationDao;
import com.geekhub.entities.Organization;
import com.geekhub.entities.User;
import java.util.List;

import com.geekhub.services.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    private OrganizationDao organizationDao;

    @Override
    public List<Organization> getAll(String orderParameter) {
        return organizationDao.getAll(orderParameter);
    }

    @Override
    public Organization getById(Long id) {
        return organizationDao.getById(id);
    }

    @Override
    public Organization get(String propertyName, Object value) {
        return organizationDao.get(propertyName, value);
    }

    @Override
    public Long save(Organization entity) {
        return organizationDao.save(entity);
    }

    @Override
    public void update(Organization entity) {
        organizationDao.update(entity);
    }

    @Override
    public void delete(Organization entity) {
        organizationDao.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        organizationDao.deleteById(entityId);
    }

    @Override
    public Organization getByCreatorAndName(User creator, String organizationName) {
        return organizationDao.get(creator, "name", organizationName);
    }

    @Override
    public List<Organization> getByCreator(User creator) {
        return organizationDao.getList("creator", creator);
    }

    @Override
    public List<Organization> getByMember(User member) {
        return organizationDao.getByMember(member);
    }
}
