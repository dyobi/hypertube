package com.hypertube.controller;

import com.hypertube.model.Response;
import com.hypertube.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/token")
    public Response getUserByToken(@RequestParam String token) {
        return userService.getUserByToken(token);
    }

    @GetMapping("/{userName}")
    public Response getUserByUserName(@RequestParam String token, @PathVariable("userName") String userName) {
        return userService.getUserByUserName(token, userName);
    }

    @PutMapping
    public Response putUser(@RequestBody String token, @RequestBody String userName, @RequestBody String password,
                            @RequestBody String email, @RequestBody String firstName, @RequestBody String lastName) {
        return userService.putUser(token, userName, password, email, firstName, lastName);
    }

    @DeleteMapping
    public Response deleteUser(@RequestParam String token) {
        return userService.deleteUser(token);
    }

}
