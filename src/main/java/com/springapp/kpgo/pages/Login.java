package com.springapp.kpgo.pages;

import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.*;

@Controller
public class Login {
	
	@RequestMapping("/login")
	public String login() {
		return "login";
	}
	
}