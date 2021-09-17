package com.springapp.kpgo.page;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class Index {
	
	@RequestMapping("/")
	public String index(Model model) {
		model.addAttribute("test", "it works.");
		return "index";
	}
	
}