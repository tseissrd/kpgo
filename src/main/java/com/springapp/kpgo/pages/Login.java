package com.springapp.kpgo.pages;

import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.*;

import com.springapp.kpgo.core.*;

@Controller
public class Login {
    
    @RequestMapping("/login")
    public String login() {
        H2Adapter testAdapter = new H2Adapter();
        testAdapter.test();
        return "login";
    }
	
}