/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.service;

import com.springapp.kpgo.core.QueueManager;
import com.springapp.kpgo.go.HumanPlayer;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import com.springapp.kpgo.repository.DataRepository;
import com.springapp.kpgo.model.*;
import com.springapp.kpgo.security.AuthorizationManager;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

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
    private QueueManager qMgr;
    
    @RequestMapping("/queue")
    public @ResponseBody Map<String, Object> queueEndpoint(@RequestHeader Map<String, String> headers, @RequestBody Map<String, Object> data, HttpServletRequest request, HttpServletResponse response) {
      User user = authMgr.authorize(request.getCookies());
      Map<String, Object> respBody = new HashMap<>();
      respBody.put("status", false);
      if (user == null) {
        LoginService.redirectToLogin(response);
        return respBody;
      }

      long gameId = qMgr.enqueue(new HumanPlayer(user)).block();
      respBody.put("game_id", gameId);
      respBody.put("status", true);
      
      return respBody;
    }
}
