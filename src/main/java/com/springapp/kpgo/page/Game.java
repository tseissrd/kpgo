package com.springapp.kpgo.page;

import com.springapp.kpgo.model.User;
import com.springapp.kpgo.security.AuthorizationManager;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
public class Game {
  
  @Autowired
  private AuthorizationManager authMgr;
  
  @RequestMapping("/play")
  public String index(@RequestHeader Map<String, String> headers, HttpServletRequest request, HttpServletResponse response, Model model) {
    User user = authMgr.authorize(request.getCookies());
    if (user == null) {
      try {
        response.sendRedirect("/login");
      } catch (Exception err) {
        System.err.println(err);
      }
      return "login";
    }
    
    return "game";
  }
	
}
