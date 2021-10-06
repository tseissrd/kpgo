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
import com.springapp.kpgo.security.AuthorizationManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Sovereign
 */
@RestController
public class GoService {
    
    @Autowired
    private DataRepository repository;
    
    @Autowired
    private AuthorizationManager authMgr;
    
    @RequestMapping("/go/act")
    public @ResponseBody String goActEndpoint(@RequestHeader Map<String, String> headers, @RequestBody Map<String, Object> data, HttpServletRequest request, HttpServletResponse response) {
      User user = authMgr.authorize(request.getCookies());
      if (user == null) {
        try {
          response.sendRedirect("/");
        } catch (Exception err) {
          throw new Error(err);
        }
        return "";
      }
      
      return "ok";
    }
    
}
