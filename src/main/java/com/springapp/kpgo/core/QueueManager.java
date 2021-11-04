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
import java.util.concurrent.LinkedBlockingDeque;
import javax.persistence.EntityManagerFactory;
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
        User user1 = null;
        User user2 = null;

        try {
          user1 = playerQueue.take();
        } catch (InterruptedException err) {
          sink.error(err);
        }

        try {
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
    resMgr.giveAccess(gameResource, Arrays.asList(user1, user2));
    
    System.out.println("started a new game");
    return gameResource;
  }
  
  public Mono<Long> enqueue(final User user) {
    Disposable queueSub;
    Mono<Long> result = Mono.create(sink -> {
      queueSub = queueEvents.subscribe(gameResource -> {
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
    
    result.doOnSuccess(() -> queueSub.dispose());
    
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
