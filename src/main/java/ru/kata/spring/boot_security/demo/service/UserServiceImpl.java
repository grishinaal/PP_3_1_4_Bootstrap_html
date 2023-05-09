package ru.kata.spring.boot_security.demo.service;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;


   @Autowired
    public UserServiceImpl(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;

   }

    private User encryptUserPassword(User user){
       user.setPassword(passwordEncoder.encode(user.getPassword()));
       return user;
    }


    /*
     3.1.4 - обновлён метод saveUser
     */
@Override
@Transactional
public void saveUser(User user) {
    if(user.getPassword() == null || user.getPassword().isEmpty()) {
        throw new IllegalArgumentException("Пароль не может быть пустым");
    }
    String encodedPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(encodedPassword);
    userDao.saveUser(user);
}

    @Override
    @Transactional
    public void updateUser(User updateUser) {
    userDao.updateUser(encryptUserPassword(updateUser));
    }




//    @Override
//    @Transactional
//    public void updateUser(User updateUser) {
//        userDao.updateUser(updateUser);
//
//    }

/*  под вопросом  метод updateUser


    @Override
    @Transactional
    public void updateUser(User updateUser) {
        if(updateUser.getPassword() == null || updateUser.getPassword().isEmpty()) {
            User existingUser = userDao.getUserById(updateUser.getId());
            updateUser.setPassword(existingUser.getPassword());
        } else {
            updateUser.setPassword(passwordEncoder.encode(updateUser.getPassword()));
        }
        userDao.updateUser(updateUser);
    }
 */
    @Override
    @Transactional
    public void removeUserById(Long id) {
        userDao.removeUserById(id);

    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = userDao.getAllUsers();
        for (User user : users) {
            Hibernate.initialize(user.getRoles());
        }
        return users;
    }

    @Override
    public User getUserById(Long id) {
        return userDao.getUserById(id);
    }


    @Override
    public User getUserByLogin(String login) {
        return userDao.findByLogin(login);
    }



    @Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userDao.findByLogin(login);
        System.out.println(user);
        if (user == null)
            throw new UsernameNotFoundException("Пользователь не найден");
        return user;
    }

}
