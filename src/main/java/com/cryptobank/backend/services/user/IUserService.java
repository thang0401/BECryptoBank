package com.cryptobank.backend.services.user;

import com.cryptobank.backend.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IUserService {

    User getUserId(String id);
    User getUserEmail(String email);
    List<User> getUserName(String name);

    List<User> Users();
    Page<User> getAllUsers(int page, int size);
    

    User createUser(User User);
    User updateUser(String id, User user);
    void deleteUser(String id);

}
