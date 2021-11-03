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
import com.springapp.kpgo.security.AuthorizationManager;
import com.springapp.kpgo.security.ResourcesManager;
import java.util.Arrays;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 *
 * @author Sovereign
 */
@Component
public class QueueManager {
  
  @Autowired
  ResourcesManager resMgr;

  private final BlockingDeque<User> playerQueue;
  private final Queue<User> serviceQueue;
  private final Set<User> usersInQueue;
  private final Flux<Resource<Game>> queueEvents;
  
  private QueueManager() {
    System.out.println("created new queue mgr");
    playerQueue = new LinkedBlockingDeque<>();
    usersInQueue = new HashSet<>();
    serviceQueue = new ConcurrentLinkedQueue();
    Flux<Resource<Game>> gameEvents = Flux.<Resource<Game>>create(sink -> {
      sink.onRequest(num -> {
        if (usersInQueue.size() < 2)
          sink.error(new Error("concurrency error"));
        
        User user1 = null;
        User user2 = null;
        try {
          user1 = serviceQueue.remove();
          user2 = serviceQueue.remove();
        } catch (NoSuchElementException err) {
          sink.error(err);
        }
        
        sink.next(startGame(user1, user2));
      });
    });
    
    queueEvents = Flux.<Resource<Game>>generate(sink -> {
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
        serviceQueue.add(user1);
        serviceQueue.add(user2);
        System.out.println("put all players to serviceQueue");
        sink.next(gameEvents.take(1).blockFirst());
      }
    }).subscribeOn(Schedulers.boundedElastic())
      .share();
    queueEvents.subscribe();
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
    } catch (ClassCastException err) {}
    
    System.out.println("started a new game");
    return gameResource;
  }
  
  public Mono<Long> enqueue(final User user) {
    System.out.println("enqueing new player, queue length is now " + playerQueue.size());
    Mono<Long> result = Mono.create(sink -> {
      System.out.println("in mono, waiting for game events");
      // queueEvents.doOnNext(gameResource -> {
      queueEvents.subscribe(gameResource -> {
        System.out.println("new game started, check if it contains current user");
        Game game = gameResource.getContent().read();
        Player[] players = game.getPlayers();
        for (Player player: players) {
          if (player.is(user)) {
            System.out.println("it does!");
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
