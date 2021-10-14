package com.springapp.kpgo.service;

import com.springapp.kpgo.model.Password;
import com.springapp.kpgo.model.Session;
import com.springapp.kpgo.model.User;
import com.springapp.kpgo.security.AuthorizationManager;
import java.util.Base64;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginService {
  
  @Autowired
  private AuthorizationManager authMgr;
  
  public static void redirectToLogin(HttpServletResponse response) {
    try {
      response.sendRedirect("/");
    } catch (Exception err) {
      throw new Error(err);
    }
  }
	
  @RequestMapping("/authorize")
  public @ResponseBody String authorize(@RequestHeader Map<String, String> headers, HttpServletResponse response) {
    String authHeader = headers.get("authorization");
    if (authHeader == null) {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      return "";
    } else {
      System.out.println(authHeader);
      String[] authHeaderParts = authHeader.split(" ");
      String authMethod = authHeaderParts[0];
      String decodedAuthHeader;
      System.out.println(authMethod);
      try {
        decodedAuthHeader = new String(Base64.getDecoder().decode(authHeaderParts[1]), "UTF-8");
      } catch (Exception err) {
        System.err.println(err);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return "";
      }
      System.out.println(decodedAuthHeader);
      String[] authParts = decodedAuthHeader.split(":");
      if (authParts.length < 2) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return "";
      }
      String username = authParts[0];
      String password = authParts[1];
      Password pwdObj = new Password(username, password);
      System.out.println(pwdObj.getDigest());
      User user = authMgr.authorize(username, pwdObj);
      if (user == null) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        return "";
      }
      System.out.println(user);

      Session session = authMgr.getSession(user);
      System.out.println(session.toString());

      response.setStatus(HttpStatus.OK.value());
//      try {
//        response.sendRedirect("/");
//      } catch (Exception err) {
//        throw new Error(err);
//      }
      Cookie sessionCookie = new Cookie("session", user.getUsername() + "#" + session.toString());
      sessionCookie.setPath("/");
      sessionCookie.setMaxAge(1000*60*60);
      response.addCookie(sessionCookie);
      return session.toString();
    }
  }
	
}
