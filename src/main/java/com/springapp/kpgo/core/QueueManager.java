/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.core;

import com.springapp.kpgo.go.Player;
import com.springapp.kpgo.model.Resource;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import org.springframework.stereotype.Component;

/**
 *
 * @author Sovereign
 */
@Component
public class QueueManager {

  private final BlockingQueue<Player> playerQueue = new SynchronousQueue<>();
  private final ExecutorService executor = Executors.newFixedThreadPool(10);
  private Player nextPlayer;
  
  private QueueManager() {
    System.out.println("created new queue mgr");
  }
  
  Future<Long> enqueue(final Player player) {
    return executor.submit(() -> {
      if (playerQueue.peek() == null) {
        playerQueue.put(player);
      }
      return 123L;
    });
  }
  
}
