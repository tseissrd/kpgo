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
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
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
  private ResourcesManager resMgr;
  
  @Autowired
  private EntityManagerFactory emf;

  private final BlockingDeque<User> playerQueue;
  private final Set<User> usersInQueue;
  private Flux<Resource<Game>> queueEvents;
  
  private QueueManager() {
    System.out.println("created new queue mgr");
    playerQueue = new LinkedBlockingDeque<>();
    usersInQueue = new HashSet<>();
    initGameGenerator();
  }
  
  private void initGameGenerator() {
    queueEvents = Flux.<Resource<Game>>create(sink -> {
      System.out.println("started queue thread");
      while (true) {
        // EntityManager eMgr = emf.createEntityManager();
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
    resMgr.giveAccess(gameResource, Arrays.asList(user1, user2));
    
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
