/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.service;

import com.springapp.kpgo.go.ComputerPlayer;
import com.springapp.kpgo.go.Game;
import com.springapp.kpgo.go.HumanPlayer;
import com.springapp.kpgo.go.Player;
import com.springapp.kpgo.go.Table;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;
import com.springapp.kpgo.repository.DataRepository;
import com.springapp.kpgo.model.*;
import com.springapp.kpgo.security.AuthorizationManager;
import com.springapp.kpgo.security.ResourcesManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Sovereign
 */
@RestController
public class GameQueueService {
    
    @Autowired
    private DataRepository repository;
    
    @Autowired
    private AuthorizationManager authMgr;
    
    @Autowired
    private ResourcesManager resMgr;
    
    
    
    @RequestMapping("/queue")
    public @ResponseBody Map<String, Object> queueEndpoint(@RequestHeader Map<String, String> headers, @RequestBody Map<String, Object> data, HttpServletRequest request, HttpServletResponse response) {
      User user = authMgr.authorize(request.getCookies());
      Map<String, Object> respBody = new HashMap<>();
      respBody.put("status", false);
      if (user == null) {
        LoginService.redirectToLogin(response);
        return respBody;
      }

      
      
      return respBody;
    }
    
}
