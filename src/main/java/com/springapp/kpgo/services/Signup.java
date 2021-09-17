/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.services;

import org.springframework.web.bind.annotation.*;
import java.util.Map;
import com.springapp.kpgo.repository.DataRepository;
import com.springapp.kpgo.model.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Sovereign
 */
@RestController
public class Signup {
    
    @Autowired
    private DataRepository repository;
    
    @RequestMapping("/signup")
    public void signupEndpoint(@RequestBody Map<String, Object> userInfo) {
        if ((userInfo == null) || ((userInfo.get("username") == null) || (userInfo.get("password") == null)))
            return;
        String username = (String)userInfo.get("username");
        Password userPassword = new Password(username, (String)userInfo.get("password"));
        repository.save(userPassword);
        User newUser = new User(username, userPassword);
        repository.save(newUser);
    }
    
}
