package com.springapp.kpgo.pages;

import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.Autowired;

// import com.springapp.kpgo.core.*;

import com.springapp.kpgo.repositories.UserRepository;
import com.springapp.kpgo.models.User;

@Controller
public class Login {
    
    @Autowired
    UserRepository myRepo;
    
    @RequestMapping("/login")
    public String login() {
//        H2Adapter testAdapter = new H2Adapter();
//        testAdapter.test();
        test();
        return "login";
    }
    
    public void test() {
        User myUser = myRepo.findByUsername("TestName");
        if (myUser == null) {
            myUser = new User("TestName", "TestPwd");
            myRepo.save(myUser);
            System.out.println("created new user");
        }
        
        System.out.println(myUser);
    }
	
}