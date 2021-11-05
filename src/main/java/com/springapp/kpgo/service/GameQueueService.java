/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.service;

import com.springapp.kpgo.core.QueueManager;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import com.springapp.kpgo.model.*;
import com.springapp.kpgo.security.AuthorizationManager;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Sovereign
 */
@RestController
public class GameQueueService {
    
    @Autowired
    private AuthorizationManager authMgr;
    
    @Autowired
    private QueueManager qMgr;
    
    @Transactional
    @RequestMapping("/queue")
    public @ResponseBody Map<String, Object> queueEndpoint(HttpServletRequest request, HttpServletResponse response) {
      User user = authMgr.authorize(request.getCookies());
      Map<String, Object> respBody = new HashMap<>();
      respBody.put("status", false);
      if (user == null) {
        LoginService.redirectToLogin(response);
        return respBody;
      }

      System.out.println("waiting in queue");
      long gameId = qMgr.enqueue(user).block();
      System.out.println("got game!");
      respBody.put("game_id", gameId);
      respBody.put("status", true);
      
      return respBody;
    }
}
