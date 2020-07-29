package ru.innopolis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.innopolis.dao.UserDao;
import ru.innopolis.model.User;

import java.util.List;

@Service
public class UserOperationService {
    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public void addUsers(List<User> users) {
        for (User user : users) {
            userDao.addUserUsingSimpleJdbcInsert(user);
        }
    }
}
