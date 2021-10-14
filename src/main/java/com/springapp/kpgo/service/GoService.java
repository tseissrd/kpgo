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
    
    @Autowired
    private ResourcesManager resMgr;
    
    @RequestMapping("/go/state")
    public @ResponseBody Map<String, Object> goStateEndpoint(@RequestHeader Map<String, String> headers, @RequestBody Map<String, Object> data, HttpServletRequest request, HttpServletResponse response) {
      User user = authMgr.authorize(request.getCookies());
      Map<String, Object> respBody = new HashMap<>();
      respBody.put("status", false);
      if (user == null) {
        LoginService.redirectToLogin(response);
        return respBody;
      }

      Long gameId;
      Object gameIdData = data.get("game_id");
      if (gameIdData.getClass() == Integer.class) {
        gameId = ((Integer)gameIdData).longValue();
      } else if (gameIdData.getClass() == Long.class) {
        gameId = (Long)data.get("game_id");
      } else {
        return respBody;
      }
      Resource<Game> gameResource;
      
    //~~~~
      try {
        resMgr.getResource(1L);
      } catch (NoSuchElementException err) {
        Player human = new HumanPlayer(user);
        Player computer = new ComputerPlayer();
        Game newTestGame = new Game(human, computer, 9, 9);
        Resource<Game> testGameResource = new Resource<>();
        testGameResource = resMgr.writeContent(testGameResource, newTestGame);
        System.out.println("created new game: " + testGameResource.getId());
        resMgr.giveAccess(testGameResource, user);
      }
    //~~~~
      
      try {
        gameResource = resMgr.getResource(gameId);
      } catch (NoSuchElementException err) {
        respBody.put("message", err.getLocalizedMessage());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return respBody;
      }
      
      if (!gameResource.checkAccess(user)) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return respBody;
      }
      
      Game game = gameResource.getContent().read();
      
      List<Map<String, String>> playersInfo = new ArrayList<>(2);
      for (Player player: game.getPlayers()) {
        Map<String, String> playerInfo = new HashMap<>();
        playerInfo.put("username", player.getName());
        playerInfo.put("colour", player.getBowl().colour.toString());
        playersInfo.add(playerInfo);
      }
      respBody.put("players", playersInfo);
        
      List<List<String>> jsonView = game.getTable().jsonView();
      respBody.put("table", jsonView);
      respBody.put("status", true);
      
      return respBody;
    }
    
    @RequestMapping("/go/act")
    public @ResponseBody Map<String, Object> goActEndpoint(@RequestHeader Map<String, String> headers, @RequestBody Map<String, Object> data, HttpServletRequest request, HttpServletResponse response) {
      User user = authMgr.authorize(request.getCookies());
      Map<String, Object> respBody = new HashMap<>();
      respBody.put("status", false);
      if (user == null) {
        LoginService.redirectToLogin(response);
        return respBody;
      }
      
      Long gameId;
      Object gameIdData = data.get("game_id");
      if (gameIdData.getClass() == Integer.class) {
        gameId = ((Integer)gameIdData).longValue();
      } else if (gameIdData.getClass() == Long.class) {
        gameId = (Long)data.get("game_id");
      } else {
        return respBody;
      }
      
      Map<String, Object> action = (Map<String, Object>)data.get("action");
      if ((gameId == null) || (action == null))
        return respBody;
      
      String actionType = (String)action.get("type");
      if (actionType == null)
        return respBody;
      
//      {
//      game_id: "321",
//      action: {
//        type: 'place_stone',
//        params: {
//          point: [3, 5]
//        }
//      }
//    }

      Resource<Game> gameResource;
      
      Map<String, Object> actionParams = (Map<String, Object>)action.get("params");
      if (actionParams == null)
        return respBody;
      
      List<Integer> pointParam = null;
      if (actionType.equals("place_stone")) {
        pointParam = (List<Integer>)actionParams.get("point");
        if (pointParam == null)
          return respBody;
      }
      
      try {
        gameResource = resMgr.getResource(gameId);
      } catch (NoSuchElementException err) {
        respBody.put("message", err.getLocalizedMessage());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return respBody;
      }
      
      if (!gameResource.checkAccess(user)) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return respBody;
      }
      
      Game game = gameResource.getContent().read();
      Player player = null;
      
      for (Player tempPlayer: game.getPlayers()) {
        if (tempPlayer.is(user)) {
          player = tempPlayer;
          System.out.println("detected that player " + tempPlayer.getName() + " is user " + user.getUsername());
          break;
        }
      }
      
      if (player != null) {
        if (actionType.equals("place_stone")) {
          System.out.println("x: " + pointParam.get(0));
          System.out.println("y: " + pointParam.get(1));
          player.takeTurn(game.getTable().getPoint(pointParam.get(0), pointParam.get(1)));
          resMgr.writeContent(gameResource, game);
          respBody.put("status", true);
          return respBody;
        }
        
        if (actionType.equals("pass")) {
          player.pass();
          resMgr.writeContent(gameResource, game);
          respBody.put("status", true);
          return respBody;
        }
      }
      
      return respBody;
    }
    
}
