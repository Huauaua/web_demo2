package com.zts.web_demo2.UserService;

import com.zts.web_demo2.Repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public String getUsers(){
        return userRepository.getUsers();
    }
    public String saveUsers(String username){
        return userRepository.saveUser(username);
    }
}
