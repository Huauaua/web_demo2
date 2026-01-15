package com.zts.web_demo2.Controller;

import com.zts.web_demo2.UserService.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

}
