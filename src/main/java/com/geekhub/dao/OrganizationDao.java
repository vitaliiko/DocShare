package com.geekhub.dao;

import com.geekhub.entities.Organization;
import com.geekhub.entities.User;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrganizationDao implements EntityDao<Organization, Long> {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    private Class<Organization> clazz = Organization.class;

    @Override
    public List<Organization> getAll(String orderParameter) {
        return (List<Organization>) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .addOrder(Order.asc(orderParameter))
                .list();
    }

    @Override
    public Organization getById(Long id) {
        return (Organization) sessionFactory.getCurrentSession().get(clazz, id);
    }

    @Override
    public Organization get(String propertyName, Object value) {
        return (Organization) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    @Override
    public List<Organization> getList(String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    @Override
    public Long save(Organization entity) {
        return (Long) sessionFactory.getCurrentSession().save(entity);
    }

    @Override
    public void update(Organization entity) {
        sessionFactory.getCurrentSession().update(entity);
    }

    @Override
    public void saveOrUpdate(Organization entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
    }

    @Override
    public void delete(Organization entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        Organization user = getById(entityId);
        sessionFactory.getCurrentSession().delete(user);
    }

    public Organization get(User creator, String propertyName, Object value) {
        return (Organization) sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("creator", creator))
                .add(Restrictions.eq(propertyName, value))
                .uniqueResult();
    }

    public List<Organization> getList(User creator, String propertyName, Object value) {
        return sessionFactory.getCurrentSession()
                .createCriteria(clazz)
                .add(Restrictions.eq("creator", creator))
                .add(Restrictions.eq(propertyName, value))
                .list();
    }

    public List<Organization> getByMember(User member) {
        return sessionFactory.getCurrentSession()
                .createQuery("from Organization org where :memberUser in elements(org.members)")
                .setParameter("memberUser", member)
                .list();
    }
}
