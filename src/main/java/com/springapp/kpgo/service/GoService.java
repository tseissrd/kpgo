/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.service;

import com.springapp.kpgo.go.Game;
import com.springapp.kpgo.go.Player;
import com.springapp.kpgo.go.TablePoint;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;
import com.springapp.kpgo.model.*;
import com.springapp.kpgo.security.AccessForbiddenException;
import com.springapp.kpgo.security.AuthorizationManager;
import com.springapp.kpgo.security.ResourcesManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 *
 * @author Sovereign
 */
@RestController
public class GoService {
    
    @Autowired
    private AuthorizationManager authMgr;
    
    @Autowired
    private ResourcesManager resMgr;
    
    public Resource<Game> getGameResource(User user, Map<String, Object> requestBody)
    throws InvalidRequestException, GameNotFoundException, AccessForbiddenException
    {
      Long gameId;
      Object gameIdData = requestBody.get("game_id");
      if (gameIdData == null)
        throw new InvalidRequestException();
      
      if (gameIdData.getClass() == Integer.class) {
        gameId = ((Integer)gameIdData).longValue();
      } else if (gameIdData.getClass() == Long.class) {
        gameId = (Long)requestBody.get("game_id");
      } else {
        throw new InvalidRequestException();
      }
      Resource<Game> gameResource;
      
      try {
        gameResource = resMgr.getResource(gameId);
      } catch (NoSuchElementException err) {
        throw new GameNotFoundException();
      }
      
      if (!gameResource.checkAccess(user))
        throw new AccessForbiddenException();
      
      return gameResource;
    }
    
    @Transactional
    @RequestMapping("/go/state")
    public @ResponseBody Map<String, Object> goStateEndpoint(@RequestHeader Map<String, String> headers, @RequestBody Map<String, Object> data, HttpServletRequest request, HttpServletResponse response) {
      Map<String, Object> respBody = new HashMap<>();
      respBody.put("status", false);
      
      User user = authMgr.authorize(request.getCookies());
      if (user == null) {
        LoginService.redirectToLogin(response);
        return respBody;
      }
      
      Resource<Game> gameResource;
      try {
        gameResource = getGameResource(user, data);
      } catch (InvalidRequestException ex) {
        return respBody;
      } catch (GameNotFoundException ex) {
        respBody.put("message", ex.getLocalizedMessage());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return respBody;
      } catch (AccessForbiddenException ex) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return respBody;
      }
      
      Game game = gameResource.getContent().read();
      
      List<Map<String, Object>> playersInfo = new ArrayList<>(2);
      for (Player player: game.getPlayers()) {
        Map<String, Object> playerInfo = new HashMap<>();
        playerInfo.put("username", player.getName());
        playerInfo.put("colour", player.getBowl().colour.toString());
        playerInfo.put("you", player.is(user));
        if (game.getWinner() == null)
          playerInfo.put("winner", false);
        else
          playerInfo.put("winner", game.getWinner().equals(player));
        playersInfo.add(playerInfo);
      }
      respBody.put("players", playersInfo);
        
      List<List<String>> jsonView = game.getTable().jsonView();
      respBody.put("table", jsonView);
      respBody.put("act", game.playerToAct().is(user));
      respBody.put("ended", game.isEnded());
      respBody.put("status", true);
      
      return respBody;
    }
    
    @Transactional
    @RequestMapping("/go/act")
    public @ResponseBody Map<String, Object> goActEndpoint(@RequestHeader Map<String, String> headers, @RequestBody Map<String, Object> data, HttpServletRequest request, HttpServletResponse response) {
      Map<String, Object> respBody = new HashMap<>();
      respBody.put("status", false);
      
      User user = authMgr.authorize(request.getCookies());
      if (user == null) {
        LoginService.redirectToLogin(response);
        return respBody;
      }
      
      Map<String, Object> action = (Map<String, Object>)data.get("action");
      if (action == null)
        return respBody;
      
      String actionType = (String)action.get("type");
      if (actionType == null)
        return respBody;
      
      Map<String, Object> actionParams = (Map<String, Object>)action.get("params");
      if (actionParams == null)
        return respBody;
      
      List<Integer> pointParam = null;
      if (actionType.equals("place_stone")) {
        pointParam = (List<Integer>)actionParams.get("point");
        if (pointParam == null)
          return respBody;
      }
      
      Resource<Game> gameResource;
      try {
        gameResource = getGameResource(user, data);
      } catch (InvalidRequestException ex) {
        return respBody;
      } catch (GameNotFoundException ex) {
        respBody.put("message", ex.getLocalizedMessage());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return respBody;
      } catch (AccessForbiddenException ex) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return respBody;
      }
      
      Game game = gameResource.getContent().read();
      if (game.isEnded())
        return respBody;
      
      Player player = null;
      
      for (Player tempPlayer: game.getPlayers()) {
        if (tempPlayer.is(user)) {
          player = tempPlayer;
          break;
        }
      }
      
      if ((player != null) && (game.playerToAct() == player)) {
        if (actionType.equals("place_stone")) {
          TablePoint point = game.getTable().getPoint(pointParam.get(0), pointParam.get(1));
          if (game.checkMove(player.getBowl().colour, point))
            player.takeTurn(point);
          else
            return respBody;
          game.nextMove();
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
