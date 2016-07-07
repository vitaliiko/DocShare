package com.geekhub.services.impl;

import com.geekhub.dao.CommentDao;
import com.geekhub.entities.Comment;
import java.util.List;

import com.geekhub.services.CommentService;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    @Inject
    private CommentDao commentDao;

    @Override
    public List<Comment> getAll(String orderParameter) {
        return commentDao.getAll(orderParameter);
    }

    @Override
    public Comment getById(Long id) {
        return commentDao.getById(id);
    }

    @Override
    public Comment get(String propertyName, Object value) {
        return commentDao.get(propertyName, value);
    }

    @Override
    public Long save(Comment entity) {
        return commentDao.save(entity);
    }

    @Override
    public void update(Comment entity) {
        commentDao.update(entity);
    }

    @Override
    public void delete(Comment entity) {
        commentDao.delete(entity);
    }

    @Override
    public void deleteById(Long entityId) {
        commentDao.deleteById(entityId);
    }
}
