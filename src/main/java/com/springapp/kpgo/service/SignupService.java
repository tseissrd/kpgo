/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.service;

import org.springframework.web.bind.annotation.*;
import java.util.Map;
import com.springapp.kpgo.repository.DataRepository;
import com.springapp.kpgo.model.*;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Sovereign
 */
@RestController
public class SignupService {
    
    @Autowired
    private DataRepository repository;
    
    @RequestMapping("/signup")
    public @ResponseBody String signupEndpoint(@RequestBody Map<String, Object> userInfo, HttpServletResponse response) {
      try {
        if ((userInfo == null) || ((userInfo.get("username") == null) || (userInfo.get("password") == null)))
            return "fields are not set";
        String username = (String)userInfo.get("username");
        if (repository.users().findByUsername(username) != null) {
          System.err.println("user already exists: " + username);
          response.setStatus(HttpStatus.CONFLICT.value());
          return "user already exists";
        }
        Password userPassword = new Password(username, (String)userInfo.get("password"));
        repository.save(userPassword);
        User newUser = new User(username, userPassword);
        repository.save(newUser);
        return "ok";
      } catch (Exception err) {
        System.err.println(err);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return err.toString();
      }
    }
    
}
