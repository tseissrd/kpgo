/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.springapp.kpgo.go;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;

/**
 *
 * @author Sovereign
 */
public class Game
implements Runnable, Serializable
{
  
  private final Player[] players;
  private final Table table;
  private boolean ended;
  private Player winner;
  private Player actsNext;
  
  protected Game() {
    players = null;
    table = null;
  }
  
  public Game(Player player1, Player player2, int tableWidth, int tableHeight) {
    players = new Player[2];
    players[0] = player1;
    players[1] = player2;
    
    table = new Table(tableWidth, tableHeight);
    int whiteBowlPlayerNumber = new Random(Calendar.getInstance().toInstant().getEpochSecond()).nextInt(2);
    players[whiteBowlPlayerNumber].assignBowl(new Bowl(Colour.WHITE));
    players[1 - whiteBowlPlayerNumber].assignBowl(new Bowl(Colour.BLACK));
    actsNext = players[whiteBowlPlayerNumber];
    winner = null;
    ended = false;
  }
  
  public Player[] getPlayers() {
    return Arrays.copyOf(players, 2);
  }
  
  public Table getTable() {
    return table;
  }
  
  public Player playerToAct() {
    return actsNext;
  }
  
  public void nextMove() {
    if (actsNext == players[0])
      actsNext = players[1];
    else
      actsNext = players[0];
  }

  @Override
  public void run() {
    while (true) {
      for (Player player: players) {
        if (player.passed) {
          ended = true;
          break;
        }
        
      }
      
      if (ended)
        break;
    }
  }
  
}
