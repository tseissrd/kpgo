/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.core;

import com.springapp.kpgo.go.Game;
import com.springapp.kpgo.go.HumanPlayer;
import com.springapp.kpgo.go.Player;
import com.springapp.kpgo.model.Resource;
import com.springapp.kpgo.security.ResourcesManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @author Sovereign
 */
@Component
public class QueueManager {
  
  @Autowired
  ResourcesManager resMgr;

  private final BlockingDeque<Player> playerQueue;
  private final ExecutorService executor;
  private final Set<Player> playersInQueue;
  private final Flux<Resource<Game>> gameEvents;
  
  private QueueManager() {
    System.out.println("created new queue mgr");
    playerQueue = new LinkedBlockingDeque<>();
    executor = Executors.newCachedThreadPool();
    playersInQueue = new HashSet();
    gameEvents = Flux.create(sink -> {
      new Thread(() -> {
        while (true) {
          Player player1 = null;
          Player player2 = null;

          try {
            player1 = playerQueue.take();
            playersInQueue.remove(player1);
          } catch (InterruptedException err) {
            sink.error(err);
          }

          try {
            player2 = playerQueue.take();
            playersInQueue.remove(player2);
          } catch (InterruptedException err) {
            try {
              playersInQueue.add(player1);
              playerQueue.putFirst(player1);
            } catch (InterruptedException err2) {
              sink.error(err2);
            }
            sink.error(err);
          }

          Game game = new Game(player1, player2, 19, 19);
          sink.next(startGame(player1, player2));
        }
      }).run();
    });
  }
  
  private Resource<Game> startGame(Player player1, Player player2) {
    Game game = new Game(player1, player2, 19, 19);
    Resource<Game> gameResource = resMgr.writeContent(new Resource<>(), game);
    try {
      resMgr.giveAccess(gameResource, ((HumanPlayer)player1).getUser());
    } catch (ClassCastException err) {}
    try {
      resMgr.giveAccess(gameResource, ((HumanPlayer)player2).getUser());
    } catch (ClassCastException err) {}
    
    System.out.println("started a new game");
    return gameResource;
  }
  
  public Mono<Long> enqueue(final Player player) {
    System.out.println("enqueued new player, queue length is now " + playerQueue.size());
    Mono<Long> result = Mono.create(sink -> {
      gameEvents.doOnNext(gameResource -> {
        Game game = gameResource.getContent().read();
        if (Arrays.asList(game.getPlayers()).contains(player)) {
          sink.success(gameResource.getId());
        }
      });
    });
    
    try {
      if (!playersInQueue.contains(player))
        playerQueue.put(player);
    } catch (InterruptedException err) {
      return Mono.error(err);
    }
    return result;
  }
  
}
