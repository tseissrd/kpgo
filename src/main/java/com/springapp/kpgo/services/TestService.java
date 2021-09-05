package com.springapp.kpgo.services;

import org.springframework.web.bind.annotation.*;

@RestController
public class TestService {

	@GetMapping("/test")
	public @ResponseBody String testSvc() {
		return "Test success";
	}

}
