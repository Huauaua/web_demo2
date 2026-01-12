package com.zts.web_demo2.Repository;

import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {
    @Override
    public String getUsers() {
        return "im users";
    }

    @Override
    public String saveUser(String name) {
        return "Users is saved";
    }
}
