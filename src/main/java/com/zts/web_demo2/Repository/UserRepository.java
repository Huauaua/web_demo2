package com.zts.web_demo2.Repository;

@org.springframework.stereotype.Repository
public interface UserRepository {
    String getUsers();
    String saveUser( String name);
}
