package com.springapp.kpgo.page;

import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.*;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

@Controller
public class Login {

  @RequestMapping("/login")
  public String login(@RequestHeader Map<String, String> headers, HttpServletResponse response) {
    if (headers.get("authorization") != null) {
      try {
        response.sendRedirect("/authorize");
      } catch (Exception err) {
        System.err.println(err);
        return "login";
      }
    }
    return "login";
  }
  
}