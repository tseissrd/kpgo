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
import com.springapp.kpgo.model.User;
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

  private final BlockingDeque<User> playerQueue;
  private final Set<User> usersInQueue;
  private final Flux<Resource<Game>> gameEvents;
  
  private QueueManager() {
    System.out.println("created new queue mgr");
    playerQueue = new LinkedBlockingDeque<>();
    usersInQueue = new HashSet<>();
    gameEvents = Flux.create(sink -> {
      (new Thread("game queue thread") {
        @Override
        public void run() {
          System.out.println("started queue thread");
          while (true) {
            User user1 = null;
            User user2 = null;

            try {
              System.out.println("waiting for first player from the queue");
              user1 = playerQueue.take();
            } catch (InterruptedException err) {
              sink.error(err);
            }

            System.out.println("took first player from the queue");
            try {
              System.out.println("waiting for second player from the queue");
              user2 = playerQueue.take();
              usersInQueue.remove(user1);
              usersInQueue.remove(user2);
            } catch (InterruptedException err) {
              try {
                playerQueue.putFirst(user1);
              } catch (InterruptedException err2) {
                sink.error(err2);
              }
              sink.error(err);
            }

            System.out.println("took second player from the queue");
            sink.next(startGame(user1, user2));
          }
        }
      }).start();
    });
    gameEvents.subscribe();
  }
  
  private Resource<Game> startGame(final User user1, final User user2) {
    Player player1 = new HumanPlayer(user1);
    Player player2 = new HumanPlayer(user2);
    Game game = new Game(player1, player2, 19, 19);
    Resource<Game> gameResource = resMgr.writeContent(new Resource<>(), game);
    System.out.println("new game id is " + gameResource.getId());
    try {
      System.out.println("giving access to " + user1.getUsername());
      resMgr.giveAccess(gameResource, user1);
    } catch (ClassCastException err) {}
    try {
      System.out.println("giving access to " + user2.getUsername());
      resMgr.giveAccess(gameResource, user2);
      // resMgr.giveAccess(gameResource, user2);
    } catch (ClassCastException err) {}
    
    System.out.println("started a new game");
    return gameResource;
  }
  
  public Mono<Long> enqueue(final User user) {
    System.out.println("enqueing new player, queue length is now " + playerQueue.size());
    Mono<Long> result = Mono.create(sink -> {
      gameEvents.doOnNext(gameResource -> {
        Game game = gameResource.getContent().read();
        Player[] players = game.getPlayers();
        for (Player player: players) {
          if (player.is(user)) {
            sink.success(gameResource.getId());
          }
        }
      });
    });
    
    try {
      usersInQueue.forEach(user1 -> System.out.println(user1.getUsername()));
      usersInQueue.forEach(user1 -> System.out.println(user.equals(user1)));
      System.out.println(usersInQueue.contains(user));
      if (usersInQueue.add(user)) {
        playerQueue.put(user);
      }
    } catch (InterruptedException err) {
      return Mono.error(err);
    }
    return result;
  }
  
}
